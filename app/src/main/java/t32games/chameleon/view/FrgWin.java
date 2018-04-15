package t32games.chameleon.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.R;
import t32games.chameleon.model.WinEvent;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.MenuState;

public class FrgWin extends Fragment {

    Button buttonOk;
    TextView score;

    private final BehaviorSubject<WinEvent> winEvent= BehaviorSubject.create();
    private final PublishSubject<Integer> winAction = PublishSubject.create();

    private CompositeDisposable externalDisposable = new CompositeDisposable();
    private CompositeDisposable internalDisposable = new CompositeDisposable();

    public void setWinEvent(Observable<WinEvent> winEvent) {
        externalDisposable.dispose();
        externalDisposable = new CompositeDisposable();

        externalDisposable.add(
            winEvent
                .subscribe(this.winEvent::onNext)
        );
    }

    public Observable<Integer> getWinAction(){
        return winAction;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View ans = inflater.inflate(R.layout.frg_win, container, false);
        buttonOk=ans.findViewById(R.id.FrgWinOk);
        score = ans.findViewById(R.id.FrgWinScore);

        internalDisposable.dispose();
        internalDisposable = new CompositeDisposable();

        internalDisposable.addAll(
            RxView.clicks(buttonOk)
                .map(o->0)
                .subscribe(winAction::onNext)
            , winEvent
                .subscribe(o-> {
                    String s = "";
                    s=s+o.getScore(0);
                    if (o.isTwoPlayers()){
                        s=s+":"+o.getScore(1)+" ";
                        if (o.getWinner()==0) {
                            s=s+"TOP";
                        } else {
                            s=s+"BOTTOM";
                        }
                    }
                    score.setText(s);
                })
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
