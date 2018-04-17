package t32games.chameleon.presenter;

import io.reactivex.Observable;
import t32games.chameleon.model.SourceFieldState;
import t32games.chameleon.model.SourcePlayerPanelState;
import t32games.chameleon.model.WinEvent;


public interface PresenterFacade {
    public Observable<FragmentName> getFragmentControlState();
    public Observable<MenuState> getMenuState();
    public Observable<SourceFieldState> getFieldState();
    public Observable<SourcePlayerPanelState> getPlayerPanelState();
    public Observable<WinEvent> getWinState();
}
