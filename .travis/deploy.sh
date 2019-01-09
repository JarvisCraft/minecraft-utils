#!/usr/bin/env bash

echo 'Decrypting encryption key'
openssl aes-256-cbc -pass pass:"$CODESIGNING_ASC_ENC_PASS" \
-in .travis/gpg/codesigning.asc.enc -out .travis/gpg/codesigning.asc -d
echo 'Decrypted encryption key'

echo 'Importing encryption key'
gpg --fast-import .travis/gpg/codesigning.asc
echo 'Imported encryption key'

echo 'Deploying artifacts'
# Generate source and javadocs, sign binaries, deploy to Sonatype using credentials from env.
mvn deploy -P build-jars-with-dependencies,build-extras,sign,ossrh-env-credentials,ossrh-deploy \
--settings .travis/.mvn/settings.xml
echo 'Deployed artifacts'