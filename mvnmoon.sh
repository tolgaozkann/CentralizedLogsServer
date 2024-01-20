#!/bin/bash
##
# This script used for make development environment used for faster development,
#
##

WATCH_DIR="src/"

DEBOUNCE_DELAY=1
MAVEN_BUILD="mvn clean install"

RUN_APP="java -jar target/LogServer-1.0-SNAPSHOT.jar"

APP_PID=0



## Check inotifywait is installed or not
if ! [ -x "$(command -v inotifywait)" ]; then
  echo "sudo apt install inotify-tools"
fi


function start() {
    echo "Starting App"
    $RUN_APP &
    APP_PID=$!
    echo "Application started with PID $APP_PID"
}

function stop() {
    if [ $APP_PID -ne 0 ]; then
        echo "Stopping application with PID $APP_PID"
        kill $APP_PID
        wait $APP_PID 2>/dev/null
        APP_PID=0
    fi
}



function clean() {
    echo "Exiting..."
    stop
    exit 0
}

rebuild_and_restart() {
    stop
    $MAVEN_BUILD
    start
}

trap clean SIGINT

# Initial build and start
$MAVEN_BUILD
start

# Watch for changes in the directory
while true; do
    inotifywait -r -e modify,create,delete --format '%w%f' -qq $WATCH_DIR
    echo "Change detected. Rebuilding and restarting in $DEBOUNCE_DELAY seconds..."
    sleep $DEBOUNCE_DELAY
    rebuild_and_restart
done


