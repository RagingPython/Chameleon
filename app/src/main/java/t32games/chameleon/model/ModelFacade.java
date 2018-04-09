package t32games.chameleon.model;

import io.reactivex.Observable;

public interface ModelFacade {
    void setCommand(Observable<ModelCommand> command);
    Observable<Integer> getTimerState();
    Observable<FieldState> getFieldState();
    Observable<PlayerPanelState> getPlayerPanelState();
    Observable<WinEvent> getWinEvent();
}
