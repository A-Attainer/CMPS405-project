#!/bin/bash

# Define the directory where the site will be created
SITE_DIR="$HOME/site"

# Check if Hugo is installed
if ! command -v hugo &> /dev/null; then
    echo "Hugo is not installed. Please install Hugo and try again."
    exit 1
fi

# Check if Git is installed
if ! command -v git &> /dev/null; then
    echo "Git is not installed. Please install Git and try again."
    exit 1
fi

# Create a new Hugo site in the defined directory
echo "Creating a new Hugo site at $SITE_DIR..."
hugo new site "$SITE_DIR" || exit 2

# Navigate to the site directory
cd "$SITE_DIR" || exit 2

# Initialize a new Git repository in the site directory
echo "Initializing Git repository..."
git init

# Add the default theme submodule (for publication)
echo "Adding Git submodule for Hugo publication..."
git submodule add https://github.com/theNewDynamic/gohugo-theme-ananke.git themes/ananke

# Initialize the submodule
git submodule update --init --recursive

# Add the default content to the site (optional, can be customized)
echo "Adding default content to the site..."
hugo new posts/first-post.md

# Stage all the files and make the first commit
echo "Committing the initial site structure..."
git add .
git commit -m "Initial commit: Hugo site structure and theme"

# Print the next steps
echo ""
echo "The site has been successfully created in $SITE_DIR."
echo "You can now run 'hugo server' to start the development server."
echo "Use 'git push' to push the site to your remote repository (if configured)."
