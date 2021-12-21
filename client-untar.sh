#!/bin/bash
#Improvement: arguments: [$1: project.name, $2: project.version]

#Aborts on error
set -e
#enter server/target folder
cd client/target

# untar
gzip="tpe1-g3-client-1.0-SNAPSHOT-bin.tar.gz"
tar -xzf $gzip

#cd into the folder name
cd "tpe1-g3-client-1.0-SNAPSHOT"

#give execution permission to scripts
chmod +x run-airline.sh
chmod +x run-management.sh
chmod +x run-query.sh
chmod +x run-runway.sh

#go back to parent's project dir
cd ..
cd ..
cd ..

echo "Client scripts available at client/target/tpe1-g3-client-1.0-SNAPSHOT"