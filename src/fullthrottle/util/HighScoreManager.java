package fullthrottle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {

    public static void main(String[] args) {
        List<HighScore> scores = HighScoreManager.getHighScores();

        for (HighScore s : scores) {
            System.out.println(s.name + " : " + s.score);
        }

    }

    public static final class HighScore implements Comparable<HighScore> {
        public String name;
        public int score;

        private HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(HighScore hs) {
            return (this.score == hs.score) ? hs.name.compareTo(this.name) : (this.score < hs.score) ? -1 : 1;
        }
    }

    public static List<HighScore> getHighScores() {
        return getHighScores(0);
    }

    public static List<HighScore> getHighScores(int limit) {
        List<HighScore> scores = new ArrayList<>();

        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("./.highscores"));
        } catch (FileNotFoundException e) {
            try {
                (new File("./.highscores")).createNewFile();
                reader = new BufferedReader(new FileReader("./.highscores"));
            } catch (IOException ex) {
                ex.printStackTrace();
                return scores;
            }
        }

        try {
            String line = reader.readLine();
            while (line != null) {
                if (!line.contains(":")) {
                    line = reader.readLine();
                    continue;
                }
                int split = line.indexOf(':');
                String name = line.substring(0, split);
                int score = Integer.parseInt(line.substring(split + 1));

                HighScore s = new HighScore(name, score);
                scores.add(s);

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return scores;
        }

        Collections.sort(scores);
        Collections.reverse(scores);

        if (limit != 0)
            scores = scores.subList(0, Math.min(scores.size(), limit));

        return scores;
    }

    public static void addHighScore(String name, int score) {
        try{
            Files.writeString(
                Paths.get("./.highscores"),
                name + ":" + score + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
