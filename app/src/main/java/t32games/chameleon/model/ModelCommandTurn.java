package t32games.chameleon.model;

public class ModelCommandTurn extends ModelCommand {
    private int player;
    private int color;

    public ModelCommandTurn(int player, int color) {
        this.player = player;
        this.color = color;
    }

    public int getPlayer() {
        return player;
    }

    public int getColor() {
        return color;
    }
}
