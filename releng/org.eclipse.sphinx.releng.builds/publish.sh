#!/bin/sh

# Script may take 5-6 command line parameters:
# $1: Hudson job name: <name>
# $2: Hudson build id: <id>
# $3: Build type: i(ntegration), s(table), r(elease), t(est)
# $4: Whether to promote to an update-site: (y)es, (n)o
# $5: Whether to merge the site with an existing one: (y)es, (n)o
# $6: Release stream: <major>.<minor>.x, e.g., 0.7.x (only required if build type is release, ignored otherwise)
#

# Global settings
buildJobsPath=/shared/jobs
eclipseDownloadsPath=/home/data/httpd/download.eclipse.org
projectUpdateSitesBasePath=sphinx/updates

if [ $# -eq 5 -o $# -eq 6 ];
        then
                jobName=$1
                buildId=$2
                buildType=$3
                site=$4
                merge=$5
                if [ "$3"=r -o "$3"=R];
                then
                       releaseStream=$6
                fi
        else
                if [ $# -ne 0 ];
                then
                        exit 1
                fi
fi

if [ -z "$jobName" ];
then
        echo -n "Please enter the name of the Hudson job you want to promote:"
        read jobName
fi

if [ -z "$buildId" ];
then
        for i in $( find $buildJobsPath/$jobName/builds -type l | sed 's!.*/!!' | sort)
        do
                echo -n "$i, "
        done
        echo "lastStable, lastSuccessful"
        echo -n "Please enter the id/label of the Hudson build you want to promote:"
        read buildId
fi
if [ -z "$buildId" ];
        then
                exit 0
fi

# Determine the build we want to publish
if [ "$buildId" = "lastStable" -o "$buildId" = "lastSuccessful" ];
        then
                jobDir=$(readlink -f $buildJobsPath/$jobName/$buildId)
        else
                jobDir=$(readlink -f $buildJobsPath/$jobName/builds/$buildId)
fi
localUpdateSite=$jobDir/archive/update-site
echo "Using local update-site: $localUpdateSite"

# Reverse lookup the build id (in case lastSuccessful or lastStable was used)
for i in $(find $buildJobsPath/$jobName/builds/ -type l)
do
        if [ "$(readlink -f $i)" =  "$jobDir" ];
                then
                        buildId=${i##*/}
        fi
done
echo "Reverse lookup of build id yielded: $buildId"

# Select the build type
if [ -z "$buildType" ];
then
        echo -n "Please select which type of build you want to publish to [i(ntegration), m(aintenance), s(table), r(elease), t(est)]: "
        read buildType
fi
echo "Publishing as $buildType build"

# check if we are going to promote to an update-site
if [ -z "$site" ];
        then
                echo -n "Do you want to promote to an remote update site? [(y)es, (n)o]:"
                read site
fi
if [ "$site" != y -a "$site" != n ];
        then
                exit 0
fi
echo "Promoting to remote update site: $site"

# Select the Release stream
if [ -z "$releaseStream" ];
        then
        	if ["$buildType"=r -o "$buildType"=R];
                then
                	echo -n "Please enter a release stream you want to publish, <major>.<minor>.x, e.g., 0.7.x:"
                	read releaseStream
            fi
fi

if [ "$site" = y ];
        then

  # Determine remote update site we want to promote to (integration and stable builds are published on interim update site, release builds on applicable release stream update site)
  case $buildType in
        i|I|s|S) selectedUpdateSiteName=interim;;
        r|R) selectedUpdateSiteName=$releaseStream/releases;;
        t|T) selectedUpdateSiteName=test;;
        *) exit 0 ;;
  esac
  selectedUpdateSiteRelativePath="$projectUpdateSitesBasePath/$selectedUpdateSiteName"
  selectedUpdateSiteAbsolutePath="$eclipseDownloadsPath/$selectedUpdateSiteRelativePath"
  echo "Publishing to remote update-site: $selectedUpdateSiteAbsolutePath"

  if [ -d "$selectedUpdateSiteAbsolutePath" ];
        then
                if [ -z "$merge" ];
                then
                        echo -n "Do you want to merge with the existing update-site? [(y)es, (n)o]:"
                        read merge
                fi
                if [ "$merge" != y -a "$merge" != n ];
                        then
                        exit 0
                fi
        else
                merge=n
  fi
  echo "Merging with existing site: $merge"
fi

# Prepare a temp directory
tmpDir="$jobName-publish-tmp"
rm -fr $tmpDir
mkdir -p $tmpDir/update-site
cd $tmpDir

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository
echo "Downloading eclipse to $PWD"
cp /home/data/httpd/download.eclipse.org/eclipse/downloads/drops4/R-4.2.2-201302041200/eclipse-SDK-4.2.2-linux-gtk-x86_64.tar.gz .
tar -xvzf eclipse-SDK-4.2.2-linux-gtk-x86_64.tar.gz
cd eclipse
chmod 700 eclipse
cd ..
if [ ! -d "eclipse" ];
        then
                echo "Failed to download an Eclipse SDK, being needed for provisioning."
                exit
fi
# Prepare Eclipse SDK to provide WTP releng tools (used to postprocess repository, i.e set p2.mirrorsURL property)
echo "Installing WTP Releng tools"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/webtools/releng/repository/ -installIUs org.eclipse.wtp.releng.tools.feature.feature.group
# Clean up
echo "Cleaning up"
rm eclipse-SDK-4.2.2-linux-gtk-x86_64.tar.gz

# Prepare local update site (merging is performed later, if required)
cp -R $localUpdateSite/* update-site/
echo "Copied $localUpdateSite to local directory update-site."

if [ "$site" = y ];
        then
  if [ "$merge" = y ];
        then
        echo "Merging existing site into local one."
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:update-site
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:update-site
        echo "Merged $selectedUpdateSiteAbsolutePath into local directory update-site."
  fi

  # Ensure p2.mirrorURLs property is used in update site
  echo "Setting p2.mirrorsURL to http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$PWD/update-site -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath"

  # Create p2.index file
  if [ ! -e "update-site/p2.index" ];
        then
                echo "Creating p2.index file."
                echo "version = 1" > update-site/p2.index
                echo "metadata.repository.factory.order = content.xml,\!" >> update-site/p2.index
                echo "artifact.repository.factory.order = artifacts.xml,\!" >> update-site/p2.index
  fi

  # Backup then clean remote update site
  echo "Creating backup of remote update site."
  if [ -d "$selectedUpdateSiteAbsolutePath" ];
        then
                if [ -d BACKUP ];
                        then
                                rm -fr BACKUP
                fi
                mkdir BACKUP
                cp -R $selectedUpdateSiteAbsolutePath/* BACKUP/
                rm -fr $selectedUpdateSiteAbsolutePath
  fi

  echo "Publishing contents of local update-site directory to remote update site $selectedUpdateSiteAbsolutePath"
  mkdir -p $selectedUpdateSiteAbsolutePath
  cp -R update-site/* $selectedUpdateSiteAbsolutePath/
fi


# Clean up
echo "Cleaning up"
rm -fr eclipse
rm -fr update-site
