#!/bin/bash

# -Djavax.net.debug=all
# '-Djavax.net.ssl.keyStore=src/main/resources/ssl_cert.jks' \
# '-Djavax.net.ssl.keyStore=../play/conf/stubby_key.jks' \

HTTP_PORT=9001
HTTPS_PORT=9443

if [ "$1" != "" ]; then
   HTTP_PORT=$1
fi

if [ "$2" != "" ]; then
   HTTPS_PORT=$2
fi

SCRIPT_DIR=$(dirname $(readlink -f $0))
JAR="$SCRIPT_DIR/target/http-stub-server-standalone-1.1-SNAPSHOT.one-jar.jar"
KEY_STORE="$SCRIPT_DIR/src/main/resources/ssl_cert.jks"

java \
    "-Djavax.net.ssl.keyStore=$KEY_STORE" \
    '-Djavax.net.ssl.keyStorePassword=password' \
    -jar $JAR \
    $HTTP_PORT \
    $HTTPS_PORT

