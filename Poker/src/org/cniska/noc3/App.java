package org.cniska.noc3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Application that tracks the progress of a game of Poker.
 */
class App {
    /*
     * Vi vill ha ett program som hjälper oss att registrera ett poker spels spelmärken. Vi antar jämnt 5 deltagare
     * och 20 utdelningar, alltså 20 rundor poker.
     *
     * För varje deltagare som MÅSTE vara OBJEKT läser vi in följande data via tangentbordet:
     * • deltagarens namn (bara efternamn räcker)
     * • förening deltagaren representerar
     * ...och vi anger alla deltagare
     * • spelmärken av värde 100 till att börja med
     * ... och det som vi vill beräkna är:
     * • antal spelmärken kumulativt
     *
     * Spelmärkens tilldelning beräknas med hjälp av följande regler:
     *
     * Kumulativa spelmärksvärdet är summan efter den i:te utdelningen. Alltså resultatet är resultatet från
     * denna utdelning + resultatet från i - 1 utdelning + i - n utdelning osv. då i >= 0. En spelare kan inte låta bli
     * att delta i en utdelning, men nog sluta efter ante (insatsen för varje giv – eng. blind). Ante antar vi vara 1
     * spelmärke. Således, före runda 17 (efter 16) har alla spelare minst 3 spelmärken kvar och en sådan spelare
     * kan endast lägga in ante.
     */

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
        int numberOfLeftOverChipsPerPlayer = numberOfLeftOverChips > 0
                ? (int) Math.ceil(this.config.numberOfPlayers / numberOfLeftOverChips)
                : 0;
        int numberOfChipsPerPlayer = this.config.totalNumberOfChips / numberOfPlayers;
        int numberOfChips;

        boolean isInputValid;

        // a) 7p. Ber användaren ange namn och klubb för alla spelare. Efter att dessa angetts i början, kan inte flera
        // spelare registreras.

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
    private void askForResultForRound(int roundNumber) {
        int playerResult;
        boolean isNumberOfChipsValid;

        for (Player player : this.players) {
            isNumberOfChipsValid = false;

            while (!isNumberOfChipsValid) {
                // c) 2p. Kontrollerar att angivna resultatet är möjligt. Vi antar endast heltal för spelmärken, alltså -4 är ok, 31
                // är ok, -1.5 är fel likaså är 11.78 fel.

                playerResult = this.askQuestionAndExpectAnInteger("Ange resultat för " + player.getName() + " representerande " + player.getClub());

                if (Logic.checkThatPlayerHasEnoughChipsAfterApplyingResult(roundNumber, this.config.numberOfRounds, player, playerResult)) {
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
     * Asks the user a question and expects the answer to be an integer.
     *
     * @param question The question to ask
     * @return The answer
     */
    private int askQuestionAndExpectAnInteger(String question) {
        int answer = 0;
        boolean isAnswerAnInteger = false;

        while (!isAnswerAnInteger) {
            try {
                answer = Integer.parseInt(this.askQuestion(question));
                isAnswerAnInteger = true;
            } catch (NumberFormatException exception) {
                // If we get here, we know that the user did not input an integer, so we will ask the question again.
                UI.outputError("Du måste ge ett heltal!");
            }
        }

        return answer;
    }

    /**
     * "Plays" a round of Poker by asking the user for the result of that round.
     *
     * @param roundNumber The round number.
     */
    private void playRound(int roundNumber) {
        // b) 6p. Ber i tur och ordning om alla spelares resultat från en runda poker med spelarens namn, klubb och
        // rund-nummer, t.ex. "Runda 3, ange Ingvar representerande Infå resultat". Alltså, givet att Ingvar Infå, Stina
        // KK, Filippa MK, Martin Sigma och Lisa Bio är angivna vid inläsningsfasen, börjar programmet med att be
        // "Runda 1, ange Ingvar Infå resultat" varefter och ber "Runda 1, ange Stina representerande KK resultat",
        // ber om "Runda 1, ange Filippa representerande MK resultat", ber om "Runda 1, ange Martin
        // representerande Sigma resultat" och till slut "Runda 1, ange Lisa representerande Bio resultat" varefter
        // "Runda 2, ange Ingvar representerande Infå resultat" ombes givet att inte mellanrapport angivits i enlighet
        // med Mom e). Vi antar också att en spelare aldrig spelar slut sina spelmärken, alltså ifall en spelare har ett
        // resultat på 3 efter 17 rundor, är hennes resultat för de resterade rundorna som värst -1.

        Player[] previousPlayers;
        boolean isRoundValid = false;

        UI.outputHeading("Runda " + roundNumber + ":");

        while (!isRoundValid) {
            previousPlayers = Utils.clonePlayers(this.players);
            this.askForResultForRound(roundNumber);

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
        // e) 4p. Skriver, ifall så ombeds med input "mellanraport" (alternativt ett knapptryck grafiskt) en
        // mellanrapport om ställningen, t.ex. EFTER 5 rundor kunde detta vara
        //
        // "Ingvar representerande Infå har 128 spelmärken <nästa rad>
        // Stina representerande KK har 97 spelmärken <nästa rad>
        // Filippa representerande MK har 135 spelmärken <nästa rad>
        // Martin representerande Sigma har 88 spelmärken <nästa rad>
        // Lisa representerande Bio har 52 spelmärken"
        //
        // Detta ska vara möjligt endast före en angivning av en rundas resultat, alltså inte i mitten av
        // resultatinmatningen!

        UI.outputHeading("Mellanrapport för runda " + roundNumber + ":");
        this.outputPlayers(this.players);
        UI.outputDivider();
    }

    /**
     * Outputs the details for the given players.
     *
     * @param players A list of players to output
     */
    private void outputPlayers(Player[] players) {
        for (Player player : players) {
            UI.outputLine(player.getName() + " representerande " + player.getClub() + " har " + player.getNumberOfChips() + " spelmärken.");
        }
    }

    /**
     * Outputs the result of the game.
     */
    private void outputResult() {
        // f) 3p. När alla deltagares resultat för alla 20 rundor angivits, skriver programmet ut en resultatlista (behöver
        // inte vara sorterad)

        UI.outputHeading("Slutresultatet");
        this.outputPlayers(this.players);
        UI.outputDivider();

        // g) 4p. Sortera listan och skriv ut de tre första i ordning med en ”medalj” tilldelad. Observera att en medalj
        // kan delas (två kan vinna guld i vilket fall ingen får silver).

        Player[] players = Utils.clonePlayers(this.players);

        // Sort the players so that the one with the most chips is placed first (see Player::compareTo).
        Arrays.sort(players);

        Player[] topThreePlayers = Logic.dealMedals(players);

        UI.outputHeading("Medaljutdelning");

        for (Player player : topThreePlayers) {
            UI.outputLine("Spelare " + player.getName() + " representerande " + player.getClub() + " vann " + player.getMedal() + " med " + player.getNumberOfChips() + " spelmärken.");
        }
    }

    /**
     * Runs the application.
     */
    void run() {
        int roundNumber = 1;
        int action;
        boolean isActionValid;

        this.players = this.createPlayers(config.numberOfPlayers);

        while (roundNumber <= this.config.numberOfRounds) {
            isActionValid = false;

            while (!isActionValid) {
                action = this.askQuestionAndExpectAnInteger("Skriv 1 för att börja nästa runda eller 2 för mellanrapport");

                switch (action) {
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
