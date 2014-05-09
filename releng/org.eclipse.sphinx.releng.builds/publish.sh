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

echo BUILD_RUN=$BUILD_RUN
echo BUILD_TYPE=$BUILD_TYPE
echo BUILD_ID=$BUILD_ID
echo SERVICE_RELEASE_NUMBER=$SERVICE_RELEASE_NUMBER
echo MERGE_UPDATE_SITE=$MERGE_UPDATE_SITE

relengProjectRelativePath=releng/org.eclipse.sphinx.releng.builds
buildUpdateSiteRelativePath=$relengProjectRelativePath/repository/target/repository
releaseStream=$(echo "$BUILD_RUN" | perl -ne 's#.+/[a-z]+-(\d.\d)-[a-z]+/\d+/$#\1#; print;')

echo $releaseStream