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
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.R;
import t32games.chameleon.presenter.NewAction;
import t32games.chameleon.presenter.NewActionType;

public class FrgNew extends Fragment {

    private final PublishSubject<NewAction> newAction = PublishSubject.create();

    private CompositeDisposable externalDisposable = new CompositeDisposable();
    private CompositeDisposable internalDisposable = new CompositeDisposable();

    public Observable<NewAction> getNewAction(){
        return newAction;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View ans = inflater.inflate(R.layout.frg_new, container, false);
        Button buttonOnePlayer = ans.findViewById(R.id.FrgNewOnePlayer);
        Button buttonTwoPlayers = ans.findViewById(R.id.FrgNewTwoPlayers);
        Button buttonBack = ans.findViewById(R.id.FrgNewBack);

        internalDisposable.dispose();
        internalDisposable = new CompositeDisposable();

        internalDisposable.addAll(
            RxView.clicks(buttonOnePlayer)
                .map(o-> new NewAction(1,4, NewActionType.NEW))
                .subscribe(newAction::onNext)
            , RxView.clicks(buttonTwoPlayers)
                .map(o-> new NewAction(2,4, NewActionType.NEW))
                .subscribe(newAction::onNext)
            , RxView.clicks(buttonBack)
                .map(o-> new NewAction(0,0, NewActionType.BACK))
                .subscribe(newAction::onNext)
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
