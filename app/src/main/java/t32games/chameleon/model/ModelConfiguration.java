package t32games.chameleon.model;

public class ModelConfiguration {
    private boolean twoPlayers;
    private int numberOfColors;

    public ModelConfiguration(boolean twoPlayers, int numberOfColors) {
        this.twoPlayers = twoPlayers;
        this.numberOfColors = numberOfColors;
    }

    public boolean isTwoPlayers() {
        return twoPlayers;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

}
