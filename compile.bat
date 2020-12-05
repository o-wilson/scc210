@echo off

dir /s /B *.java > sources.txt
if not exist "out\" (
    md out
)
javac -d out -cp lib/jsfml.jar @sources.txt