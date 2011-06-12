#!/bin/bash

if [ $# != 1 ]
  then
    echo "Usage: $0 emailAddress"
    exit 1
fi

read -s -p "Password: " mypassword
echo ""

curl https://www.google.com/accounts/ClientLogin -d Email=$1 -d "Passwd=$mypassword" -d accountType=GOOGLE -d source=Google-cURL-Example -d service=ac2dm
