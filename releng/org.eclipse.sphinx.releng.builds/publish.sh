#!/bin/sh

# Script may take 5-6 command line parameters:
# $1: Hudson job name: <name>
# $2: Hudson build id: <id>
# $3: Build type: i(ntegration), s(table), r(elease), t(est)
# $4: Whether to promote to an update-site: (t)rue, (f)alse
# $5: Whether to merge the site with an existing one: (t)rue, (f)alse
# $6: Release stream: <major>.<minor>.x, e.g., 0.7.x (only required if build type is release, ignored otherwise)

# Global settings
projectUpdateSitesBasePath=sphinx/updates
projectDownloadsBasePath=sphinx/downloads
eclipseDownloadsPath=/home/data/httpd/download.eclipse.org
eclipsePackageVersion=4.2.2
eclipsePackageTimestamp=201302041200
eclipsePackagePath=$eclipseDownloadsPath/eclipse/downloads/drops4/R-$eclipsePackageVersion-$eclipsePackageTimestamp
eclipsePackageFileName=eclipse-SDK-$eclipsePackageVersion-linux-gtk-x86_64.tar.gz
relengProjectRelativePath=releng/org.eclipse.sphinx.releng.builds
relengProjectPath=${WORKSPACE}/$relengProjectRelativePath
localUpdateSitePath=$relengProjectPath/artifacts
buildEclipsePath=$relengProjectPath/eclipse
updateZipFileNamePrefix=sphinx-Update-0.8.0.
# The time stamp of the target build: yyyymmdd-hhmm
updateZipFileTimestamp=$TARGET_BUILD_TIME_STAMP

case $BUILD_TYPE in
        I|S) updateZipType=I;;
        S) updateZipType=M;;
        R) updateZipType=R;;
        T) updateZipType=T;;
        *) exit 0 ;;
esac
updateZipFileName=$updateZipFileNamePrefix$updateZipType$updateZipFileTimestamp.zip

# check if we are going to promote to an update-site
echo "Promoting to remote update site: $SITE"
echo "Merging with existing site: $MERGE"
echo "Local update-site: $localUpdateSitePath"
echo "Local update-site zip file name: $updateZipFileName"

rm -rf $localUpdateSitePath
wget --mirror --execute robots=off --directory-prefix=$localUpdateSitePath --no-host-directories --cut-dirs=11 --no-parent --reject="index.html*,*zip*" --timestamping $TARGET_BUILD_RUN/artifact/releng/org.eclipse.sphinx.releng.builds/repository/target/repository/

# Select the Release stream
if [ "$BUILD_TYPE" = R ];
     then
          releaseStream=0.8.x
          echo "Release Stream: 0.8.x"
fi

if [ $SITE ];
        then

  # Determine remote update site we want to promote to (integration and stable builds are published on interim update site, release builds on applicable release stream update site)
  case $BUILD_TYPE in
        I|S) selectedUpdateSiteName=interim
        	 selectedDownloadPath=interim
        	 ;;
        R) selectedUpdateSiteName=releases/$releaseStream
           selectedDownloadPath=releases/$releaseStream
           ;;
        T) selectedUpdateSiteName=test
           selectedDownloadPath=test
           ;;
        *) exit 0 ;;
  esac
  selectedUpdateSiteRelativePath="$projectUpdateSitesBasePath/$selectedUpdateSiteName"
  selectedUpdateSiteAbsolutePath="$eclipseDownloadsPath/$selectedUpdateSiteRelativePath"
  echo "Publishing to remote update-site: $selectedUpdateSiteAbsolutePath"

  selectedDownloadRelativePath="$projectDownloadsBasePath/$selectedDownloadPath"
  selectedDownloadAbsolutePath="$eclipseDownloadsPath/$selectedDownloadRelativePath"
  echo "Publishing to remote downloads: $selectedDownloadAbsolutePath"

  # zip the local update site, and copy to downloads
  echo "Zip and copy the local update site to downloads"
  cd $relengProjectRelativePath
  cd artifacts
  zip -r $updateZipFileName .
  echo "cp $updateZipFileName $selectedDownloadAbsolutePath"
  cp $updateZipFileName $selectedDownloadAbsolutePath
  cd ..
fi

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository
if [ ! -d "eclipse" ];
	then
		echo "Downloading eclipse to $PWD"
		cp $eclipsePackagePath/$eclipsePackageFileName $relengProjectPath
		tar -xvzf $eclipsePackageFileName
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
rm $eclipsePackageFileName

if [ $SITE ];
        then
  if [ $MERGE ];
        then
        echo "Merging existing site into local one."
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:artifacts
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:artifacts
        echo "Merged $selectedUpdateSiteAbsolutePath into local update site directory : artifacts."
  fi

  # Ensure p2.mirrorURLs property is used in update site
  echo "Setting p2.mirrorsURL to http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath (see https://wiki.eclipse.org/WTP/Releng/Tools/addRepoProperties for details)"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$PWD/artifacts -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath"

  # Create p2.index file
  if [ ! -e "artifacts/p2.index" ];
        then
                echo "Creating p2.index file."
                echo "version = 1" > artifacts/p2.index
                echo "metadata.repository.factory.order = content.xml,\!" >> artifacts/p2.index
                echo "artifact.repository.factory.order = artifacts.xml,\!" >> artifacts/p2.index
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
                echo "copy $selectedUpdateSiteAbsolutePath/* to BACKUP"
                cp -R $selectedUpdateSiteAbsolutePath/* BACKUP/
                rm -fr $selectedUpdateSiteAbsolutePath
  fi

  echo "Publishing contents of local update-site directory to remote update site $selectedUpdateSiteAbsolutePath"
  mkdir -p $selectedUpdateSiteAbsolutePath
  echo "copy artifacts/* to $selectedUpdateSiteAbsolutePath/* "
  cp -R artifacts/* $selectedUpdateSiteAbsolutePath/
  rm -f artifacts/*.zip
  #rsync -rv --exclude=sphinx-Updated-*.zip artifacts/* $selectedUpdateSiteAbsolutePath/
  #cd artifacts
  #find ./ ! -name "sphinx-Updated-*.zip" | xargs -i cp --parents {} $selectedUpdateSiteAbsolutePath/
fi

