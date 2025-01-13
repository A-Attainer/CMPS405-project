#!/bin/bash

#in my machine i needed to run these three scripts for it to work
# i think because GNOME Keyring or other keyring agents interfere with the SSH agent, so i disabled it.

pkill ssh-agent
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519


SSH_KEY_PATH="/home/client/.ssh/id_ed25519" # key path
SSH_USER=$1  # server's SSH username
SERVER_HOST=$2  # server's IP 

# Check if the public key exists
if [ ! -f "$SSH_KEY_PATH.pub" ]; then
    echo "Error: SSH public key not found at $SSH_KEY_PATH.pub."
    echo "check the provided key path or Run the client/create-keys.sh script to generate the key pair."
    exit 1
fi

# Upload the public key to the server
echo "Uploading public key to $SERVER_HOST..."
ssh-copy-id -i "$SSH_KEY_PATH.pub" "$SSH_USER@$SERVER_HOST"

# Test the passwordless SSH login
echo "Testing SSH login to $SERVER_HOST..."
if ssh -o PasswordAuthentication=no "$SSH_USER@$SERVER_HOST" exit; then
    echo "Passwordless SSH login to $SERVER_HOST is successful!"
else
    echo "Failed to set up passwordless SSH login. Please check the steps and try again."
    exit 1
fi


ssh "$SSH_USER@$SERVER_HOST"
