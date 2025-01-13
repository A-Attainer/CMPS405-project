#!/bin/bash

# Define the server IP (passed as an argument)
SERVER_IP="${1}"  

# Check if the web server is accessible using curl
if curl -s --head --request GET "http://$SERVER_IP:80" | grep "200 OK" > /dev/null; then
    echo "Web server is reachable at http://$SERVER_IP:80"
    
    # If reachable, load the homepage using w3m
    w3m "http://$SERVER_IP:80"
else
    echo "Web server is not reachable at http://$SERVER_IP:80"
fi
