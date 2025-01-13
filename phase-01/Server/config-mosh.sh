#!/bin/bash

# Ensure the script is run with sudo privileges
if [[ $(id -u) -ne 0 ]]; then
    echo "This script requires administrative privileges. Please run it with sudo."
    exit 1
fi

echo "Starting Mosh setup and configuration..."

# Install Mosh
echo "Installing Mosh..."
dnf install -y mosh
if [[ $? -ne 0 ]]; then
    echo "Failed to install Mosh. Exiting."
    exit 1
fi

# Verify that Mosh is installed
if ! command -v mosh > /dev/null; then
    echo "Mosh installation was unsuccessful. Exiting."
    exit 1
fi
echo "Mosh installed successfully."

# Copy the configuration file to the firewalld services directory
echo "Copying the mosh.xml configuration file to /etc/firewalld/services/..."
MOSH_CONFIG="/etc/firewalld/services/mosh.xml"
cp config/mosh.xml "$MOSH_CONFIG"
if [[ $? -ne 0 ]]; then
    echo "Failed to copy mosh.xml to $MOSH_CONFIG. Exiting."
    exit 1
fi

# Reload firewalld and add Mosh as a permanent service
echo "Adding Mosh as a permanent firewall service..."
firewall-cmd --permanent --add-service=mosh
if [[ $? -ne 0 ]]; then
    echo "Failed to add Mosh to the firewall. Exiting."
    exit 1
fi

# Reload firewalld to apply changes
echo "Reloading firewall to apply the new configuration..."
firewall-cmd --reload
if [[ $? -ne 0 ]]; then
    echo "Failed to reload the firewall. Exiting."
    exit 1
fi

echo "Firewall updated successfully with Mosh service."

# Final status check
echo "Final verification of firewall settings:"
firewall-cmd --list-all

echo "Mosh setup and configuration completed successfully!"
exit 0
