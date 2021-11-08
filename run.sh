#!/bin/sh

if [ ! -f x2svg.jar ]
then
	echo "No x2svg.jar found. Can not continue"
	echo "Run ant first"
	exit 1;
fi

DTDPARSER=lib/dtdparser-1.21/dtdparser121.jar
FOP=lib/fop-0.95
#
FOPLIBS=$FOP/build/fop.jar
FOPLIBS=$FOPLIBS:$FOP/lib/batik-all-1.7.jar
FOPLIBS=$FOPLIBS:$FOP/lib/commons-io-1.3.1.jar
FOPLIBS=$FOPLIBS:$FOP/lib/commons-logging-1.0.4.jar
FOPLIBS=$FOPLIBS:$FOP/lib/avalon-framework-4.2.0.jar
FOPLIBS=$FOPLIBS:$FOP/lib/xercesImpl-2.7.1.jar
FOPLIBS=$FOPLIBS:$FOP/lib/xmlgraphics-commons-1.3.1.jar
FOPLIBS=$FOPLIBS:$FOP/lib/xml-apis-1.3.04.jar
FOPLIBS=$FOPLIBS:$FOP/lib/xml-apis-ext-1.3.04.jar

CP=$DTDPARSER:$FOPLIBS:x2svg.jar:.

pwd

# Sample command line:
# java -cp $CP de.bsd.x2svg.X2Svg -p /tmp/test.pdf -o /tmp/test.svg sample.dtd root
java -Xmx512m -cp $CP de.bsd.x2svg.X2Svg $*
