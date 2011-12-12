#!/bin/sh

# Need to run a full clean install when changing the request factory interfaces
# Doing so deletes the local debug app engine database
# This script backs that up first

SRC=shuffle-app-engine/target/shuffle-app-engine-2.0.0-SNAPSHOT/WEB-INF/appengine-generated
DEST=../db_backup

rm -r $DEST
mkdir $DEST
cp -r $SRC/* $DEST
mvn -pl shuffle-shared,shuffle-app-engine clean install
cp -r $DEST $SRC

