package org.cniska.noc3.poker;

import java.util.Arrays;

/**
 * Logic used by the application.
 */
class Logic {

    /**
     * Calculates the maximum amount of chips that be assigned to a player.
     *
     * @param numberOfChipsDealt             The number of chips dealt so far
     * @param numberOfLeftOverChipsPerPlayer The number of chips (potentially) left over for each player
     * @param numberOfChipsPerPlayer         The minimum amount of chips available for each player
     * @param totalNumberOfChips             The total amount of chips in the game
     * @return The number of chips.
     */
    static int calculateNumberOfPlayerChips(int numberOfChipsDealt, int numberOfLeftOverChipsPerPlayer, int numberOfChipsPerPlayer, int totalNumberOfChips) {
        int maximumNumberOfChipsPerPlayer = numberOfChipsPerPlayer + numberOfLeftOverChipsPerPlayer;

        // NOTE:
        // This is a bit overkill, but here we ensure that the chips are evenly distributed amongst the players
        // (instead of just assuming that the total number of chips is evenly dividable by the number of players).
        // e.g. Wrong: 500 / 3 = 166, 166 x 3 = 498; Correct: 167, 167, 166 = 500
        return (numberOfChipsDealt + maximumNumberOfChipsPerPlayer) <= totalNumberOfChips
                ? maximumNumberOfChipsPerPlayer
                : numberOfChipsPerPlayer;
    }

    /**
     * Checks that the total amount of chips the given players have is equal to the total number of chips available.
     *
     * @param players            A list of players
     * @param totalNumberOfChips The total number of chips available
     * @return Whether or not the total amount of chips the players have is correct
     */
    static boolean checkTotalNumberOfChipsForPlayers(Player[] players, int totalNumberOfChips) {
        // d) 4p. Kontrollerar att summan av resultaten för en runda är 0, alltså att lika mycket förlorades som de(n)
        // som vann och att summa för allas märken alltid är 500. Flera än en spelare kan vinna en runda.

        int totalNumberOfChipsForPlayers = 0;

        for (Player player : players) {
            totalNumberOfChipsForPlayers += player.getNumberOfChips();
        }

        return totalNumberOfChipsForPlayers == totalNumberOfChips;
    }

    /**
     * Checks that the given player has enough chips left for blinds after applying the given result.
     *
     * @param roundNumber    The round number
     * @param numberOfRounds The number of rounds in the game
     * @param player         The player to check the result for
     * @param playerResult   The player's result
     * @return Whether or not the result is possible
     */
    static boolean checkThatPlayerHasEnoughChipsAfterApplyingResult(int roundNumber, int numberOfRounds, Player player, int playerResult) {
        int numberOfChipsRequiredForBlinds = numberOfRounds - roundNumber;
        int numberOfChipsAfterApplyingResult = player.getNumberOfChips() + playerResult;

        return numberOfChipsAfterApplyingResult >= numberOfChipsRequiredForBlinds;
    }

    /**
     * Deals the medals for the given players.
     *
     * @param players The players to whom deal the medals
     * @return The players who received a medal
     */
    static Player[] dealMedals(Player[] players) {
        Player[] topThreePlayers = Arrays.copyOfRange(players, 0, 3);
        String[] medals = {"Guld", "Silver", "Brons"};
        Player player, nextPlayer;
        int currentMedalIndex = 0;
        String currentMedal;

        for (int i = 0; i < topThreePlayers.length; i++) {
            player = topThreePlayers[i];
            currentMedal = medals[currentMedalIndex];
            player.setMedal(currentMedal);

            // NOTE: If we are at the last index, we do not have a "next player".
            nextPlayer = i < (topThreePlayers.length - 1) ? topThreePlayers[i + 1] : null;

            if (nextPlayer != null && player.hasMoreChipsThanPlayer(nextPlayer)) {
                currentMedalIndex++;
            }
        }

        return topThreePlayers;
    }
}
