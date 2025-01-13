#!/bin/bash

# Ensure the script is run with sudo privileges
if [[ $(id -u) -ne 0 ]]; then
    echo "This script requires administrative privileges. Please run it with sudo."
    exit 1
fi

echo "Starting NGINX setup and configuration..."

# Installing NGINX 
echo "Installing NGINX..."
dnf install -y nginx
if [[ $? -ne 0 ]]; then
    echo "Failed to install NGINX. Exiting."
    exit 1
fi

# Enable and start the NGINX service
echo "Enabling and starting the NGINX service..."
systemctl enable nginx
systemctl start nginx
if [[ $? -ne 0 ]]; then
    echo "Failed to start NGINX service. Exiting."
    exit 1
fi

# Display the NGINX service status
echo "Checking NGINX service status..."
systemctl status nginx --no-pager

# Configure the firewall to allow HTTP traffic
echo "Configuring firewall to allow HTTP traffic..."
firewall-cmd --permanent --add-service=http
if [[ $? -ne 0 ]]; then
    echo "Failed to configure firewall for HTTP. Exiting."
    exit 1
fi

# Reload firewall rules
echo "Reloading firewall to apply changes..."
firewall-cmd --reload
if [[ $? -ne 0 ]]; then
    echo "Failed to reload firewall rules. Exiting."
    exit 1
fi
echo "Firewall configured successfully for HTTP."

# Test the NGINX server using cURL
echo "Testing if the NGINX server is running..."
curl -I http://localhost
if [[ $? -ne 0 ]]; then
    echo "Failed to connect to NGINX server. Please check the service and firewall configuration."
    exit 1
fi

echo "NGINX server setup and configuration completed successfully!"
exit 0
