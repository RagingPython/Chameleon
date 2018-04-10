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
import t32games.chameleon.model.FieldState;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.MenuState;

public class FrgGame extends Fragment {


    private final BehaviorSubject<FieldState> fieldState = BehaviorSubject.create();
    //private final PublishSubject<MenuAction> menuAction = PublishSubject.create();

    private CompositeDisposable externalDisposable = new CompositeDisposable();
    private CompositeDisposable internalDisposable = new CompositeDisposable();

    public void setFieldState(Observable<FieldState> fieldState) {
        externalDisposable.dispose();
        externalDisposable = new CompositeDisposable();

        externalDisposable.add(
            fieldState
                .subscribe(this.fieldState::onNext)
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View ans = inflater.inflate(R.layout.frg_game, container, false);
        FrgGameViewField field=ans.findViewById(R.id.FrgGameField);

        internalDisposable.dispose();
        internalDisposable = new CompositeDisposable();

        internalDisposable.add(
            fieldState
                .subscribe(field::setFieldState)
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