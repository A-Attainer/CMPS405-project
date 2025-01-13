#!/bin/bash



# server details
SSH_USER=$1  # server username
SERVER_HOSTNAME=$2  # server IP address


# test if Function exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# installation command
INSTALL_COMMAND="sudo dnf install -y"

# Verify and or install SSH client
if command_exists ssh; then
    echo "SSH client is already installed."
else
    echo "SSH client not found. Installing..."
    $INSTALL_COMMAND openssh-clients || { echo "Failed to install SSH client."; exit 1; }
fi

# Verify and install Mosh
if command_exists mosh; then
    echo "Mosh is already installed."
else
    echo "Mosh not found. Installing..."
    $INSTALL_COMMAND mosh || { echo "Failed to install Mosh."; exit 1; }
fi

# Test SSH connection
echo "Testing SSH connection to $SERVER_HOSTNAME..."
if ssh -o ConnectTimeout=5 "$SSH_USER@$SERVER_HOSTNAME" exit; then
    echo "SSH connection to $SERVER_HOSTNAME successful."
else
    echo "Failed to connect to $SERVER_HOSTNAME via SSH."
    exit 1
fi

# Test Mosh connection
echo "Testing Mosh connection to $SERVER_HOSTNAME..."
if mosh "$SSH_USER@$SERVER_HOSTNAME" -- exit; then
    echo "Mosh connection to $SERVER_HOSTNAME successful."
else
    echo "Failed to connect to $SERVER_HOSTNAME via Mosh."
    exit 1
fi

echo "All tests passed. SSH and Mosh are functioning correctly."
