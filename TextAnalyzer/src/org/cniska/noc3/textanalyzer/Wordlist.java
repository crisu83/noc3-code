package org.cniska.noc3.textanalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Wordlist {
    private ArrayList<String> words;

    void readFromFile(String filePath) {
        BufferedReader reader;
        String line;

        ArrayList<String> readWords = new ArrayList<>();

        UI.outputLine("Läser in ordlistan: " + filePath);

        try {
            reader = new BufferedReader(new FileReader(filePath));

            while ((line = reader.readLine()) != null) {
                readWords.add(line.replaceAll("[ ,]+", ""));
            }
        } catch (IOException e) {
            UI.outputLine("Ett fel uppstod");
        }

        words = readWords.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        UI.outputLine("- Ordlistan innehöll " + words.size() + " olika ord");
    }

    void addWordsThatDontExist(ArrayList<String> wordsToAdd) {
        words = Stream.concat(words.stream(), wordsToAdd.stream())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    void shuffleWords() {
        Collections.shuffle(words);
    }

    void writeToFile(String filePath) {
        File file = new File(filePath);
        BufferedWriter writer;

        UI.outputLine("Skriver ut ordlistan: " + filePath);

        try {
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));

            for (String word : words) {
                writer.write(word + ",");
            }

            writer.close();
        } catch (IOException e) {
            UI.outputLine("Ett fel uppstod");
        }
    }

    int getNumberOfWords() {
        return words.size();
    }
}
