#!/usr/bin/env bash

function waitAndLog {

  service=$1
  logRegex=$2

  touch -d '+5 minute' .timeout
  echo -n "Waiting for ${service} to start up...";
  while ! docker-compose -f docker-compose-bamboo.yml logs | tee docker-compose.log | egrep "${logRegex}" 1> /dev/null;
  do
    echo -n ".";
    sleep 1;
    touch .current;
    if [ .current -nt .timeout ]; then
      echo "Service startup timed out!"
      cleanup
      exit 1;
    fi
  done

  echo
  echo "Service ${service} started."
}

function getComposeIpAddress {

  service=$1

  docker-compose -f docker-compose-bamboo.yml ps -q ${service} | docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $(xargs)
}