#!/bin/sh

# $1: BUILD_RUN:
# $1: BUILD_TYPE:  I(ntegration), M(ilestone), R(elease)C(andidate) R(elease), T(est)
# $3: Release stream: <major>.<minor>.x, e.g., 0.8.x (only required if build type is release, ignored otherwise)
# $2: Whether to merge the site with an existing one: (t)rue, (f)alse

# TODO
# * Update parameter documentation

#####################
# Adjustable settings
#####################

relengProjectRelativePath=releng/org.eclipse.sphinx.releng.builds
buildUpdateSiteRelativePath=$relengProjectRelativePath/repository/target/repository
releaseStream=$(echo "$BUILD_RUN" | perl -ne 's#.+/[a-z]+-(\d.\d)-[a-z]+)/\d+/$#\1#; print;'

projectUpdateSitesBasePath=sphinx/updates
projectDownloadSitesBasePath=sphinx/downloads
updateSiteArchiveFileNamePrefix=sphinx-Update

eclipsePackageVersion=4.2.2
eclipsePackageBuildId=201302041200

##################
# Derived settings
##################

buildPath=${WORKSPACE}/../../$(echo "$BUILD_RUN" | perl -ne 's#.+/([^/])/(\d+)/$#\1/builds/\2#; print;'
buildUpdateSitePath=$buildPath/archive/$buildUpdateSiteRelativePath
buildUpdateSiteURL=$BUILD_RUN/artifact/$buildUpdateSiteRelativePath

releaseStreamName=$releaseStream.x
release=$releaseStream.$SERVICE_RELEASE_NUMBER

localRelengProjectPath=${WORKSPACE}/$relengProjectRelativePath
localUpdateSitePath=$localRelengProjectPath/updates
localDownloadSitePath=$localRelengProjectPath/downloads

projectUpdateSiteBackupPath=$localRelengProjectPath/backup

eclipseDownloadsPath=/home/data/httpd/download.eclipse.org
eclipsePackageFileName=eclipse-platform-$eclipsePackageVersion-linux-gtk-x86_64.tar.gz
eclipsePackageDownloadPath=$eclipseDownloadsPath/eclipse/downloads/drops4/R-$eclipsePackageVersion-$eclipsePackageBuildId/$eclipsePackageFileName
eclipseInstallPath=$localRelengProjectPath

##################
# Runtime settings
##################

case $BUILD_TYPE in
        I) applicableProjectUpdateSiteName=interim
           applicableProjectDownloadSiteName=integration
           applicableUpdateSiteArchiveFileName=$updateSiteArchiveFileNamePrefix-$release.$BUILD_TYPE$BUILD_ID.zip
           ;;
        M|RC) applicableProjectUpdateSiteName=interim
        	  applicableProjectDownloadSiteName=stable
        	  applicableUpdateSiteArchiveFileName=$updateSiteArchiveFileNamePrefix-$release$BUILD_TYPE$BUILD_ID.zip
        	  ;;
        R) applicableProjectUpdateSiteName=releases/$releaseStreamName
           applicableProjectDownloadSiteName=releases/$releaseStreamName
           applicableUpdateSiteArchiveFileName=$updateSiteArchiveFileNamePrefix-$release.zip
           ;;
        T) applicableProjectUpdateSiteName=test
           applicableProjectDownloadSiteName=test
           applicableUpdateSiteArchiveFileName=$updateSiteArchiveFileNamePrefix-$release.$BUILD_TYPE$BUILD_ID.zip
           ;;
        *) exit 0 ;;
esac
applicableProjectUpdateSiteRelativePath="$projectUpdateSitesBasePath/$applicableProjectUpdateSiteName"
applicableProjectUpdateSitePath="$eclipseDownloadsPath/$applicableProjectUpdateSiteRelativePath"
applicableProjectDownloadSiteRelativePath="$projectDownloadSitesBasePath/$applicableProjectDownloadSiteName"
applicableProjectDownloadSitePath="$eclipseDownloadsPath/$applicableProjectDownloadSiteRelativePath"
applicableLocalUpdateSiteArchivePath=$localDownloadSitePath/$applicableUpdateSiteArchiveFileName

#############################################################################################
# Eclipse installation (required to create merged update site and set p2.mirrorsURL property)
#############################################################################################

if [ ! -d "$eclipseInstallPath/eclipse" ];
	then
		echo "Copying $eclipsePackageDownloadPath to $localRelengProjectPath"
		cp $eclipsePackageDownloadPath $localRelengProjectPath
		tar -xzf $localRelengProjectPath/$eclipsePackageFileName -C $eclipseInstallPath
		chmod 700 $eclipseInstallPath/eclipse/eclipse
		if [ ! -d "$eclipseInstallPath/eclipse" ];
        	then
                echo "Failed to install Eclipse package required for publishing."
                exit
		fi

		echo "Installing WTP Releng tools"
		$eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/webtools/releng/repository/ -installIUs org.eclipse.wtp.releng.tools.feature.feature.group

		echo "Removing $localRelengProjectPath/$eclipsePackageFileName"
		rm $localRelengProjectPath/$eclipsePackageFileName
fi

####################
# Publishing process
####################

echo "Copying $buildUpdateSitePath/* to $localUpdateSitePath"
rm -rf $localUpdateSitePath
mkdir $localUpdateSitePath
cp -R $buildUpdateSitePath/* $localUpdateSitePath
find $applicableProjectDownloadSitePath -type f -name "*.html" -delete
#find $applicableProjectDownloadSitePath -type d -name "*zip*" -delete

# Alternative approach:
# echo "Downloading $buildUpdateSiteURL/* to $localUpdateSitePath"
# rm -rf $localUpdateSitePath
# wget --mirror --execute robots=off --directory-prefix=$localUpdateSitePath --no-host-directories --cut-dirs=11 --no-parent --reject="index.html*,*zip*" --timestamping $buildUpdateSiteURL/

echo "Archiving $localUpdateSitePath/* into $applicableLocalUpdateSiteArchivePath"
zip -r $applicableLocalUpdateSiteArchivePath $localUpdateSitePath/*

echo "Copying $applicableLocalUpdateSiteArchivePath to $applicableProjectDownloadSitePath"
mkdir -p $applicableProjectDownloadSitePath
cp $applicableLocalUpdateSiteArchivePath $applicableProjectDownloadSitePath

if [ $MERGE_UPDATE_SITE ];
	then
        echo "Merging $applicableProjectUpdateSitePath into $localUpdateSitePath"
        $eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$applicableProjectUpdateSitePath -destination file:$localUpdateSitePath
        $eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$applicableProjectUpdateSitePath -destination file:$localUpdateSitePath
fi

echo "Setting p2.mirrorsURL property of $localUpdateSitePath to http://www.eclipse.org/downloads/download.php?format=xml&file=/$applicableProjectUpdateSiteRelativePath (see https://wiki.eclipse.org/WTP/Releng/Tools/addRepoProperties for details)"
$eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$localUpdateSitePath -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$applicableProjectUpdateSiteRelativePath"

if [ ! -e "$localUpdateSitePath/p2.index" ];
    then
            echo "Creating p2.index file for $localUpdateSitePath"
            echo "version = 1" > $localUpdateSitePath/p2.index
            echo "metadata.repository.factory.order = content.xml,\!" >> $localUpdateSitePath/p2.index
            echo "artifact.repository.factory.order = artifacts.xml,\!" >> $localUpdateSitePath/p2.index
fi

if [ -d "$applicableProjectUpdateSitePath" ];
    then
			echo "Copying $applicableProjectUpdateSitePath/* to $projectUpdateSiteBackupPath"
            rm -rf $projectUpdateSiteBackupPath
            mkdir $projectUpdateSiteBackupPath
            cp -R $applicableProjectUpdateSitePath/* $projectUpdateSiteBackupPath/

            echo "Removing $applicableProjectUpdateSitePath"
            rm -rf $applicableProjectUpdateSitePath
fi

echo "Copying $localUpdateSitePath/* to $applicableProjectUpdateSitePath"
mkdir -p $applicableProjectUpdateSitePath
cp -R $localUpdateSitePath/* $applicableProjectUpdateSitePath
