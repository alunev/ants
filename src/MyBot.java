import java.io.IOException;
import java.util.List;

import com.alunev.ants.bot.AstarBot;
import com.alunev.ants.bot.Bot;
import com.alunev.ants.io.AntsInputParser;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.io.InputReader;
import com.alunev.ants.io.OutputWriter;

/**
 * Starter bot implementation.
 */
public class MyBot {

    /**
     * Main method executed by the game engine for starting the bot.
     *
     * @param args command line arguments
     *
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        AntsInputParser inputParser = new AntsInputParser();
        InputReader inputReader = new InputReader(System.in);
        OutputWriter outputWriter = new OutputWriter(System.out);

        GameSetup gameSetup = inputParser.parseSetup(inputReader.readGameSetup());

        Bot astarBot = new AstarBot();
        astarBot.beforeStart(gameSetup);

        List<String> batch = null;
        GameState gameState = new GameState(gameSetup.getRows(), gameSetup.getCols());
        while (!(batch = inputReader.readGameUpdate()).isEmpty()) {
            gameState.merge(inputParser.parseUpdate(batch, gameSetup));

            astarBot.beforeUpdate(gameState);
            outputWriter.writeOrders(astarBot.doTurn());
            astarBot.afterUpdate();
        }
    }
}
