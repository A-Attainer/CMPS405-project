#!/bin/bash
cd "$(dirname "$0")"

# Paths to CSV files
SERVER_CSV="config/server.csv"
CLIENTS_CSV="config/clients.csv"

# Read server hostname and IP from server.csv
SERVER_HOSTNAME=$(awk -F',' 'NR==1 {print $4}' "$SERVER_CSV")
SERVER_IP=$(awk -F',' 'NR==1 {print $5}' "$SERVER_CSV")

# Set server hostname
echo "Setting hostname to $SERVER_HOSTNAME"
sudo hostnamectl set-hostname "$SERVER_HOSTNAME"

# Run server setup scripts
echo "Setting up the server..."
./server/config-sshd.sh
./server/config-mosh.sh
./server/config-nginx.sh

# Create users from clients.csv
while IFS=',' read -r username fullname email hostname ipaddress
do
  ./server/create-client.sh "$username" "$fullname"
  ./server/config-site.sh "$username"
done < "$CLIENTS_CSV"

# Display system information
echo "Fetching server system information..."
./utils/fetch-info.sh "$SERVER_IP"
