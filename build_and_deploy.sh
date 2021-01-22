#!/bin/bash

mvn clean package
docker build . -t songsws
docker run -d songsws