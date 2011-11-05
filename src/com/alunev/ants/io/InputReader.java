package com.alunev.ants.io;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles system input stream reading.
 */
public class InputReader {
    private static final String READY = "ready";
    private static final String GO = "go";

    private final InputStream inputStream;

    public InputReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<String> readGameSetup() throws IOException {
        return readBatch();
    }

    public List<String> readGameUpdate() throws IOException {
        return readBatch();
    }

    private List<String> readBatch() throws IOException {
        int c;
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<String>();
        while ((c = inputStream.read()) >= 0) {
            if (c == '\r' || c == '\n') {
                if (isBatchEnd(line)) {
                    break;
                }

                if (line.length() > 0) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
            } else {
                line = line.append((char)c);
            }
        }

        return lines;
    }

    private boolean isBatchEnd(StringBuilder line) {
        if (READY.equals(line.toString()) || GO.equals(line.toString())) {
            return true;
        }

        return false;
    }
}
