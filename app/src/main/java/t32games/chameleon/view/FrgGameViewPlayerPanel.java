package t32games.chameleon.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.R;
import t32games.chameleon.model.PlayerPanelState;

public class FrgGameViewPlayerPanel extends LinearLayout{

    private int player =0;
    private Disposable mainLink;
    private CompositeDisposable imageLinks = new CompositeDisposable();
    private Observable<PlayerPanelState> playerPanelState;

    public FrgGameViewPlayerPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPlayerPanelState(Observable<PlayerPanelState> playerPanelState) {
        this.playerPanelState=playerPanelState;
        createMainLink();
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    private void refill (int numberOfColors) {
        removeAllViews();
        imageLinks.dispose();
        imageLinks=new CompositeDisposable();

        int[] colors = getResources().getIntArray(R.array.palette);


        for (int i =0;i<numberOfColors;i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(colors[i]);
            int finalI = i;
            imageLinks.add(
                playerPanelState
                    .filter(o->o.getNumberOfColors()>finalI)
                    .map(o-> (o.getTurnOfPlayer() == player) & (!o.isBlocked(player, finalI))?VISIBLE:INVISIBLE)
                    .subscribe(imageView::setVisibility)
            );
            addView(imageView);
        }
    }

    private void createMainLink() {
        if (mainLink!=null) mainLink.dispose();
        mainLink = playerPanelState
            .map(PlayerPanelState::getNumberOfColors)
            .distinctUntilChanged()
            .subscribe(this::refill);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        createMainLink();
    }

    @Override
    protected void onDetachedFromWindow() {
        mainLink.dispose();
        imageLinks.dispose();
        super.onDetachedFromWindow();
    }
}