package com.alunev.ants.io;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles system input stream parsing.
 */
public class AntsInputParser implements InputParser  {
    private static final char COMMENT_CHAR = '#';

    private enum SetupToken {
        LOADTIME, TURNTIME, ROWS, COLS, TURNS, VIEWRADIUS2, ATTACKRADIUS2, SPAWNRADIUS2;

        private static final Pattern PATTERN = compilePattern(SetupToken.class);
    }

    private enum UpdateToken {
        W, A, F, D, H;

        private static final Pattern PATTERN = compilePattern(UpdateToken.class);
    }

    private static Pattern compilePattern(Class<? extends Enum> clazz) {
        StringBuilder builder = new StringBuilder("(");
        for (Enum enumConstant : clazz.getEnumConstants()) {
            if (enumConstant.ordinal() > 0) {
                builder.append("|");
            }
            builder.append(enumConstant.name());
        }
        builder.append(")");
        return Pattern.compile(builder.toString());
    }

    /**
     * Parses the setup information from system input stream.
     *
     * @param input setup information
     */
    @Override
    public GameSetup parseSetup(List<String> input) {
        int loadTime = 0;
        int turnTime = 0;
        int rows = 0;
        int cols = 0;
        int turns = 0;
        int viewRadius2 = 0;
        int attackRadius2 = 0;
        int spawnRadius2 = 0;

        for (String line : input) {
            line = removeComment(line);
            if (line.isEmpty()) {
                continue;
            }

            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }

            String token = scanner.next().toUpperCase();
            if (!SetupToken.PATTERN.matcher(token).matches()) {
                continue;
            }

            SetupToken setupToken = SetupToken.valueOf(token);
            switch (setupToken) {
                case LOADTIME:
                    loadTime = scanner.nextInt();
                break;
                case TURNTIME:
                    turnTime = scanner.nextInt();
                break;
                case ROWS:
                    rows = scanner.nextInt();
                break;
                case COLS:
                    cols = scanner.nextInt();
                break;
                case TURNS:
                    turns = scanner.nextInt();
                break;
                case VIEWRADIUS2:
                    viewRadius2 = scanner.nextInt();
                break;
                case ATTACKRADIUS2:
                    attackRadius2 = scanner.nextInt();
                break;
                case SPAWNRADIUS2:
                    spawnRadius2 = scanner.nextInt();
                break;
            }
        }

        return new GameSetup(loadTime, turnTime, rows, cols, turns, viewRadius2,
                attackRadius2, spawnRadius2);
    }

    /**
     * Parses the update information from system input stream.
     *
     * @param input update information
     */
    @Override
    public GameState parseUpdate(List<String> input, int rows, int cols) {
        GameStateBuilder gameStateBuilder = new GameStateBuilder(rows, cols);

        for (String line : input) {
            line = removeComment(line);

            if (line.isEmpty()) {
                continue;
            }

            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }

            String token = scanner.next().toUpperCase();
            if (!UpdateToken.PATTERN.matcher(token).matches()) {
                continue;
            }

            UpdateToken updateToken = UpdateToken.valueOf(token);
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            switch (updateToken) {
                case W:
                    gameStateBuilder.addWater(row, col);
                break;
                case A:
                    if (scanner.hasNextInt()) {
                        gameStateBuilder.addAnt(row, col, scanner.nextInt());
                    }
                break;
                case F:
                    gameStateBuilder.addFood(row, col);
                break;
                case D:
                    if (scanner.hasNextInt()) {
                        gameStateBuilder.removeAnt(row, col, scanner.nextInt());
                    }
                break;
                case H:
                    if (scanner.hasNextInt()) {
                        gameStateBuilder.addHill(row, col, scanner.nextInt());
                    }
                break;
            }
        }

        return gameStateBuilder.build();
    }

    /**
     * Finishes turn.
     */
    public void finishTurn() {
        System.out.println("go");
        System.out.flush();
    }

    private String removeComment(String line) {
        int commentCharIndex = line.indexOf(COMMENT_CHAR);
        String lineWithoutComment;
        if (commentCharIndex >= 0) {
            lineWithoutComment = line.substring(0, commentCharIndex).trim();
        } else {
            lineWithoutComment = line;
        }
        return lineWithoutComment;
    }
}