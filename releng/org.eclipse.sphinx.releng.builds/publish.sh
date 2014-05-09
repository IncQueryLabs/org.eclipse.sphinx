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

echo $BUILD_RUN
echo $BUILD_TYPE
echo $BUILD_ID
echo $SERVICE_RELEASE_NUMBER
echo $MERGE_UPDATE_SITE
