package t32games.chameleon.model;





import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class TestModel implements ModelFacade{
    //IN
    private final PublishSubject<ModelCommand> inCommand = PublishSubject.create();
    //OUT
    private final BehaviorSubject<Integer> timerState = BehaviorSubject.create();
    private final BehaviorSubject<FieldState> fieldState = BehaviorSubject.create();
    private final BehaviorSubject<PlayerPanelState> playerPanelState = BehaviorSubject.create();
    private final PublishSubject<WinEvent> winEvent = PublishSubject.create();
    //INTERNAL
    private final PublishSubject<Pair<ModelCommandNew,ModelState>> modelCommandNew = PublishSubject.create();
    private final PublishSubject<Pair<ModelCommandSet,ModelState>> modelCommandSet = PublishSubject.create();
    private final PublishSubject<Pair<ModelCommandPauseResume,ModelState>> modelCommandPauseResume = PublishSubject.create();
    private final PublishSubject<Pair<ModelCommandTurn,ModelState>> modelCommandTurn = PublishSubject.create();
    private final BehaviorSubject<ModelState> modelState = BehaviorSubject.create();
    //SUBSCRIPTIONS
    private CompositeDisposable commandSubscription = new CompositeDisposable();
    private CompositeDisposable internalSubscriptions = new CompositeDisposable();
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public TestModel(){
        createLogic();
        //defaults
        timerState.onNext(0);
    }

    @Override
    public void setCommand(Observable<ModelCommand> command) {
        commandSubscription.dispose();
        commandSubscription = new CompositeDisposable();
        commandSubscription.add(
            command.subscribe(this.inCommand::onNext)
        );
    }

    private void createLogic() {
        //Parse inCommand in different streams
        //TODO FILTERS!!!!
        internalSubscriptions.addAll(
            inCommand
                .filter(o -> o instanceof ModelCommandNew)
                .map(o->(ModelCommandNew) o)
                .map(o->new Pair<>(o,new ModelState(o)))
                .subscribe(modelCommandNew::onNext)
            , inCommand
                .filter(o -> o instanceof ModelCommandSet)
                .map(o->(ModelCommandSet) o)
                .withLatestFrom(modelState,Pair::new)
                .subscribe(modelCommandSet::onNext)
            , inCommand
                .filter(o -> o instanceof ModelCommandPauseResume)
                .map(o->(ModelCommandPauseResume) o)
                .withLatestFrom(modelState,Pair::new)
                .subscribe(modelCommandPauseResume::onNext)
            , inCommand
                .filter(o -> o instanceof ModelCommandTurn)
                .map(o->(ModelCommandTurn) o)
                .withLatestFrom(modelState,Pair::new)
                .subscribe(modelCommandTurn::onNext)
        );

        //TODO Execute commands
        //TODO NEW
        internalSubscriptions.add(
            modelCommandNew
                .map(Pair::getValue)
                .map(ModelState::newGame)
                .map(ModelState::makeImmutable)
                .subscribe(modelState::onNext)
        );
        //TODO SET
        //TODO TURN
        internalSubscriptions.add(
            modelCommandTurn
                .filter(o-> o.getKey().getPlayer()==o.getValue().getTurnOfPlayer())
                .filter(o->o.getKey().getColor()<o.getValue().getNumberOfColors())
                .filter(o->!o.getValue().getBlockedColors()[o.getKey().getColor()][o.getKey().getPlayer()])
                .map(o->o.getValue().makeTurn(o.getKey().getColor()))
                .map(ModelState::makeImmutable)
                .subscribe(modelState::onNext)
        );
        //TODO PAUSE

        //TODO UPDATE OUTS:
        //TODO fieldState
        internalSubscriptions.add(
            modelState
                .map(FieldState::new)
                .subscribe(fieldState::onNext)
        );
        //TODO playersPanelState
        internalSubscriptions.add(
            modelState
                .map(PlayerPanelState::new)
                .subscribe(playerPanelState::onNext)
        );
        //TODO timer
        /*
        internalSubscriptions.add(
            Observable
                .switchOnNext(
                    modelState
                        .map(ModelState::getTimer)
                )
                .subscribe(timerState::onNext)
        );
        */
        //OTHER LOGIC
        //TODO CHECK WIN
        internalSubscriptions.add(
            modelState
                .filter(o->{
                    boolean ans=true;
                    for (boolean[] a:o.getBlockedColors()){
                        for(boolean b:a){
                            ans=ans&b;
                        }
                    }
                    return ans;
                })
                .map(ModelState::getWinEvent)
                .subscribe(winEvent::onNext)
        );
    }


    @Override
    public Observable<Integer> getTimerState() {
        return timerState;
    }

    @Override
    public Observable<FieldState> getFieldState() {
        return fieldState;
    }

    @Override
    public Observable<PlayerPanelState> getPlayerPanelState() {
        return playerPanelState;
    }

    @Override
    public Observable<WinEvent> getWinEvent() {
        return winEvent;
    }

    @Override
    protected void finalize() throws Throwable {
        commandSubscription.dispose();
        commandSubscription.clear();
        internalSubscriptions.dispose();
        internalSubscriptions.clear();
        super.finalize();
    }
}
