#!/usr/bin/env bash
echo "Executing before-deployment.sh"

echo "Branch: $TRAVIS_BRANCH"
echo "Pull-request: $TRAVIS_PULL_REQUEST"

# Deployment happens only for `releases` branch excluding pull requests to it
if [ "$TRAVIS_BRANCH" = 'releases' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "Decrypting encryption key"
    openssl aes-256-cbc -pass pass:"$CODESIGNING_ASC_ENC_PASS" \
    -in .travis/gpg/codesigning.asc.enc -out .travis/gpg/codesigning.asc -d
    echo "Decrypted"

    echo "Importing encryption key"
    gpg --fast-import .travis/.mvn/codesigning.asc
    echo"Imported"

    echo "Deploying to maven central"
    # Generate source and javadocs, sign binaries, deploy to Sonatype using credentials from env.
    mvn deploy -P build-extras,sign,ossrh-env-credentials,ossrh-deploy --settings .travis/gpg/settings.xml
    echo "Deployed"
fi