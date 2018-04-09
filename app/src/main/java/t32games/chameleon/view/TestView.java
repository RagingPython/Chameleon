package t32games.chameleon.view;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.model.Pair;
import t32games.chameleon.model.WinEvent;
import t32games.chameleon.presenter.FragmentName;
import t32games.chameleon.presenter.GameAction;
import t32games.chameleon.presenter.GameActionType;
import t32games.chameleon.presenter.MenuAction;
import t32games.chameleon.presenter.MenuState;
import t32games.chameleon.presenter.NewAction;
import t32games.chameleon.presenter.NewActionType;
import t32games.chameleon.presenter.PresenterFacade;
import t32games.chameleon.presenter.ViewFacade;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestView implements ViewFacade {
    private CompositeDisposable externalDisposables = new CompositeDisposable();
    private CompositeDisposable internalDisposables = new CompositeDisposable();

    private PublishSubject<MenuAction> menuActions = PublishSubject.create();
    private PublishSubject<GameAction> gameActions = PublishSubject.create();
    private PublishSubject<NewAction> newActions = PublishSubject.create();
    private PublishSubject<Integer> winActions = PublishSubject.create();

    private ConnectableObservable<String[]> inputFlow;


    //STATE
    private boolean exit=false;
    private String frame;

    public TestView(){
        Observable<String> rawFlow = Observable.create(s->{
            BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
            while (!s.isDisposed()) {
                s.onNext(b.readLine());
            }
            s.onComplete();
        });
        inputFlow = rawFlow
                .map(s -> s.split(" "))
                .filter(o->o.length>1)
                .subscribeOn(Schedulers.io())
                .publish();
        //menu
        internalDisposables.add(inputFlow
                .filter(s->s[0].equals("m"))
                .<MenuAction>flatMap(o->Observable.create(s->{
                    switch (o[1]) {
                        case "resume":
                            s.onNext(MenuAction.RESUME);
                            break;
                        case "new":
                            s.onNext(MenuAction.NEW);
                            break;
                        case "exit":
                            s.onNext(MenuAction.EXIT);
                            break;
                    }
                    s.onComplete();
                }))
                .subscribe(menuActions::onNext));
        //newAction
        internalDisposables.add(inputFlow
                .filter(s->s[0].equals("n"))
                .filter(s->(s[1].equals("new")&s.length>3)|s[1].equals("back"))
                .<NewAction>flatMap(o->Observable.create(s->{
                    try {
                        boolean t = o[1].equals("new");
                        s.onNext(new NewAction(t ? Integer.valueOf(o[2]) : 0, t ? Integer.valueOf(o[3]) : 0, o[1].equals("new")?NewActionType.NEW: NewActionType.BACK));
                    } catch (Exception e) {
                    }
                    s.onComplete();
                }))
                .subscribe(newActions::onNext));
        //gameActions
        internalDisposables.add(inputFlow
                .filter(s->s[0].equals("g"))
                .filter(s->(s[1].equals("turn")&s.length>3)|s[1].equals("back"))
                .<GameAction>flatMap(o->Observable.create(s-> {
                    try {
                        boolean t = o[1].equals("turn");
                        s.onNext(new GameAction(t ? GameActionType.TURN : GameActionType.BACK, t ? Integer.valueOf(o[2]) : 0, t ? Integer.valueOf(o[3]) : 0));
                    } catch (Exception e) {
                    }
                    s.onComplete();
                }))
                .subscribe(gameActions::onNext));
        //winActions
        internalDisposables.add(
            inputFlow
                .filter(s->s[0].equals("w"))
                .filter(s->s[1].equals("ok"))
                .map(o->0)
                .subscribe(winActions::onNext)
        );

        internalDisposables.add(inputFlow.connect());
    }

    public void initialize(PresenterFacade presenterFacade) {
        externalDisposables.dispose();
        externalDisposables=new CompositeDisposable();

        //DRAW MENU
        externalDisposables.add(
            Observable.combineLatest(presenterFacade.getFragmentControlState(), presenterFacade.getMenuState(), Pair::new)
                .filter(o->o.getKey()== FragmentName.MENU)
                .map(Pair::getValue)
                .subscribe(this::drawMenuFragment)
        );
        //DRAW NEW
        externalDisposables.add(
                presenterFacade.getFragmentControlState()
                .subscribe(s->{
                    if (s==FragmentName.NEW){
                        drawNewGameFragment();
                    }
                })
        );
        //DRAW GAME
        externalDisposables.add(
                Observable.combineLatest(presenterFacade.getFragmentControlState()
                        , presenterFacade.getFieldState()
                        ,presenterFacade.getPlayerPanelState()
                        ,presenterFacade.getTimerState()
                        , (fc, fs, tus, tis)-> new Pair<>(fc,new GameState(fs,tus,tis)))
                .filter(o->o.getKey()==FragmentName.GAME)
                .map(Pair::getValue)
                .subscribe(this::drawGameFragment)
        );
        //DRAW WIN
        externalDisposables.add(
            Observable.
                combineLatest(presenterFacade.getFragmentControlState(),presenterFacade.getWinState(), Pair::new)
                .filter(o->o.getKey()==FragmentName.WIN)
                .map(Pair::getValue)
                .subscribe(this::drawWinFragment)
        );
    }

    private void drawWinFragment(WinEvent winState){
        System.out.println("[[[WIN]]]");

        System.out.println("Winner :"+ winState.getWinner()+" ["+winState.getScore(0)+":"+winState.getScore(1)+"]");
    }

    private void drawMenuFragment(MenuState ms) {
        System.out.println("[[[MENU]]]");
        if (ms==MenuState.RESUMABLE) System.out.println("resume");
        System.out.println("new");
        System.out.println("exit");
    };

    private void drawNewGameFragment() {
        System.out.println("[[[NEW GAME]]]");
        System.out.println("new p c");
        System.out.println("back");
    };

    private void drawGameFragment(GameState gs) {
        System.out.println("[[[GAME]]]");
        System.out.println("[FIELD]");
        for (int x=0;x<gs.getFieldState().getXSize()+1;x++){
            System.out.print("--");
        }
        System.out.println();
        for (int x=0;x<gs.getFieldState().getXSize();x++){
            System.out.print("|");
            for (int y=0;y<gs.getFieldState().getYSize();y++){
                if (gs.getFieldState().isVisible(x,y)) {
                    System.out.print(gs.getFieldState().getColor(x,y)+" ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println("|");
        }
        for (int x=0;x<gs.getFieldState().getXSize()+1;x++){
            System.out.print("--");
        }
        System.out.println();
        System.out.println("[TIMER]");
        System.out.println(gs.getTimerState());
        System.out.println("[PLAYER PANELS]");
        for (int x=0;x<(gs.getPlayerPanelState().isTwoPlayers()?2:1);x++){
            for (int y=0;y<gs.getPlayerPanelState().getNumberOfColors();y++){
                System.out.print((gs.getPlayerPanelState().isBlocked(x,y)?"-":"+")+" ");
            }
            if (gs.getPlayerPanelState().getTurnOfPlayer()==x) System.out.print("<--");
            System.out.println();
        }
    };

    @Override
    public PublishSubject<MenuAction> getMenuActions() { return menuActions; }

    @Override
    public PublishSubject<GameAction> getGameActions() {
        return gameActions;
    }

    @Override
    public PublishSubject<NewAction> getNewActions() {
        return newActions;
    }

    @Override
    public Observable<Integer> getWinActions() {
        return winActions;
    }

    @Override
    protected void finalize() throws Throwable {
        externalDisposables.dispose();
        externalDisposables.clear();
        internalDisposables.dispose();
        internalDisposables.clear();
        super.finalize();
    }

}
