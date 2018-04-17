package t32games.chameleon.model;

import io.reactivex.Observable;

public interface ModelFacade {
    void setCommand(Observable<ModelCommand> command);
    Observable<SourceFieldState> getFieldState();
    Observable<SourcePlayerPanelState> getPlayerPanelState();
    Observable<WinEvent> getWinEvent();
}
