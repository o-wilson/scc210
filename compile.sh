#!/bin/bash
#Generate list of source files and then pass to the java compiler

if [[ ! $(ldconfig -p | grep libopenal) ]]; then
    sudo apt-get install libopenal1 -y
fi

find -name "*.java" > sources.txt
javac -d out -cp lib/jsfml.jar @sources.txt