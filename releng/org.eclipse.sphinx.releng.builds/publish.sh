#!/bin/sh

# $1: Build type:  I(ntegration), M(ilestone), R(elease)C(andidate) R(elease), T(est)
# $2: Whether to merge the site with an existing one: (t)rue, (f)alse
# $3: Release stream: <major>.<minor>.x, e.g., 0.8.x (only required if build type is release, ignored otherwise)

# Global settings
projectUpdateSitesBasePath=sphinx/updates
projectDownloadsBasePath=sphinx/downloads

eclipseDownloadsPath=/home/data/httpd/download.eclipse.org
eclipsePackageVersion=4.2.2
eclipsePackageTimestamp=201302041200
eclipsePackagePath=$eclipseDownloadsPath/eclipse/downloads/drops4/R-$eclipsePackageVersion-$eclipsePackageTimestamp
eclipsePackageFileName=eclipse-SDK-$eclipsePackageVersion-linux-gtk-x86_64.tar.gz

targetBuildRelativePath=$(echo "$TARGET_BUILD_RUN" | grep -o '/[^/]\+/[0-9]\+/$')
relengProjectRelativePath=releng/org.eclipse.sphinx.releng.builds
originalArtifactsRelativePath=artifact/$relengProjectRelativePath/repository/target/repository/
originalArtifactsPath=${WORKSPACE}/../../$targetBuildRelativePath/$originalArtifactsRelativePath
localRelengProjectPath=${WORKSPACE}/$relengProjectRelativePath
localArtifactsDirectoryName=artifacts
localArtifactsPath=$localRelengProjectPath/$localArtifactsDirectoryName

buildEclipsePath=$localRelengProjectPath/eclipse
releaseStreamPrefix=0.8
updateZipFileNamePrefix=sphinx-Update-$releaseStreamPrefix.0

echo "Copying $originalArtifactsPath/* to $localArtifactsPath/*"
rm -rf $localArtifactsPath
cp -R $originalArtifactsPath/* $localArtifactsPath/

# Alternative approach: download build artifacts rather than copying them
# rm -rf $localArtifactsPath
# wget --mirror --execute robots=off --directory-prefix=$localArtifactsPath --no-host-directories --cut-dirs=11 --no-parent --reject="index.html*,*zip*" --timestamping $TARGET_BUILD_RUN/artifact/releng/org.eclipse.sphinx.releng.builds/repository/target/repository/

# Determine remote update site we want to promote to (integration and stable builds are published on interim update site, release builds on applicable release stream update site)
case $TARGET_BUILD_TYPE in
        I) selectedUpdateSiteName=interim
           selectedDownloadPath=integration
           updateZipFileName=$updateZipFileNamePrefix.$TARGET_BUILD_TYPE$PUBLISH_ID.zip
           ;;
        M|RC) selectedUpdateSiteName=interim
        	  selectedDownloadPath=stable
        	  updateZipFileName=$updateZipFileNamePrefix$TARGET_BUILD_TYPE$PUBLISH_ID.zip
        	  ;;
        R) selectedUpdateSiteName=releases/$releaseStreamPrefix.x
           selectedDownloadPath=releases/$releaseStreamPrefix.0
           updateZipFileName=$updateZipFileNamePrefix.zip
           ;;
        T) selectedUpdateSiteName=test
           selectedDownloadPath=test
           updateZipFileName=$updateZipFileNamePrefix.$TARGET_BUILD_TYPE$PUBLISH_ID.zip
           ;;
        *) exit 0 ;;
esac

echo "Local update-site zip file name: $updateZipFileName"
selectedUpdateSiteRelativePath="$projectUpdateSitesBasePath/$selectedUpdateSiteName"
selectedUpdateSiteAbsolutePath="$eclipseDownloadsPath/$selectedUpdateSiteRelativePath"
echo "Publishing to remote update-site: $selectedUpdateSiteAbsolutePath"

selectedDownloadRelativePath="$projectDownloadsBasePath/$selectedDownloadPath"
selectedDownloadAbsolutePath="$eclipseDownloadsPath/$selectedDownloadRelativePath"
echo "Publishing to remote downloads: $selectedDownloadAbsolutePath"

# zip the local update site, and copy to downloads
echo "Zip and copy the local update site to downloads"
cd $relengProjectRelativePath
cd $localArtifactsDirectoryName
zip -r $updateZipFileName .
echo "move $updateZipFileName to $selectedDownloadAbsolutePath"
mv -f $updateZipFileName $selectedDownloadAbsolutePath
cd ..

# Download and prepare Eclipse SDK, which is needed to merge update site and postprocess repository
if [ ! -d "eclipse" ];
	then
		echo "Downloading eclipse to $PWD"
		cp $eclipsePackagePath/$eclipsePackageFileName $localRelengProjectPath
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

if [ $MERGE_UPDATE_SITE ];
        then
        echo "Merging existing site into local one."
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:$localArtifactsPath
        ./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$selectedUpdateSiteAbsolutePath -destination file:$localArtifactsPath
        echo "Merged $selectedUpdateSiteAbsolutePath into local update site directory : $localArtifactsPath."
fi

# Ensure p2.mirrorURLs property is used in update site
echo "Setting p2.mirrorsURL to http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath (see https://wiki.eclipse.org/WTP/Releng/Tools/addRepoProperties for details)"
./eclipse/eclipse -nosplash --launcher.suppressErrors -clean -debug -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$localArtifactsPath -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$selectedUpdateSiteRelativePath"

# Create p2.index file
if [ ! -e "$localArtifactsPath/p2.index" ];
        then
                echo "Creating p2.index file."
                echo "version = 1" > $localArtifactsPath/p2.index
                echo "metadata.repository.factory.order = content.xml,\!" >> $localArtifactsPath/p2.index
                echo "artifact.repository.factory.order = artifacts.xml,\!" >> $localArtifactsPath/p2.index
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
echo "Copying $localArtifactsPath/* to $selectedUpdateSiteAbsolutePath/*"
cp -R $localArtifactsPath/* $selectedUpdateSiteAbsolutePath/

