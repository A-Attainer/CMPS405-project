#!/bin/bash

# check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Define installation command 
INSTALL_COMMAND="sudo dnf install -y"

# Check and install pwgen if not available
if command_exists pwgen; then
    echo "pwgen is already installed."
else
    echo "Installing pwgen..."
    $INSTALL_COMMAND pwgen || { echo "Failed to install pwgen."; exit 1; }
fi

# Generate a random password with pwgen
PASSWORD=$(pwgen -s 16 1)

# output password
echo "$PASSWORD"

