package t32games.chameleon.presenter;


import io.reactivex.Observable;

public interface ViewFacade {
    public Observable<MenuAction> getMenuActions();
    public Observable<GameAction> getGameActions();
    public Observable<NewAction> getNewActions();
    public Observable<Integer> getWinActions();
}
