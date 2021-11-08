@echo off
if not exist x2svg.jar (
	echo "No x2svg.jar found. Can not continue"
	echo "Run ant first"
	exit /B 1
)

set DTDPARSER=lib/dtdparser-1.21/dtdparser121.jar
set FOP=lib/fop-0.95

set FOPLIBS=%FOP%/build/fop.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/batik-all-1.7.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/commons-io-1.3.1.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/commons-logging-1.0.4.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/avalon-framework-4.2.0.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/xercesImpl-2.7.1.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/xmlgraphics-commons-1.3.1.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/xml-apis-1.3.04.jar
set FOPLIBS=%FOPLIBS%;%FOP%/lib/xml-apis-ext-1.3.04.jar

set CP=%DTDPARSER%;%FOPLIBS%;x2svg.jar;.

rem Sample command line:
rem java -cp %CP% de.bsd.x2svg.X2Svg -c c:\tmp\test.pdf -o c:\tmp\test.svg sample.dtd root

java -cp %CP% de.bsd.x2svg.X2Svg %*
