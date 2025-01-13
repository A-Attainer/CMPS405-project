#!/bin/bash
cd "$(dirname "$0")"

# Path to the CSV file
CSV_FILE="config/clients.csv"
CURRENT_IP=$(hostname -I | awk '{print $1}')

# Process each row in the CSV file
while IFS=',' read -r username fullname email hostname ipaddress
do
  if [[ "$ipaddress" == "$CURRENT_IP" ]]; then
    # Set hostname
    echo "Setting hostname for $username to $hostname"
    sudo hostnamectl set-hostname "$hostname"

    # Run client setup scripts
    echo "Setting up client $username..."
    ./client/test-remote.sh "$username"
    ./client/create-keys.sh "$username"
    ./client/setup-auth.sh "$username"
    ./client/exec-map.sh "$username"
    ./client/init-site.sh "$username"
    ./client/publish-site.sh "$username"

    # Test web access
    ./client/test-web.sh "$username"

    # Display system information
    echo "Fetching system information..."
    ./utils/fetch-info.sh "$ipaddress"
  fi
done < "$CSV_FILE"
