#!/bin/sh
#generate documentation
javadoc -d ./doc $(find -name "*.java")>/dev/null 2>&1

#builds java files
javac -d build $(find ! -name "Test*" -name "*.java")

#runs java files
#java -cp .:build: ija.main.Init

#creates jar
jar -cvfm ./dest-server/ija2014-server.jar ./Manifest -C build ija main >/dev/null 2>&1

#runs jar
java -jar ./dest-server/ija2014-server.jar

