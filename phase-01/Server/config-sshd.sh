#!/bin/bash

# Check if the script is run with sudo
if [[ $(id -u) -ne 0 ]]; then
    echo "This script requires administrative privileges. Please run with sudo."
    exit 1
fi

echo "Starting SSH server setup..."

# Install OpenSSH Server
echo "Installing OpenSSH server..."
dnf install -y openssh-server
if [[ $? -ne 0 ]]; then
    echo "Failed to install OpenSSH server. Exiting."
    exit 1
fi

# Enable and start SSH service
echo "Enabling and starting the SSH service..."
systemctl enable sshd
systemctl start sshd
systemctl status sshd --no-pager

# Enable SFTP and restrict access to the 'clients' group
echo "Configuring SSH server to restrict access to the 'clients' group..."

# Create 'clients' group if it doesn't exist
if ! getent group clients > /dev/null; then
    echo "Creating 'clients' group..."
    groupadd clients
fi

# Backup the SSH configuration file
echo "Backing up SSH configuration file..."
cp /etc/ssh/sshd_config /etc/ssh/sshd_config.bak

# Modify SSH configuration to restrict access and enable SFTP
echo "Applying SSH server configurations..."
sed -i '/^#Subsystem sftp/s/^#//' /etc/ssh/sshd_config
sed -i '/^AllowGroups/s/^/#/' /etc/ssh/sshd_config
echo -e "\n# Allow only the 'clients' group to access via SSH\nAllowGroups clients" >> /etc/ssh/sshd_config

# Restart SSH service to apply changes
echo "Restarting SSH service..."
systemctl restart sshd

# Configure firewall rules
echo "Configuring firewall rules for SSH access..."
firewall-cmd --permanent --add-service=ssh
firewall-cmd --reload

echo "Firewall rules updated."

# status checks
echo "Final SSH service status:"
systemctl status sshd --no-pager

echo "SSH server setup and configuration completed successfully!"
exit 0
