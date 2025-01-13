#!/bin/bash

# config-site.sh
# Usage: ./config-site.sh <username>

# Check if username is provided as an argument
if [ -z "$1" ]; then
    echo "Usage: $0 <username>"
    exit 1
fi

username=$1

# Variables
user_home="/home/$username"
site_dir="$user_home/site"
nginx_html_dir="/usr/share/nginx/html"
nginx_user="nginx"  # Default nginx user on Fedora

# Ensure the script is run with sudo privileges
if [[ $(id -u) -ne 0 ]]; then
    echo "This script requires administrative privileges. Please run it with sudo."
    exit 1
fi

# Create the site directory in the user's home directory if it doesn't exist
if [ ! -d "$site_dir" ]; then
    echo "Creating site directory for user $username..."
    sudo -u "$username" mkdir -p "$site_dir"
    sudo -u "$username" chmod 700 "$site_dir"
fi

# Create symbolic link in NGINX html directory
echo "Creating symbolic link in NGINX html directory..."
if [ -L "$nginx_html_dir/$username" ]; then
    echo "Symbolic link already exists. Removing and recreating it."
    rm "$nginx_html_dir/$username"
fi
ln -s "$site_dir" "$nginx_html_dir/$username"

# Adjusting permissions to allow NGINX to read the user's site directory

# Option 1: Use Access Control Lists (ACLs) for better security
echo "Setting up permissions using ACLs..."

# Allowing NGINX to traverse the user's home directory
setfacl -m u:$nginx_user:x "$user_home"

# Allowing NGINX to read the site directory and its contents
setfacl -R -m u:$nginx_user:rx "$site_dir"

# setting default ACLs so new files/directories inherit the permissions
setfacl -R -d -m u:$nginx_user:rx "$site_dir"

echo "Website directory configured for user $username."
echo "The site can be accessed at http://server_ip/$username/"

exit 0
