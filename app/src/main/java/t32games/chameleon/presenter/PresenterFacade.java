package t32games.chameleon.presenter;

import io.reactivex.Observable;
import t32games.chameleon.model.FieldState;
import t32games.chameleon.model.PlayerPanelState;
import t32games.chameleon.model.WinEvent;


public interface PresenterFacade {
    public Observable<FragmentName> getFragmentControlState();
    public Observable<MenuState> getMenuState();
    public Observable<FieldState> getFieldState();
    public Observable<Integer> getTimerState();
    public Observable<PlayerPanelState> getPlayerPanelState();
    public Observable<WinEvent> getWinState();
}
