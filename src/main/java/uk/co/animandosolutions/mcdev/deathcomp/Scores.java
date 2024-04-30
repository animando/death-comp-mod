package uk.co.animandosolutions.mcdev.deathcomp;

public record Scores(String playerName, int deaths, double playTime) {
    public String toString() {
        return String.format("[name=%s, deaths=%s, playTime=%sh]", playerName, deaths, playTime);
    }
}