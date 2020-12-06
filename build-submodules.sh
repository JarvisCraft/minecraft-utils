#!/usr/bin/env bash

readonly ROOT_DIRECTORY=$(pwd)

# Make sure that submodules are initialized
git submodule update --init --recursive

function install_dependency() {
    cd "$1" || exit
    mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -B -V
    cd "$ROOT_DIRECTORY" || exit
}

echo 'Building submodule dependencies'

install_dependency "dependencies/PacketWrapper/PacketWrapper"

echo 'Submodule dependencies have been built'
