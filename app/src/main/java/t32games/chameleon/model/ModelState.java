package t32games.chameleon.model;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.Random;

public class ModelState {
    private int xSize;
    private int ySize;
    private boolean twoPlayers;
    private int numberOfColors;
    private int timeLimit;

    private int turnOfPlayer;
    private int[][] field;
    private boolean[][] visible;
    private boolean[][] blockedColors;
    private boolean active;
    private Observable<Integer> timer;

    private boolean immutable = false;

    public ModelState(ModelState ms) {
        this.xSize=ms.getXSize();
        this.ySize=ms.getYSize();
        this.twoPlayers=ms.isTwoPlayers();
        this.numberOfColors=ms.getNumberOfColors();
        this.timeLimit=ms.getTimeLimit();
        this.turnOfPlayer=ms.getTurnOfPlayer();
        this.field=ms.getField();
        this.visible=ms.getVisible();
        this.blockedColors=ms.getBlockedColors();
        this.active=ms.isActive();
        this.timer=ms.getTimer();
    }

    public ModelState(ModelCommandNew mcn){
        this.xSize=mcn.getXSize();
        this.ySize=mcn.getYSize();
        this.twoPlayers=mcn.isTwoPlayers();
        this.numberOfColors=mcn.getNumberOfColors();
        this.timeLimit=mcn.getTimeLimit();
        this.field = new int[0][0];
        this.visible = new boolean[0][0];
        this.blockedColors = new boolean[0][0];
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public boolean isTwoPlayers() {
        return twoPlayers;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getTurnOfPlayer() {
        return turnOfPlayer;
    }

    public int[][] getField() {
        int[][] ans = field.clone();
        for(int i=0; i<ans.length; i++){
            ans[i]= field[i].clone();
        }
        return ans;
    }

    public boolean[][] getVisible() {
        boolean[][] ans = visible.clone();
        for(int i=0; i<ans.length; i++){
            ans[i]=visible[i].clone();
        }
        return ans;
    }

    public boolean[][] getBlockedColors() {
        boolean[][] ans = blockedColors.clone();
        for(int i=0; i<ans.length; i++){
            ans[i]=blockedColors[i].clone();
        }
        return ans;
    }

    public int getColor(int x, int y){
        return field[x][y];
    }

    public int getColor(Pair<Integer,Integer> xy){
        return field[xy.getKey()][xy.getValue()];
    }

    public boolean isVisible(int x, int y){
        return visible[x][y];
    }

    public boolean isVisible(Pair<Integer,Integer> xy){
        return visible[xy.getKey()][xy.getValue()];
    }

    public boolean isActive() {
        return active;
    }

    public Observable<Integer> getTimer() {
        return timer;
    }

    public void setXSize(int xSize) {
        if(!immutable) this.xSize = xSize;
    }

    public void setYSize(int ySize) {
        if(!immutable) this.ySize = ySize;
    }

    public void setTwoPlayers(boolean twoPlayers) {
        if(!immutable) this.twoPlayers = twoPlayers;
    }

    public void setNumberOfColors(int numberOfColors) {
        if(!immutable) this.numberOfColors = numberOfColors;
    }

    public void setTimeLimit(int timeLimit) {
        if(!immutable) this.timeLimit = timeLimit;
    }

    public void setTurnOfPlayer(int turnOfPlayer) {
        if(!immutable) this.turnOfPlayer = turnOfPlayer;
    }

    public void setField(int[][] field) {
        if(!immutable) this.field = field;
    }

    public void setVisible(boolean[][] visible) {
        if(!immutable) this.visible = visible;
    }

    public void setBlockedColors(boolean[][] blockedColors) {
        if(!immutable) this.blockedColors = blockedColors;
    }

    public void setColor(int x, int y, int color) {
        if(!immutable) field[x][y]=color;
    }

    public void setColor(Pair<Integer,Integer> xy, int color) {
        if(!immutable) field[xy.getKey()][xy.getValue()]=color;
    }

    public void setVisible(int x, int y, boolean v) {
        if(!immutable) visible[x][y]=v;
    }

    public void setVisible(Pair<Integer,Integer> xy, boolean v) {
        if(!immutable) visible[xy.getKey()][xy.getValue()]=v;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTimer(Observable<Integer> timer) {
        this.timer = timer;
    }

    public ModelState makeImmutable(){
        immutable=true;
        return this;
    }
    ///LOGIC

    public ModelState makeTurn(int color){
        //TODO make turn
        ModelState ms = new ModelState(this);
        int x=0;
        int y=0;

        if (ms.getTurnOfPlayer()==1) {
            x=ms.getXSize()-1;
            y=ms.getYSize()-1;
        }

        ms.allSameColorAs(new Pair<>(x,y), new ModelState(ms))
            .subscribe(o->ms.setColor(o,color));
        if(ms.twoPlayers) {
            ms.setTurnOfPlayer(1-ms.getTurnOfPlayer());
        }

        return ms.updateVisible();
    }

    public ModelState newGame(){
        ModelState ms = new ModelState(this);
        Random random = new Random();
        int X = ms.getXSize();
        int Y = ms.getYSize();
        int[][] field = new int[X][Y];
        boolean[][] visible = new boolean[X][Y];
        for (int i = 0;i<X;i++){
            for (int j = 0;j<Y;j++){
                field[i][j]= random.nextInt(ms.getNumberOfColors());
            }
        }
        ms.setField(field);
        ms.setActive(true);
        ms.setTurnOfPlayer(0);
        return ms.updateVisible();
    }

    public WinEvent checkWin(){
        //TODO checkWin
        return null;
    }

    public ModelState updateVisible(){
        ModelState ms = new ModelState(this);
        int X = ms.getXSize();
        int Y = ms.getYSize();
        boolean[][] visible = new boolean[X][Y];

        Observable<Pair<Integer,Integer>> tmp;

        allSameColorAs(new Pair<>(0,0), ms)
            .flatMap(o->allNearCells(o,ms))
            .distinct()
            .subscribe(o-> {
                if ((ms.getColor(o.getKey(),o.getValue())!=ms.getColor(0,0))&(!visible[o.getKey()][o.getValue()])){
                    allSameColorAs(o,ms)
                        .subscribe(oo->{
                            visible[oo.getKey()][oo.getValue()]=true;
                        });
                } else {
                    visible[o.getKey()][o.getValue()] = true;
                }
            });


        if(ms.isTwoPlayers()) {
            allSameColorAs(new Pair<>(X - 1, Y - 1), ms)
                .flatMap(o->allNearCells(o,ms))
                .distinct()
                .subscribe(o-> {
                    if ((ms.getColor(o.getKey(),o.getValue())!=ms.getColor(X - 1, Y - 1))&(!visible[o.getKey()][o.getValue()])){
                        allSameColorAs(o,ms)
                            .subscribe(oo->{
                                visible[oo.getKey()][oo.getValue()]=true;
                            });
                    } else {
                        visible[o.getKey()][o.getValue()] = true;
                    }
                });
        };
        ms.setVisible(visible);
        return ms.updateBlockedColors();
    }

    public ModelState updateBlockedColors(){
        ModelState ms = new ModelState(this);
        boolean[][] bc = new boolean[ms.getNumberOfColors()][ms.isTwoPlayers()?2:1];

        for (int i = 0; i<(ms.isTwoPlayers()?2:1);i++){
            for (int j = 0; j<(ms.getNumberOfColors());j++){
                bc[j][i]=true;
            }
        }

        allSameColorAs(new Pair<>(0,0),ms)
            .flatMap(o->allNearCells(o,ms))
            .map(ms::getColor)
            .distinct()
            .subscribe(o->bc[o][0]=false);
        bc[ms.getColor(0,0)][0]=true;

        if (ms.isTwoPlayers()) {
            allSameColorAs(new Pair<>(ms.getXSize()-1,ms.getYSize()-1),ms)
                .flatMap(o->allNearCells(o,ms))
                .map(ms::getColor)
                .distinct()
                .subscribe(o->bc[o][1]=false);
            bc[ms.getColor(ms.getXSize()-1,ms.getYSize()-1)][0]=true;
            bc[ms.getColor(ms.getXSize()-1,ms.getYSize()-1)][1]=true;
            bc[ms.getColor(0,0)][1]=true;
            boolean b = true;
            for (int i= 0; i<ms.getNumberOfColors(); i++) b=b&bc[i][ms.getTurnOfPlayer()];
            if (b) ms.setTurnOfPlayer(1-ms.getTurnOfPlayer());
        }
        ms.setBlockedColors(bc);
        return ms;
    }

    private Observable<Pair<Integer,Integer>> allNearCells(Pair<Integer,Integer> xy, ModelState ms){
        int x= xy.getKey();
        int y= xy.getValue();
        return Observable.create(s->{
            if ((x>=0)&(y>=0)&(x<ms.getXSize())&(y<ms.getYSize())){
                s.onNext(new Pair<>(x,y));
                if(x-1>=0) s.onNext(new Pair<>(x-1,y));
                if(x+1<ms.getXSize()) s.onNext(new Pair<>(x+1,y));
                if(y-1>=0) s.onNext(new Pair<>(x,y-1));
                if(y+1<ms.getYSize()) s.onNext(new Pair<>(x,y+1));
            }
            s.onComplete();
        });
    }

    private Observable<Pair<Integer,Integer>> allSameColorAs(Pair<Integer,Integer> xy, ModelState ms){
        int x = xy.getKey();
        int y = xy.getValue();
        return Observable.create(s->{
            int startColor=ms.getColor(x,y);
            PublishSubject<Pair<Integer,Integer>> ps = PublishSubject.create();
            Observable<Pair<Integer,Integer>> obs = ps.distinct();
            obs.subscribe(s::onNext);
            Scheduler.Worker w = Schedulers.trampoline().createWorker();
            obs.subscribe(o->{
                if (ms.getColor(o)==startColor){
                    allNearCells(o,ms)
                        .filter(oo->ms.getColor(oo)==startColor)
                        .subscribe(oo->w.schedule(()->ps.onNext(oo)));
                }
            });

            ps.onNext(new Pair<>(x,y));
            ps.onComplete();
            s.onComplete();
        });
    }

    public WinEvent getWinEvent(){

        int[] score = new int[2];
        allSameColorAs(new Pair<>(0,0),this)
            .count()
            .subscribe(o->score[0]=o.intValue());
        Pair<Integer,Integer> p =new Pair<>(getXSize()-1,getYSize()-1);
        allSameColorAs(p,this)
            .count()
            .subscribe(o->score[1]=o.intValue());
        int winner = isTwoPlayers()&(score[1]>score[0])?1:0;
        return new WinEvent(isTwoPlayers(), score, winner);
    }
}
