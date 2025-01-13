#!/bin/bash

# create-client.sh
# Usage: ./create-client.sh <username> "<full name>"

username=$1
full_name=$2

# Function to create a user and set a password
create_user() {
    local user=$1
    local fullname=$2

    # Add user with home directory and full name
    sudo useradd -m -c "$fullname" -s /bin/bash "$user"

    # Check if useradd was successful
    if [ $? -ne 0 ]; then
        echo "Error: Failed to create user $user."
        exit 1
    fi

    # Set a generated password
    local password
    password=$(./utils/generate-pass.sh)  # Adjusted to relative path
    echo -e "$password\n$password" | sudo passwd "$user"

    # Output the username and password
    echo "User created:"
    echo "Username: $user"
    echo "Password: $password"
}

# Create the group 'clients' if it doesn't exist
sudo getent group clients &> /dev/null || sudo groupadd clients

# Call the function to create user
create_user "$username" "$full_name"

# Add user to the 'clients' and 'sudoers' groups
sudo usermod -aG clients,wheel "$username"
echo "$username added to the 'clients' and 'sudoers' groups."

# Check if config-site.sh exists and is executable
if [ -x "./utils/config-site.sh" ]; then  # Adjusted path to relative path
    # Call config-site.sh to set up the user's website directory
    ./utils/config-site.sh "$username"
else
    echo "Error: config-site.sh not found or not executable."
    exit 1
fi
