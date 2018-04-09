package t32games.chameleon.view;

import android.app.FragmentController;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import t32games.chameleon.presenter.FragmentName;
import t32games.chameleon.presenter.GameAction;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.NewAction;
import t32games.chameleon.presenter.PresenterFacade;
import t32games.chameleon.presenter.ViewFacade;

public class FragmentControl implements ViewFacade {

    private MenuFragment menuFragment = new MenuFragment();

    private FragmentManager fragmentManager;
    private CompositeDisposable disposables = new CompositeDisposable();
    private int containerId;


    public FragmentControl(FragmentManager fragmentManager, int containerId){
        this.fragmentManager=fragmentManager;
        this.containerId=containerId;
    }

    public void setPresenter(PresenterFacade p){
        //TODO set more!
        menuFragment.setMenuState(p.getMenuState());

        //TODO Fragment switch:
        disposables.dispose();
        disposables = new CompositeDisposable();
        disposables.add(
            p.getFragmentControlState()
                .subscribe(o->{
                    FragmentTransaction t = fragmentManager.beginTransaction();
                    switch (o) {
                        case MENU:
                            t.add(containerId, menuFragment);
                            break;
                        case NEW:
                            break;
                        case GAME:
                            break;
                        case WIN:
                            break;
                    }
                    t.commit();
                })
        );
    }

    @Override
    public Observable<MenuAction> getMenuActions() {
        return menuFragment.getMenuAction();
    }

    @Override
    public Observable<GameAction> getGameActions() {
        return null;
    }

    @Override
    public Observable<NewAction> getNewActions() {
        return null;
    }

    @Override
    public Observable<Integer> getWinActions() {
        return null;
    }
}
