#!/bin/bash
#Generate list of source files and then pass to the java compiler

find -name "*.java" > sources.txt
javac -d out -cp lib/jsfml.jar @sources.txt