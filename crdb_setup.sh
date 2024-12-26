#!/bin/bash
echo Wait for servers to be up
sleep 10

HOSTPARAMS="--host database --insecure"
SQL="/cockroach/cockroach.sh sql $HOSTPARAMS"

$SQL -e "CREATE DATABASE IF NOT EXISTS mydatabase;"
$SQL -e "SHOW USERS;"
$SQL -e "CREATE USER myuser;"
