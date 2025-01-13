#!/bin/bash

# generate-pass.sh
# Usage: ./generate-pass.sh

# Ensure the pwgen command is available
./utils/check-install.sh pwgen pwgen  # Adjusted path to relative path

# Generate a random password
pwgen 12 1
