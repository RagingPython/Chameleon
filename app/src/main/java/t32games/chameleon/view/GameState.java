package t32games.chameleon.view;


import t32games.chameleon.model.FieldState;
import t32games.chameleon.model.PlayerPanelState;

class GameState {
    private FieldState fieldState;
    private PlayerPanelState playerPanelState;
    private int timerState;

    public GameState(FieldState fieldState, PlayerPanelState playerPanelState, int timerState) {
        this.fieldState = fieldState;
        this.playerPanelState = playerPanelState;
        this.timerState = timerState;
    }

    public FieldState getFieldState() {
        return fieldState;
    }

    public PlayerPanelState getPlayerPanelState() {
        return playerPanelState;
    }

    public int getTimerState() {
        return timerState;
    }
}
