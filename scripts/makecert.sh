#!/bin/bash

brew install mkcert
mkcert -install
mkcert localhost
mkdir -p ../.certs/
mv localhost*.pem ../.certs/
