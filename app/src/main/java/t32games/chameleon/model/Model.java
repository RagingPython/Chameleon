package t32games.chameleon.model;





import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class Model implements ModelFacade{
    //IN
    private final PublishSubject<ModelCommand> inCommand = PublishSubject.create();
    //OUT
    private final PublishSubject<WinEvent> winEvent = PublishSubject.create();
    //INTERNAL
    private final PublishSubject<ModelCommandNew> modelCommandNew = PublishSubject.create();
    private final PublishSubject<Pair<ModelCommandTurn,ModelState>> modelCommandTurn = PublishSubject.create();
    private final BehaviorSubject<ModelState> modelState = BehaviorSubject.create();
    //SUBSCRIPTIONS
    private CompositeDisposable commandSubscription = new CompositeDisposable();
    private CompositeDisposable internalSubscriptions = new CompositeDisposable();
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Model(){
        createLogic();
        //defaults
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
                .cast(ModelCommandNew.class)
                .subscribe(modelCommandNew::onNext)
            , inCommand
                .filter(o -> o instanceof ModelCommandTurn)
                .cast(ModelCommandTurn.class)
                .withLatestFrom(modelState,Pair::new)
                .subscribe(modelCommandTurn::onNext)
        );

        //TODO Execute commands
        //TODO NEW
        internalSubscriptions.add(
            modelCommandNew
                .map(ModelState::newGame)
                .subscribe(modelState::onNext)
        );
        //TODO TURN
        internalSubscriptions.add(
            modelCommandTurn
                .filter(o-> o.getKey().getPlayer()==o.getValue().getTurnOfPlayer())
                .filter(o->o.getKey().getColor()<o.getValue().getNumberOfColors())
                .filter(o->o.getValue().getAllowedColors(o.getValue().getTurnOfPlayer())
                    .filter(oo->oo.equals(o.getKey().getColor()))
                    .count()
                    .blockingGet()==1
                )
                .map(o->o.getValue().makeTurn(o.getKey().getColor()))
                .subscribe(modelState::onNext)
        );
        //OTHER LOGIC
        //TODO CHECK WIN
        internalSubscriptions.add(
            modelState
                .filter(o->Observable
                    .concat(o.getAllowedColors(0),o.getAllowedColors(1))
                    .count()
                    .blockingGet()==0
                )
                .map(ModelState::getWinEvent)
                .subscribe(winEvent::onNext)
        );
    }


    @Override
    public Observable<SourceFieldState> getFieldState() {
        return modelState.cast(SourceFieldState.class);
    }

    @Override
    public Observable<SourcePlayerPanelState> getPlayerPanelState() {
        return modelState.cast(SourcePlayerPanelState.class);
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
