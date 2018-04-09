package t32games.chameleon.model;

public class WinEvent {
    private boolean twoPlayers;
    private int winner;
    private int[] score;

    public WinEvent(boolean twoPlayers, int[] score, int winner) {
        this.twoPlayers = twoPlayers;
        this.score = score.clone();
        this.winner = winner;
    }

    public boolean isTwoPlayers() {
        return twoPlayers;
    }

    public int getScore(int i) {
        return score[i];
    }

    public int getWinner() {
        return winner;
    }

    public int[] getScore() {
        return score;
    }
}
