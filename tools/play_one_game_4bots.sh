#!/usr/bin/env sh
./playgame.py -SoeEIO --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --turns 1000 --turntime 6000000 --loadtime 60000000 --map_file maps/official/maze_04p_01.map "$@" "python sample_bots/python/HunterBot.py" "python sample_bots/python/HunterBot.py" "python sample_bots/python/GreedyBot.py"
