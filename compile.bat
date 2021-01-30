@echo off

if exist "sources.txt" (
    del sources.txt
)

SET "StartPath=%cd%\"

SetLocal EnableDelayedExpansion
FOR /f "tokens=*" %%f in ('dir /B /S "*.java"') DO (
    set "SubDirsAndFiles=%%f"
    set "SubDirsAndFiles=!SubDirsAndFiles:%StartPath%=!"
    echo !SubDirsAndFiles! >> sources.txt
)

if not exist "out\" (
    md out
)
javac -d out -cp lib/jsfml.jar @sources.txt