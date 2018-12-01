#!/usr/bin/env bash
echo "Executing before-deployment.sh"

echo "Branch: $TRAVIS_BRANCH"
echo "Branch: $TRAVIS_PULL_REQUEST"

# Deployment happens only for `releases` branch excluding pull requests to it
if [ "$TRAVIS_BRANCH" = 'releases' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "Decrypting encryption key"
    openssl aes-256-cbc -K $encrypted_0b9eeae2d880_key -iv $encrypted_0b9eeae2d880_iv \
    -in .travis/gpg/codesigning.asc.enc -out .travis/gpg/codesigning.asc -d
    echo "Decrypted"

    echo "Importing encryption key"
    gpg --fast-import .travis/gpg/codesigning.asc
    echo"Imported"

    echo "Deploying to maven central"
    # Generate source and javadocs, sign binaries, deploy to Sonatype using credentials from env.
    mvn deploy -P build-extras,sign,ossrh-env-credentials,ossrh-deploy --settings .travis/gpg/mvnsettings.xml
    echo "Deployed"
fi