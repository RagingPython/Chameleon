package t32games.chameleon.view;


import t32games.chameleon.model.FieldState_;
import t32games.chameleon.model.PlayerPanelState;

class GameState {
    private FieldState_ fieldState;
    private PlayerPanelState playerPanelState;
    private int timerState;

    public GameState(FieldState_ fieldState, PlayerPanelState playerPanelState, int timerState) {
        this.fieldState = fieldState;
        this.playerPanelState = playerPanelState;
        this.timerState = timerState;
    }

    public FieldState_ getFieldState() {
        return fieldState;
    }

    public PlayerPanelState getPlayerPanelState() {
        return playerPanelState;
    }

    public int getTimerState() {
        return timerState;
    }
}
