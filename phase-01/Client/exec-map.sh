#!/bin/bash

# Check if nmap is installed
if ! command -v nmap &> /dev/null; then
    echo "nmap is not installed. Please install nmap and try again."
    exit 1
fi

# Get all non-local IP addresses (excluding loopback addresses)
ADDRESSES=$(ip -o -f inet addr show | awk '{print $4}' | grep -v 127.*)

# Run nmap on each network in ADDRESSES
for IP in $ADDRESSES; do
	NET_ID="$(echo $IP | cut -d'.' -f1-3).0/24"
    echo "Running nmap scan on $NET_ID"

    # Run nmap scan on the current network
    nmap -F --open $NET_ID
done

