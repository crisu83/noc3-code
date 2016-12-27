package org.cniska.noc3.textanalyzer;

import java.util.ArrayList;

class App {
    private static final String BOOK_URI = "http://manybooks.net/send/1:text:.txt:text/topeliusz2724927249-8/topeliusz2724927249-8.txt";
    private static final String WORDLIST_FILE_PATH = "/Users/chris/ordlista.csv";
    private static final int TIMES_TO_ANALYZE = 1;

    void run() {
        Book book = new Book();
        book.readWordsFromUri(BOOK_URI);

        Wordlist wordlist = new Wordlist();
        wordlist.readFromFile(WORDLIST_FILE_PATH);

        for (int i = 0; i < TIMES_TO_ANALYZE; i++) {
            analyze(book, wordlist);
        }
    }

    void analyze(Book book, Wordlist wordlist) {
        long startTime, endTime;
        int numberOfWordsBefore, numberOfWordsAfter;

        UI.outputLine("Analyserar boken med ordlistan");

        numberOfWordsBefore = wordlist.getNumberOfWords();
        startTime = System.currentTimeMillis();

        ArrayList<String> wordsInBook = book.getWords();
        wordlist.shuffleWords();
        wordlist.addWordsThatDontExist(wordsInBook);

        endTime = System.currentTimeMillis();
        numberOfWordsAfter = wordlist.getNumberOfWords();

        UI.outputLine("- Analysen tog: " + (endTime - startTime) / 1000.0 + " sek.");
        UI.outputLine("- Antalet nya ord: " + (numberOfWordsAfter - numberOfWordsBefore));

        String[] fileParts = WORDLIST_FILE_PATH.split("\\.");
        wordlist.writeToFile(fileParts[0] + "_" + endTime + "." + fileParts[1]);
    }
}