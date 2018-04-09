package t32games.chameleon.presenter;

public class GameAction {
    private GameActionType type;
    private int player;
    private int color;

    public GameAction(GameActionType type, int player, int color){
        this.type=type;
        this.player=player;
        this.color=color;
    }

    public GameActionType getType() {
        return type;
    }

    public int getPlayer() {
        return player;
    }

    public int getColor() {
        return color;
    }

}
