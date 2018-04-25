package t32games.chameleon.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.R;
import t32games.chameleon.model.SourceFieldState;
import t32games.chameleon.model.SourcePlayerPanelState;
import t32games.chameleon.presenter.GameAction;

public class FrgGame extends Fragment {


    private final BehaviorSubject<SourceFieldState> fieldState = BehaviorSubject.create();
    private final BehaviorSubject<SourcePlayerPanelState> playerPanelState = BehaviorSubject.create();


    private final PublishSubject<GameAction> gameAction = PublishSubject.create();

    private CompositeDisposable internalDisposable = new CompositeDisposable();
    private Disposable fieldStateLink, playerPanelStateLink;

    public void setFieldState(Observable<SourceFieldState> fieldState) {
        if (fieldStateLink!=null) fieldStateLink.dispose();
        fieldStateLink = fieldState
                .subscribe(this.fieldState::onNext);

    }

    public void setPlayerPanelState(Observable<SourcePlayerPanelState> playerPanelState) {
        if (playerPanelStateLink!=null) playerPanelStateLink.dispose();
        playerPanelStateLink = playerPanelState
            .subscribe(this.playerPanelState::onNext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View ans = inflater.inflate(R.layout.frg_game, container, false);
        FrgGameViewField field=ans.findViewById(R.id.FrgGameField);
        FrgGameViewPlayerPanel panel1=ans.findViewById(R.id.FrgGamePlayerPanel1);
        FrgGameViewPlayerPanel panel2=ans.findViewById(R.id.FrgGamePlayerPanel2);

        internalDisposable.dispose();
        internalDisposable = new CompositeDisposable();

        internalDisposable.addAll(
            fieldState
                .subscribe(field::setFieldState)
        );
        panel1.setPlayer(0);
        panel2.setPlayer(1);
        panel1.setPlayerPanelState(playerPanelState);
        panel2.setPlayerPanelState(playerPanelState);
        internalDisposable.addAll(
            panel1.getGameAction()
                .subscribe(gameAction::onNext)
            ,panel2.getGameAction()
                .subscribe(gameAction::onNext)
        );
        return ans;
    }

    @Override
    public void onDestroyView() {
        internalDisposable.dispose();
        super.onDestroyView();
    }

    public Observable<GameAction> getGameAction(){
        return gameAction;
    }

    @Override
    protected void finalize() throws Throwable {
        fieldStateLink.dispose();
        playerPanelStateLink.dispose();
        super.finalize();
    }
}