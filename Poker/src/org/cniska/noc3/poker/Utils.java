package org.cniska.noc3.poker;

/**
 * Utilities used by the application.
 */
class Utils {

    /**
     * Clones the given list of players and returns the cloned list.
     *
     * @param players A list of players
     * @return The cloned list
     */
    static Player[] clonePlayers(Player[] players) {
        Player[] clonedPlayers = new Player[players.length];
        Player player;

        for (int i = 0; i < players.length; i++) {
            player = players[i];

            try {
                clonedPlayers[i] = player.clone();
            } catch (CloneNotSupportedException exception) {
                // We should never get here, so we might as well silence this error.
            }
        }

        return clonedPlayers;
    }
}
