#!/bin/bash

# Define variables
SITE_DIR="$HOME/site"                # Local directory of the Hugo site
BUILD_DIR="$SITE_DIR/public"     # Hugo output directory (default: public/)
REMOTE_USER=$1      # Replace with your SSH username on the server
REMOTE_HOST=$2     # Replace with your server's IP or hostname
REMOTE_DIR=/site # Remote directory where the site will be deployed (e.g., /var/www/html)

# Step 1: Build the Hugo site
echo "Building the Hugo site..."
cd $SITE_DIR
hugo

# Step 2: Synchronize the local build directory with the remote server using rsync
echo "Synchronizing the site with the remote server..."

rsync -avz --delete $BUILD_DIR/ $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/

# Explanation of the rsync options:
# -a : archive mode, preserves symbolic links, permissions, timestamps, etc.
# -v : verbose, shows details of the files being transferred
# -z : compresses the data during transfer to save bandwidth
# --delete : deletes files from the remote server that no longer exist in the local build (this ensures the remote server mirrors the local directory)

# Step 3: Confirm the deployment was successful
if [ $? -eq 0 ]; then
  echo "Site published successfully!"
else
  echo "An error occurred during the site publishing process."
  exit 1
fi
