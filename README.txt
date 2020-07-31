Authors: JANDU Harry & CISSE Demba

Prerequisites for compilation
-----------------------------
JavaFX, OpenJDK or equivalent for Linux

Download link for JavaFX: https://gluonhq.com/products/javafx/

Compilation instructions
------------------------
We have included the source files for OpenJFX in the zip, follow the instructions to compile and execute the program (on a linux machine)

	javac --module-path javafx-sdk-12.0.1/lib --add-modules javafx.controls *.java
	java --module-path javafx-sdk-12.0.1/lib --add-modules javafx.controls MainApp

Or you could use the `execute.sh` file that will compile and execute the program for you

For further details on compilation, consult the documentation with detailed and easy explanations
	https://openjfx.io/openjfx-docs/#install-javafx

