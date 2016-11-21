package org.cniska.noc3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Application that tracks the progress of a game of Poker.
 */
class App {

    private BufferedReader input;
    private Config config;
    private Player[] players;

    /**
     * Creates a new application with the given configuration.
     *
     * @param config The application configuration.
     */
    App(Config config) {
        this.input = new BufferedReader(new InputStreamReader(System.in));
        this.config = config;
    }

    /**
     * Creates the given amount of players for a game of Poker.
     *
     * @param numberOfPlayers The number of players
     * @return The players created
     */
    private Player[] createPlayers(int numberOfPlayers) {
        Player[] players = new Player[numberOfPlayers];

        String input;
        String[] playerNameAndClub = new String[2];

        int numberOfChipsDealt = 0;
        int numberOfLeftOverChips = this.config.totalNumberOfChips % numberOfPlayers;
        int numberOfLeftOverChipsPerPlayer = (int) Math.ceil(numberOfPlayers / numberOfLeftOverChips);
        int numberOfChipsPerPlayer = this.config.totalNumberOfChips / numberOfPlayers;
        int numberOfChips;

        boolean isInputValid;

        for (int i = 0; i < players.length; i++) {
            isInputValid = false;

            while (!isInputValid) {
                input = this.askQuestion("Ange spelarens namn samt klubb (t.ex. \"Ingvar Infå\")");
                playerNameAndClub = input.split(" ");
                isInputValid = playerNameAndClub.length == 2;
            }

            numberOfChips = Logic.calculateNumberOfPlayerChips(
                    numberOfChipsDealt,
                    numberOfLeftOverChipsPerPlayer,
                    numberOfChipsPerPlayer,
                    this.config.totalNumberOfChips
            );

            players[i] = new Player(playerNameAndClub[0], playerNameAndClub[1], numberOfChips);

            numberOfChipsDealt += numberOfChips;
        }

        return players;
    }

    /**
     * Asks the user for the result for all players from the given round.
     *
     * @param roundNumber The round number
     */
    private void askForRoundResult(int roundNumber) {
        int playerResult;
        boolean isNumberOfChipsValid;

        for (Player player : this.players) {
            isNumberOfChipsValid = false;

            while (!isNumberOfChipsValid) {
                playerResult = Integer.parseInt(this.askQuestion("Ange resultat för " + player.getName() + " representerande " + player.getClub()));

                if (Logic.checkIsPlayerResultPossible(roundNumber, this.config.numberOfRounds, player, playerResult)) {
                    player.applyResult(playerResult);
                    isNumberOfChipsValid = true;
                } else {
                    UI.outputError("Resultatet du gav är inte möjligt!");
                }
            }
        }
    }

    /**
     * Asks the user a question.
     *
     * @param question The question to ask
     * @return The answer
     */
    private String askQuestion(String question) {
        String answer = null;

        try {
            UI.outputLine(question + ": ");
            answer = this.input.readLine();
        } catch (IOException e) {
            // We should never get here, so we might as well silence this error.
        }

        return answer;
    }

    /**
     * "Plays" a round of Poker by asking the user for the result of that round.
     *
     * @param roundNumber The round number.
     */
    private void playRound(int roundNumber) {
        Player[] previousPlayers;
        boolean isRoundValid = false;

        UI.outputHeading("Runda " + roundNumber + ":");

        while (!isRoundValid) {
            previousPlayers = Utils.clonePlayers(this.players);
            this.askForRoundResult(roundNumber);

            if (Logic.checkTotalNumberOfChipsForPlayers(this.players, this.config.totalNumberOfChips)) {
                isRoundValid = true;
            } else {
                UI.outputError("Spelarnas resultat stämmer inte ihop med det totala antalet spelmärken!");
                this.players = previousPlayers;
            }
        }
    }

    /**
     * Outputs the details for the given round.
     *
     * @param roundNumber The round number.
     */
    private void outputRound(int roundNumber) {
        UI.outputHeading("Mellanrapport för runda " + roundNumber + ":");

        for (Player player : players) {
            UI.outputLine(player.getName() + " representerande " + player.getClub() + " har " + player.getNumberOfChips() + " spelmärken.");
        }

        UI.outputDivider();
    }

    /**
     * Outputs the result of the game.
     */
    private void outputResult() {
        Player[] players = Utils.clonePlayers(this.players);

        // Sort the players so that the one with the most chips is placed first (see Player::compareTo).
        Arrays.sort(players);

        Player[] topThreePlayers = Logic.dealMedals(players);

        UI.outputDivider();

        for (Player player : topThreePlayers) {
            UI.outputLine("Spelare " + player.getName() + " representerande " + player.getClub() + " vann " + player.getMedal() + " med " + player.getNumberOfChips() + " spelmärken.");
        }
    }

    /**
     * Runs the application.
     */
    void run() {
        int roundNumber = 1;
        String action;
        boolean isActionValid;

        this.players = this.createPlayers(config.numberOfPlayers);

        while (roundNumber <= this.config.numberOfRounds) {
            isActionValid = false;

            while (!isActionValid) {
                action = this.askQuestion("Skriv 1 för att börja nästa runda eller 2 för mellanrapport");

                switch (Integer.parseInt(action)) {
                    case 1:
                        this.playRound(roundNumber);
                        roundNumber++;
                        isActionValid = true;
                        break;

                    case 2:
                        this.outputRound(roundNumber);
                        isActionValid = true;
                        break;

                    default:
                        UI.outputError("Du måste skriva antingen 1 eller 2.");
                        break;
                }
            }
        }

        this.outputResult();
    }
}
