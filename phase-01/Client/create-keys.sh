#!/bin/bash

# Paths
GENERATE_PASS_SCRIPT="generate-pass.sh"
SSH_KEY_DIR="$HOME/.ssh"
KEY_NAME="id_ed25519"

# Ensure utils/generate-pass.sh is executable
if [ ! -x "$GENERATE_PASS_SCRIPT" ]; then
    echo "Error: $GENERATE_PASS_SCRIPT is not executable or does not exist."
    exit 1
fi


echo "Enter a passphrase to secure the SSH key (leave blank to generate one automatically):"
read -r -s USER_PASSPHRASE

# Generate a secure passphrase if none was provided
if [ -z "$USER_PASSPHRASE" ]; then
    echo "No passphrase provided. Generating one automatically..."
    USER_PASSPHRASE=$($GENERATE_PASS_SCRIPT)
    echo "Generated Passphrase: $USER_PASSPHRASE"
    echo "Please save this passphrase securely."
fi

# Create the SSH key directory if it doesn't exist
mkdir -p "$SSH_KEY_DIR"
chmod 700 "$SSH_KEY_DIR"

# Generate the Ed25519 SSH key pair
SSH_KEY_PATH="$SSH_KEY_DIR/$KEY_NAME"
if ssh-keygen -t ed25519 -f "$SSH_KEY_PATH" -N "$USER_PASSPHRASE"; then
    echo "SSH key pair generated successfully:"
    echo " - Private key: $SSH_KEY_PATH"
    echo " - Public key: $SSH_KEY_PATH.pub"
else
    echo "Failed to generate SSH key pair."
    exit 1
fi
