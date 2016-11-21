package org.cniska.noc3;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Represents a single player in a game of Poker.
 */
class Player implements Comparable<Player>, Cloneable {

    private String name;
    private String club;
    private int numberOfChips;
    private String medal;

    /**
     * Creates a new player.
     *
     * @param name The player name
     * @param club The club the player belongs to
     * @param numberOfChips The number of chips the player has
     */
    Player(String name, String club, int numberOfChips) {
        this.setName(name);
        this.setClub(club);
        this.setNumberOfChips(numberOfChips);
        this.setMedal(null);
    }

    @Override
    public int compareTo(Player other) {
        return other.numberOfChips - this.numberOfChips;
    }

    @Override
    protected Player clone() throws CloneNotSupportedException {
        return new Player(this.getName(), this.getClub(), this.getNumberOfChips());
    }

    /**
     * Returns whether this player as more chips than the given player.
     *
     * @param other The other player
     * @return The result
     */
    boolean hasMoreChipsThanPlayer(Player other) {
        return this.getNumberOfChips() > other.getNumberOfChips();
    }

    /**
     * Applies the given result by adding it to the number of chips this player has.
     *
     * @param result The result to add
     */
    void applyResult(int result) {
        this.numberOfChips += result;
    }

    int getNumberOfChips() {
        return numberOfChips;
    }

    private void setNumberOfChips(int numberOfChips) {
        this.numberOfChips = numberOfChips;
    }

    String getMedal() {
        return medal;
    }

    void setMedal(String medal) {
        this.medal = medal;
    }

    String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name.trim();
    }

    String getClub() {
        return club;
    }

    private void setClub(String club) {
        this.club = club.trim();
    }
}
