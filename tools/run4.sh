#!/usr/bin/env sh
./play_one_game_4bots.sh "java -Xshare:off -classpath ../build/classes MyBot" | java -jar visualizer.jar
