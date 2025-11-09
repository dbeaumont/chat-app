#!/bin/bash

brew install mkcert || sudo apt-get install mkcert
mkcert -install
mkcert localhost
mkdir -p ../.certs/
mv localhost*.pem ../.certs/
