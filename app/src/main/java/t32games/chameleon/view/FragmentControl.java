package t32games.chameleon.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import t32games.chameleon.presenter.GameAction;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.NewAction;
import t32games.chameleon.presenter.PresenterFacade;
import t32games.chameleon.presenter.ViewFacade;

public class FragmentControl implements ViewFacade {

    private FrgMenu frgMenu = new FrgMenu();
    private FrgNew frgNew = new FrgNew();
    private FrgGame frgGame = new FrgGame();
    private FrgWin frgWin = new FrgWin();

    private FragmentManager fragmentManager;
    private CompositeDisposable disposables = new CompositeDisposable();
    private int containerId;


    public FragmentControl(FragmentManager fragmentManager, int containerId){
        this.fragmentManager=fragmentManager;
        this.containerId=containerId;
    }

    public void setPresenter(PresenterFacade p){
        //TODO set more!
        frgMenu.setMenuState(p.getMenuState().observeOn(AndroidSchedulers.mainThread()));
        frgGame.setFieldState(p.getFieldState().observeOn(AndroidSchedulers.mainThread()));
        frgGame.setPlayerPanelState(p.getPlayerPanelState().observeOn(AndroidSchedulers.mainThread()));
        frgWin.setWinEvent(p.getWinState().observeOn(AndroidSchedulers.mainThread()));

        //TODO Fragment switch:
        disposables.dispose();
        disposables = new CompositeDisposable();
        disposables.add(
            p.getFragmentControlState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o->{
                    FragmentTransaction t = fragmentManager.beginTransaction();
                    switch (o) {
                        case MENU:
                            t.replace(containerId, frgMenu);
                            break;
                        case NEW:
                            t.replace(containerId, frgNew);
                            break;
                        case GAME:
                            t.replace(containerId, frgGame);
                            break;
                        case WIN:
                            t.replace(containerId, frgWin);
                    }
                    t.commit();
                })
        );
    }

    @Override
    public Observable<MenuAction> getMenuActions() {
        return frgMenu.getMenuAction();
    }

    @Override
    public Observable<GameAction> getGameActions() { return frgGame.getGameAction(); }

    @Override
    public Observable<NewAction> getNewActions() { return frgNew.getNewAction();  }

    @Override
    public Observable<Integer> getWinActions() {
        return frgWin.getWinAction();
    }
}
