#!/usr/bin/env sh
./play_one_game_8bots.sh "java -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -classpath ../build/classes MyBot" | java -jar visualizer.jar
