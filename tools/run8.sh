#!/usr/bin/env sh
./play_one_game_8bots.sh "java -Xshare:off -classpath ../build/classes MyBot" | java -jar visualizer.jar
