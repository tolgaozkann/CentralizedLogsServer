#!/bin/bash

WATCH_DIR="src/"
DEBOUNCE_DELAY=2
MAVEN_BUILD="mvn clean install"
RUN_APP="java -jar target/LogServer-1.0-SNAPSHOT.jar"
APP_PID=0

## Check if inotifywait is installed
if ! [ -x "$(command -v inotifywait)" ]; then
  echo "inotify-tools is not installed. Installing now..."
  sudo apt-get install -y inotify-tools
fi

function start_app() {
    echo "Starting App..."
    $RUN_APP &
    APP_PID=$!
    echo "Application started with PID: $APP_PID"
}

function stop_app() {
    if [ $APP_PID -ne 0 ]; then
        echo "Stopping application with PID: $APP_PID"
        kill $APP_PID
        wait $APP_PID 2>/dev/null
        APP_PID=0
    fi
}

function rebuild_and_restart() {
    stop_app
    echo "Building the project..."
    $MAVEN_BUILD
    start_app
}

trap 'stop_app; echo "Script terminated"; exit' SIGINT SIGTERM

echo "Initial build and start..."
$MAVEN_BUILD
start_app

# Watch and debounce changes
LAST_BUILD_TIME=0
while true; do
    FILE_CHANGED=$(inotifywait -r -e modify,create,delete --format '%w%f' -qq $WATCH_DIR)
    CURRENT_TIME=$(date +%s)

    if [[ $((CURRENT_TIME - LAST_BUILD_TIME)) -gt $DEBOUNCE_DELAY ]]; then
        echo "Change detected in $FILE_CHANGED. Rebuilding and restarting after $DEBOUNCE_DELAY seconds of inactivity..."
        sleep $DEBOUNCE_DELAY
        LAST_BUILD_TIME=$(date +%s)
        rebuild_and_restart
    fi
done
