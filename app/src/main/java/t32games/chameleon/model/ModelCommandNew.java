package t32games.chameleon.model;

public class ModelCommandNew extends ModelCommand {
    private int xSize;
    private int ySize;
    private boolean twoPlayers;
    private int numberOfColors;
    private int timeLimit;

    public ModelCommandNew(int xSize, int ySize, boolean twoPlayers, int numberOfColors) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.twoPlayers = twoPlayers;
        this.numberOfColors = numberOfColors;
        this.timeLimit = timeLimit;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public boolean isTwoPlayers() {
        return twoPlayers;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
}
