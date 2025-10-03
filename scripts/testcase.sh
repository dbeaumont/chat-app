#!/usr/bin/env bash

echo "Récupération de l'access token OIDC"
export TOKEN=$(./get-access-token.sh)

echo "Lancer l'api"
./smoke-tests.sh

