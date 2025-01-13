#!/bin/bash

# check-install.sh
# Usage: ./check-install.sh <command_name> <package_name>

command_name=$1
package_name=$2

# Check if the command is available
if ! command -v $command_name &> /dev/null; then
    echo "$command_name could not be found, installing $package_name..."
    sudo dnf install -y $package_name

    # Verify if the installation was successful
    if command -v $command_name &> /dev/null; then
        echo "$command_name has been successfully installed."
    else
        echo "Failed to install $package_name. Please check for errors."
        exit 1
    fi
else
    echo "$command_name is already installed."
fi
