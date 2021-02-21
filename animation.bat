@echo off

call "compile.bat"

if "%~1"=="" GOTO none
if "%~2"=="" GOTO one
GOTO both

:one
java -cp out;lib/jsfml.jar fullthrottle.AnimationPreview "%~1"
GOTO :EOF

:both
java -cp out;lib/jsfml.jar fullthrottle.AnimationPreview "%~1" "%~2"
GOTO :EOF

:none
echo No spritesheet given