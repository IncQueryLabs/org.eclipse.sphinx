#!/bin/sh

######################
# Command line options
######################

# $1: BUILD_RUN: The build to be published (<strong>Be sure to select only successful builds with all tests passed!</strong>), must be an URL formated like this one: https://hudson.eclipse.org/sphinx/job/sphinx-0.8-luna/99/
# $2: BUILD_TYPE: The type as which the selected build is going to be published, must be one of: I(ntegration), M(ilestone), R(elease)C(andidate) R(elease), T(est)
# $3: BUILD_ID: The id under which the selected build is going to be published, use following convention: I: yyyymmdd-hhmm, M: n RC: n, R: none
# $4: SERVICE_RELEASE_NUMBER: The service release number of the build to be published (will be used to complement the major and minor version number in the name of the build to be published)
# $5: MERGE_UPDATE_SITE: Whether to keep all previously published builds in project update site and merge the build to be published into it (project update site will be wiped out and replaced by the update site of the build to be published otherwise), must be one of: true, false

#####################
# Adjustable settings
#####################

relengProjectRelativePath=releng/org.eclipse.sphinx.releng.builds
buildUpdateSiteRelativePath=$relengProjectRelativePath/repository/target/repository
releaseStream=$(echo "$BUILD_RUN" | perl -ne 's#.+/[a-z]+-(\d.\d)-[a-z]+/\d+/$#\1#; print;'

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
		echo "------------------------------------------------------------------------"
		echo "Installing Eclipse"
		echo "------------------------------------------------------------------------"

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

echo "------------------------------------------------------------------------"
echo "Retrieving build update site"
echo "------------------------------------------------------------------------"

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

echo "------------------------------------------------------------------------"
echo "Creating archived update site"
echo "------------------------------------------------------------------------"

echo "Archiving $localUpdateSitePath/* into $applicableLocalUpdateSiteArchivePath"
zip -r $applicableLocalUpdateSiteArchivePath $localUpdateSitePath/*

echo "------------------------------------------------------------------------"
echo "Publishing archived update site"
echo "------------------------------------------------------------------------"

echo "Copying $applicableLocalUpdateSiteArchivePath to $applicableProjectDownloadSitePath"
mkdir -p $applicableProjectDownloadSitePath
cp $applicableLocalUpdateSiteArchivePath $applicableProjectDownloadSitePath

if [ $MERGE_UPDATE_SITE ];
	then
		echo "------------------------------------------------------------------------"
		echo "Merging project update site into build update site"
		echo "------------------------------------------------------------------------"

        echo "Merging $applicableProjectUpdateSitePath into $localUpdateSitePath"
        $eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:$applicableProjectUpdateSitePath -destination file:$localUpdateSitePath
        $eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:$applicableProjectUpdateSitePath -destination file:$localUpdateSitePath
fi

echo "------------------------------------------------------------------------"
echo "Setting p2.mirrorsURL property"
echo "------------------------------------------------------------------------"

echo "Setting p2.mirrorsURL property of $localUpdateSitePath to http://www.eclipse.org/downloads/download.php?format=xml&file=/$applicableProjectUpdateSiteRelativePath (see https://wiki.eclipse.org/WTP/Releng/Tools/addRepoProperties for details)"
$eclipseInstallPath/eclipse -nosplash --launcher.suppressErrors -clean -application org.eclipse.wtp.releng.tools.addRepoProperties -vmargs -DartifactRepoDirectory=$localUpdateSitePath -Dp2MirrorsURL="http://www.eclipse.org/downloads/download.php?format=xml&file=/$applicableProjectUpdateSiteRelativePath"

if [ ! -e "$localUpdateSitePath/p2.index" ];
    then
		echo "------------------------------------------------------------------------"
		echo "Creating p2.index file"
		echo "------------------------------------------------------------------------"

        echo "Creating p2.index file for $localUpdateSitePath"
        echo "version = 1" > $localUpdateSitePath/p2.index
        echo "metadata.repository.factory.order = content.xml,\!" >> $localUpdateSitePath/p2.index
        echo "artifact.repository.factory.order = artifacts.xml,\!" >> $localUpdateSitePath/p2.index
fi

if [ -d "$applicableProjectUpdateSitePath" ];
    then
		echo "------------------------------------------------------------------------"
		echo "Creating backup of project update site"
		echo "------------------------------------------------------------------------"

		echo "Copying $applicableProjectUpdateSitePath/* to $projectUpdateSiteBackupPath"
        rm -rf $projectUpdateSiteBackupPath
        mkdir $projectUpdateSiteBackupPath
        cp -R $applicableProjectUpdateSitePath/* $projectUpdateSiteBackupPath/

        echo "Removing $applicableProjectUpdateSitePath"
        rm -rf $applicableProjectUpdateSitePath
fi

echo "------------------------------------------------------------------------"
echo "Publishing update site"
echo "------------------------------------------------------------------------"

echo "Copying $localUpdateSitePath/* to $applicableProjectUpdateSitePath"
mkdir -p $applicableProjectUpdateSitePath
cp -R $localUpdateSitePath/* $applicableProjectUpdateSitePath
