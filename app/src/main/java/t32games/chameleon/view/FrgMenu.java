package t32games.chameleon.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;


import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.R;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.MenuState;


public class FrgMenu extends Fragment {

    Button buttonNew, buttonResume, buttonExit;

    private final BehaviorSubject<MenuState> menuState= BehaviorSubject.create();
    private final PublishSubject<MenuAction> menuAction = PublishSubject.create();

    private CompositeDisposable externalDisposable = new CompositeDisposable();
    private CompositeDisposable internalDisposable = new CompositeDisposable();

    public void setMenuState(Observable<MenuState> menuState) {
        externalDisposable.dispose();
        externalDisposable = new CompositeDisposable();

        externalDisposable.add(
            menuState
                .subscribe(this.menuState::onNext)
        );
    }

    public Observable<MenuAction> getMenuAction(){
        return menuAction;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View ans = inflater.inflate(R.layout.frg_menu, container, false);
        buttonResume=ans.findViewById(R.id.FrgMenuResume);
        buttonNew=ans.findViewById(R.id.FrgMenuNew);
        buttonExit=ans.findViewById(R.id.FrgMenuExit);

        internalDisposable.dispose();
        internalDisposable = new CompositeDisposable();

        internalDisposable.addAll(
            RxView.clicks(buttonResume)
                .map(o->MenuAction.RESUME)
                .subscribe(menuAction::onNext)
            , RxView.clicks(buttonNew)
                .map(o->MenuAction.NEW)
                .subscribe(menuAction::onNext)
            , RxView.clicks(buttonExit)
                .map(o->MenuAction.EXIT)
                .subscribe(menuAction::onNext)
            , menuState
                .map(o->o==MenuState.RESUMABLE)
                .subscribe(buttonResume::setEnabled)
        );
        return ans;
    }

    @Override
    public void onDestroyView() {
        internalDisposable.dispose();
        super.onDestroyView();
    }

    @Override
    protected void finalize() throws Throwable {
        externalDisposable.dispose();
        super.finalize();
    }
}
