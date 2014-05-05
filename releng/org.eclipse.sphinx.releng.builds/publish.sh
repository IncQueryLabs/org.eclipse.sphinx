#!/bin/sh

# Script may take 5-6 command line parameters:
# $1: Hudson job name: <name>
# $2: Hudson build id: <id>
# $3: Build type: i(ntegration), s(table), r(elease), t(est)
# $4: Whether to promote to an update-site: (y)es, (n)o
# $5: Whether to merge the site with an existing one: (y)es, (n)o
# $6: Release stream: <major>.<minor>.x, e.g., 0.7.x (only required if build type is release, ignored otherwise)

# Global settings
projectUpdateSitesBasePath=sphinx/updates
eclipseDownloadsPath=/home/data/httpd/download.eclipse.org
eclipsePackage=eclipse-SDK-4.2.2-linux-gtk-x86_64.tar.gz
eclipsePackagePath=$eclipseDownloadsPath/eclipse/downloads/drops4/R-4.2.2-201302041200

localUpdateSite=${WORKSPACE}/artifacts
echo "Local update-site: $localUpdateSite"

rm -rf $localUpdateSite
wget --mirror --execute robots=off --directory-prefix=$localUpdateSite --no-host-directories --cut-dirs=11 --no-parent --reject="index.html*,*zip*" --timestamping $TARGET_BUILD_RUN/artifact/releng/org.eclipse.sphinx.releng.builds/repository/target/repository/

# check if we are going to promote to an update-site
echo "Promoting to remote update site: $SITE"
echo "Merging with existing site: $MERGE"

# Select the Release stream
if [ "$BUILD_TYPE" = R ];
     then
          releaseStream=0.8.x
          echo "Release Stream: 0.8.x"
fi

if [ "$SITE" = y ];
        then

  # Determine remote update site we want to promote to (integration and stable builds are published on interim update site, release builds on applicable release stream update site)
  case $BUILD_TYPE in
        I|S) selectedUpdateSiteName=interim;;
        R) selectedUpdateSiteName=$releaseStream/releases;;
        T) selectedUpdateSiteName=test;;
        *) exit 0 ;;
  esac
  selectedUpdateSiteRelativePath="$projectUpdateSitesBasePath/$selectedUpdateSiteName"
  selectedUpdateSiteAbsolutePath="$eclipseDownloadsPath/$selectedUpdateSiteRelativePath"
  echo "Publishing to remote update-site: $selectedUpdateSiteAbsolutePath"
fi

# Prepare a temp directory
tmpDir="$BUILD_JOB_NAME-publish-tmp"
rm -fr $tmpDir
mkdir -p $tmpDir/update-site
cd $tmpDir

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository
if [ ! -d "eclipse" ];
	then
		echo "Downloading eclipse to $PWD"
		cp $eclipsePackagePath/$eclipsePackage .
		tar -xvzf $eclipsePackage
		cd eclipse
		chmod 700 eclipse
		cd ..
		if [ ! -d "eclipse" ];
        	then
                echo "Failed to download an Eclipse SDK, being needed for provisioning."
                exit
		fi
fi

# Prepare Eclipse SDK to provide WTP releng tools (used to postprocess repository, i.e set p2.mirrorsURL property)
echo "Installing WTP Releng tools"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/webtools/releng/repository/ -installIUs org.eclipse.wtp.releng.tools.feature.feature.group
# Clean up
echo "Cleaning up"
rm $eclipsePackage


# Prepare local update site (merging is performed later, if required)
cp -R $localUpdateSite/* update-site/
echo "Copied $localUpdateSite to local directory update-site."

if [ "$SITE" = y ];
        then
  if [ "$MERGE" = y ];
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
