#!/bin/bash

mvn clean package
docker build . -t songsws
docker run -p 8080:8080 -p 3.89.155.213:8080:8080 -d songsws