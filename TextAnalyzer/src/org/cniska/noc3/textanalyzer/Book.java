package org.cniska.noc3.textanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

class Book {
    private static String anythingButWordsAndSpacesPattern = "(?i)[^a-zåäö ]+";
    private ArrayList<String> words = new ArrayList<>();

    void readWordsFromUri(String uri) {
        ArrayList<String> readWords = new ArrayList<>();

        UI.outputLine("Läser ord från boken: " + uri);

        try {
            URL url = new URL(uri);
            InputStreamReader input = new InputStreamReader(url.openStream());
            BufferedReader reader = new BufferedReader(input);
            String line;
            String[] wordsOnLine;

            while ((line = reader.readLine()) != null) {
                wordsOnLine = line.replaceAll(anythingButWordsAndSpacesPattern, "").split(" ");

                for (String word : wordsOnLine) {
                    readWords.add(word.toLowerCase());
                }
            }
        } catch (IOException e) {
            UI.outputLine("Ett fel uppstod");
        }

        UI.outputLine("- Boken innehöll " + readWords.size() + " ord");

        words = readWords.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        UI.outputLine("- Boken innehöll " + words.size() + " olika ord");
    }

    ArrayList<String> getWords() {
        return words;
    }
}
