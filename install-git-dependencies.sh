#!/usr/bin/env bash


readonly ROOT_DIRECTORY=$(pwd)
readonly DIRECTORY='___locally_built_dependencies___'

# Resets the selected directory to the ROOT_DIRECTORY
function reset_directory() {
    cd $ROOT_DIRECTORY
}

# Installs dependency using HTTPS-Git and Maven
# @param 1 Url of Git user
# @param 2 Name of the Git repository
# @param 3 Git branch to use
# <@param 4> Sub-directory to use as Maven root
function install_dependency() {
    local url="https://$1/$2.git"
    echo "Installing dependency from $url ($3)"
    git clone --single-branch --branch $3 $url
    cd $2
    if [ ! -z $4 ]; then
        cd $4
    fi

    echo "Building..."
    mvn clean install -Dmaven.javadoc.skip=true -B -V

    reset_directory
}

echo 'Installing special dependencies'

echo "Root directory set to $ROOT_DIRECTORY"

echo "Creating directory $DIRECTORY"
mkdir $DIRECTORY
echo 'Directory created'
cd $DIRECTORY

install_dependency "github.com/JarvisCraft" "PacketWrapper" "legacy-support" "PacketWrapper"