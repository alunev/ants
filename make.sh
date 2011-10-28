#!/bin/bash

# clean
rm build/classes/*.class
rm dist/MyBot.zip

# compile
javac -verbose -d build/classes -cp src src/*.java

# package
jar -cvmf Manifest.txt dist/MyBot.jar -C build/classes .
cd src
zip -r ../dist/MyBot.zip .
# cd ..
# mv src/MyBot.zip dist/

# clean
# rm *.class
