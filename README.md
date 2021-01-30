# Game

## Compiling and Running

Compilation works by generating a list of .java files in the project and then passing them to the Java compiler

### Linux (Ubuntu)

From the root directory (Game) run the following from a terminal

*Bash scripts may need to be marked as executable for first time use using `chmod 700 <file>.sh`*

`./compile.sh`

`./run.sh`

### Windows

From the root directory (Game) run the following:

*You may need to install [Microsoft Visual C++ 2010 Redistributable Package (x64)](http://www.microsoft.com/en-us/download/details.aspx?id=14632) for some dependencies*

`compile.bat`

`run.bat`

*NB: if executing from PowerShell, batch files need to be prefaced with `.\`*

## Generating JavaDoc

### Windows or Linux

**Generate a list of sources**

*Only necessary if a sources.txt does not already exist, or if more source files have been added since it was generated*

Linux: `find -name "*.java" > sources.txt`

Windows: `dir /s /B *.java > sources.txt`

**Generate docs**

`javadoc -d javadoc @sources.txt -cp lib/jsfml.jar` (either OS)

## General

JavaDoc and the `out` folder (class files) are ignored by git as they don't need to be tracked, as is the `sources.txt` file, which can easily be generated and doesn't need to be tracked

### Preferred Filetypes

`.ogg` for audio - able to load longer audio files due to more compression

`.png` for sprites - allows for transparency

### Common Colours

![#45283c](https://placehold.it/150x40/45283c/ffffff?text=45283c)

![#ac3232](https://placehold.it/150x40/ac3232/ffffff?text=ac3232)

![#df7126](https://placehold.it/150x40/df7126/ffffff?text=df7126)

![#fbf236](https://placehold.it/150x40/fbf236/000000?text=fbf236)