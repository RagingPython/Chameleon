package t32games.chameleon.presenter;


import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import t32games.chameleon.data.DataFacade;
import t32games.chameleon.model.FieldState;
import t32games.chameleon.model.ModelCommand;
import t32games.chameleon.model.ModelCommandNew;
import t32games.chameleon.model.ModelCommandPauseResume;
import t32games.chameleon.model.ModelCommandTurn;
import t32games.chameleon.model.ModelFacade;
import t32games.chameleon.model.Pair;
import t32games.chameleon.model.PlayerPanelState;
import t32games.chameleon.model.WinEvent;


public class TestPresenter implements PresenterFacade {
    //OUT Streams
    private BehaviorSubject<FragmentName> fragmentControlState = BehaviorSubject.createDefault(FragmentName.MENU);
    private BehaviorSubject<MenuState> menuState = BehaviorSubject.createDefault(MenuState.UNRESUMABLE);
    private BehaviorSubject<FieldState> fieldState = BehaviorSubject.create();
    private BehaviorSubject<Integer> timerState = BehaviorSubject.create();
    private BehaviorSubject<PlayerPanelState> playerPanelState = BehaviorSubject.create();
    private PublishSubject<ModelCommand> modelCommand = PublishSubject.create();
    private BehaviorSubject<WinEvent> winState = BehaviorSubject.create();
    //IN Streams
    private PublishSubject<MenuAction> menuActions = PublishSubject.create();
    private PublishSubject<GameAction> gameActions = PublishSubject.create();
    private PublishSubject<NewAction> newActions = PublishSubject.create();
    private PublishSubject<Integer> winActions = PublishSubject.create();
    private PublishSubject<WinEvent> winEvent = PublishSubject.create();
    //Disposable Containers
    private CompositeDisposable internalDisposables = new CompositeDisposable();
    private CompositeDisposable externalDisposables = new CompositeDisposable();
    //Internal state
    Executor e = Executors.newSingleThreadExecutor();
    private Scheduler s = Schedulers.from(Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(new ThreadGroup("test"),runnable,"thread",100000);
        }
    }));


    public TestPresenter(){

        createLogic();



    }

    public void initialize(ViewFacade viewFacade, ModelFacade modelFacade, DataFacade dataFacade) {
        externalDisposables.dispose();
        externalDisposables = new CompositeDisposable();

        //Streams binding
        modelFacade.setCommand(modelCommand);

        externalDisposables.addAll(
            modelFacade.getFieldState().subscribe(fieldState::onNext)
            , modelFacade.getPlayerPanelState().subscribe(playerPanelState::onNext)
            , modelFacade.getTimerState().subscribe(timerState::onNext)
            , modelFacade.getWinEvent().subscribe(winEvent::onNext)
            , viewFacade.getMenuActions().observeOn(Schedulers.io()).subscribe(menuActions::onNext)
            , viewFacade.getGameActions().observeOn(Schedulers.io()).subscribe(gameActions::onNext)
            , viewFacade.getNewActions().observeOn(Schedulers.io()).subscribe(newActions::onNext)
            , viewFacade.getWinActions().observeOn(Schedulers.io()).subscribe(winActions::onNext)
        );

    }

    private void createLogic(){
        //MENU->NEW
        internalDisposables.add(
            menuActions
                .filter(MenuAction.NEW::equals)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.MENU)
                .map(Pair::getKey)
                .subscribe(o->fragmentControlState.onNext(FragmentName.NEW))
        );
        //MENU->RESUME
        internalDisposables.add(
            menuActions
                .filter(MenuAction.RESUME::equals)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.MENU)
                .map(Pair::getKey)
                .withLatestFrom(menuState, Pair::new)
                .filter(o->o.getValue()==MenuState.RESUMABLE)
                .map(Pair::getKey)
                .subscribe(o->fragmentControlState.onNext(FragmentName.GAME))
        );
        //MENU->EXIT

        //NEW->BACK
        internalDisposables.add(
            newActions
                .filter(o->o.getType()== NewActionType.BACK)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.NEW)
                .map(Pair::getKey)
                .subscribe(o->fragmentControlState.onNext(FragmentName.MENU))
        );
        //NEW->NEW
        internalDisposables.add(
            newActions
                .filter(o->o.getType()== NewActionType.NEW)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.NEW)
                .map(Pair::getKey)
                .subscribe(o->{
                    fragmentControlState.onNext(FragmentName.GAME);
                    modelCommand.onNext(new ModelCommandNew(15,15,o.getPlayers()==2,o.getColors(),10));
                })
        );
        //GAME->BACK
        internalDisposables.add(
            gameActions
                .filter(o->o.getType()== GameActionType.BACK)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.GAME)
                .map(Pair::getKey)
                .subscribe(o->fragmentControlState.onNext(FragmentName.MENU))
        );
        //GAME->TURN
        internalDisposables.add(
            gameActions
                .filter(o->o.getType()== GameActionType.TURN)
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.GAME)
                .map(Pair::getKey)
                .subscribe(o->modelCommand.onNext(new ModelCommandTurn(o.getPlayer(),o.getColor())))
        );
        //WIN->OK
        internalDisposables.add(
            winActions
                .withLatestFrom(fragmentControlState, Pair::new)
                .filter(o->o.getValue()==FragmentName.WIN)
                .map(Pair::getKey)
                .subscribe(o->fragmentControlState.onNext(FragmentName.MENU))
        );
        //GAME PAUSE/RESUME
        internalDisposables.add(
            fragmentControlState
                .subscribe(o->modelCommand.onNext(new ModelCommandPauseResume(o==FragmentName.GAME)))
        );
        //MENU STATE
        internalDisposables.add(
            fragmentControlState
                .filter(FragmentName.GAME::equals)
                .map(o->(Object)o)
                .mergeWith(winEvent)
                .subscribe(o->{
                    if (o instanceof WinEvent){
                        menuState.onNext(MenuState.UNRESUMABLE);
                    } else {
                        menuState.onNext(MenuState.RESUMABLE);
                    }
                })
        );
        //WIN EVENT
        internalDisposables.add(
            winEvent
                .subscribe(o->{
                    winState.onNext(o);
                    fragmentControlState.onNext(FragmentName.WIN);
                })
        );
    }


    @Override
    public BehaviorSubject<FragmentName> getFragmentControlState() {
        return fragmentControlState;
    }

    @Override
    public BehaviorSubject<MenuState> getMenuState() {
        return menuState;
    }

    @Override
    public Observable<FieldState> getFieldState() {
        return fieldState;
    }

    @Override
    public BehaviorSubject<Integer> getTimerState() {
        return timerState;
    }

    @Override
    public Observable<PlayerPanelState> getPlayerPanelState() {
        return playerPanelState;
    }

    @Override
    public Observable<WinEvent> getWinState() {
        return winState;
    }

    @Override
    protected void finalize() throws Throwable {
        internalDisposables.dispose();
        internalDisposables.clear();
        externalDisposables.dispose();
        externalDisposables.clear();
        super.finalize();
    }
}
