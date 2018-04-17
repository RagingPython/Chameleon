package t32games.chameleon.model;

import android.annotation.SuppressLint;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;


import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ModelState implements SourceFieldState, SourcePlayerPanelState{
    private int xSize;
    private int ySize;
    private int numberOfColors;
    private boolean twoPlayers;

    private int turnOfPlayer;
    private LinkedList<Pair<Block,Block>> blockLinks;
    private Block player0Block;
    private Block player1Block;

    public ModelState(int xSize, int ySize, int numberOfColors, boolean twoPlayers, int turnOfPlayer, LinkedList<Pair<Block, Block>> blockLinks, Block player0Block, Block player1Block) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.numberOfColors = numberOfColors;
        this.twoPlayers = twoPlayers;
        this.turnOfPlayer = turnOfPlayer;
        this.blockLinks = blockLinks;
        this.player0Block = player0Block;
        this.player1Block = player1Block;
    }

    @SuppressLint("CheckResult")
    public static ModelState newGame(ModelCommandNew mcn){
        int xSize = mcn.getXSize();
        int ySize = mcn.getYSize();
        int numberOfColors = mcn.getNumberOfColors();
        boolean twoPlayers = mcn.isTwoPlayers();

        int[][] newField= new int[xSize][ySize];
        Block[][] newBlockField= new Block[xSize][ySize];
        LinkedList<Pair<Block,Block>> newBlockLinks = new LinkedList<>();
        Random r= new Random();
        for(int i=0;i<xSize;i++){
            for(int j=0;j<ySize;j++){
                newField[i][j]=r.nextInt(numberOfColors);
            }
        }
        for(int i=0;i<xSize;i++){
            for(int j=0;j<ySize;j++){
                if (newBlockField[i][j] == null) {
                    Block newBlock = new Block(newField[i][j]);
                    allSameColorAs(new Pair<>(i, j), newField)
                        .subscribe(o->{
                            newBlockField[o.getKey()][o.getValue()]=newBlock;
                            newBlock.addCell(o);
                        });
                }
            }
        }
        Block player0Block = newBlockField[0][0];
        Block player1Block = newBlockField[xSize-1][ySize-1];
        for(int i=0;i<xSize;i++){
            for(int j=0;j<ySize;j++){
                if (newBlockField[i][j] != null) {
                    Block newBlock = newBlockField[i][j];
                    allSameColorAs(new Pair<>(i, j), newField)
                        .doOnNext(o->{
                            if (newBlockField[o.getKey()][o.getValue()]==newBlock){
                                newBlockField[o.getKey()][o.getValue()]=null;
                        }})
                        .flatMap(o->allNearCells(o,xSize,ySize))
                        .filter(o->newBlockField[o.getKey()][o.getValue()]!=null)
                        .map(o->newBlockField[o.getKey()][o.getValue()])
                        .distinct()
                        .subscribe(o-> newBlockLinks.add(new Pair<>(newBlock,o)));
                }
            }
        }



        return new ModelState(xSize,ySize,numberOfColors,twoPlayers,0,newBlockLinks,player0Block,player1Block);
    }

    public ModelState makeTurn(int color){
        //TODO: makeTurn

        LinkedList<Pair<Block,Block>> newBlockLinks = new LinkedList<>();
        Block currentBlock = turnOfPlayer ==0?player0Block:player1Block;
        HashSet<Block> affectedBlocks = new HashSet<>();
        Block newPlayer0Block, newPlayer1Block;
        for (Pair<Block,Block> p:blockLinks){
            if ((p.getKey()==currentBlock)|(p.getValue()==currentBlock)) {
                if ((p.getKey() == currentBlock ? p.getValue() : p.getKey()).getColor()==color) {
                    affectedBlocks.add(p.getKey() == currentBlock ? p.getValue() : p.getKey()); //TODO: RF minor
                }
            }
        }
        affectedBlocks.add(currentBlock);
        currentBlock=new Block(color);
        for(Block b:affectedBlocks){
            currentBlock.mergeWith(b,color);
        }
        for (Pair<Block,Block> p:blockLinks){
            if (affectedBlocks.contains(p.getKey())) {
                if (!affectedBlocks.contains(p.getValue())) {
                    newBlockLinks.add(new Pair<>(currentBlock,p.getValue()));
                }
            } else if (affectedBlocks.contains(p.getValue())){
                newBlockLinks.add(new Pair<>(currentBlock,p.getKey()));
            } else {
                newBlockLinks.add(p);
            }
        }

        if (turnOfPlayer ==0){
            newPlayer0Block=currentBlock;
            newPlayer1Block=player1Block;
        } else {
            newPlayer0Block=player0Block;
            newPlayer1Block=currentBlock;
        }

        return new ModelState(xSize,ySize,numberOfColors, twoPlayers, twoPlayers ?1- turnOfPlayer : turnOfPlayer,newBlockLinks,newPlayer0Block,newPlayer1Block);
    }


    @Override
    public int getXSize() {
        return xSize;
    }

    @Override
    public int getYSize() {
        return ySize;
    }

    @Override
    public Observable<SourceCellState> getVisibleCells() {
        return Observable.fromIterable(blockLinks)
            .filter(o->(o.getKey()==player0Block)|(o.getKey()==player1Block)|(o.getValue()==player0Block)|(o.getValue()==player1Block))
            .flatMap(o->Observable.just(o.getKey(),o.getValue()))
            .distinct()
            .flatMap(Block::getCells);
    }

    @Override
    public boolean isTwoPlayers() {
        return twoPlayers;
    }

    @Override
    public Observable<Integer> getAllowedColors(int player) {
        return Observable.fromIterable(blockLinks)
            .filter(o->player==turnOfPlayer)
            .filter(o->(o.getKey()==(player==0?player0Block:player1Block))|(o.getValue()==(player==0?player0Block:player1Block)))
            .flatMap(o->Observable.just(o.getKey(),o.getValue()))
            .map(Block::getColor)
            .distinct()
            .filter(o->(o!=player0Block.getColor())&(o!=player1Block.getColor()));
    }

    @Override
    public int getNumberOfColors() {
        return numberOfColors;
    }

    public int getTurnOfPlayer() {
        return turnOfPlayer;
    }

    public WinEvent getWinEvent(){
        int[] score = new int[2];
        score[0] = player0Block.getCells().count().blockingGet().intValue();
        score[1] = player1Block.getCells().count().blockingGet().intValue();
        return new WinEvent(twoPlayers, score, twoPlayers&(score[0]<score[1])?1:0);
    }

    private static Observable<Pair<Integer,Integer>> allNearCells(Pair<Integer,Integer> xy, int xSize, int ySize){
        int x= xy.getKey();
        int y= xy.getValue();
        return Observable.create(s->{
            if ((x>=0)&(y>=0)&(x<xSize)&(y<ySize)){
                s.onNext(new Pair<>(x,y));
                if(x-1>=0) s.onNext(new Pair<>(x-1,y));
                if(x+1<xSize) s.onNext(new Pair<>(x+1,y));
                if(y-1>=0) s.onNext(new Pair<>(x,y-1));
                if(y+1<ySize) s.onNext(new Pair<>(x,y+1));
            }
            s.onComplete();
        });
    }

    @SuppressLint("CheckResult")
    private static Observable<Pair<Integer,Integer>> allSameColorAs(Pair<Integer,Integer> xy, int[][] colors){
        int x = xy.getKey();
        int y = xy.getValue();
        return Observable.create(s->{
            int startColor=colors[x][y];
            PublishSubject<Pair<Integer,Integer>> ps = PublishSubject.create();
            Observable<Pair<Integer,Integer>> obs = ps.distinct();
            obs.subscribe(s::onNext);
            Scheduler.Worker w = Schedulers.trampoline().createWorker();
            obs.subscribe(o->{
                if (colors[o.getKey()][o.getValue()]==startColor){
                    allNearCells(o,colors.length,colors[0].length)
                        .filter(oo->colors[oo.getKey()][oo.getValue()]==startColor)
                        .subscribe(oo->w.schedule(()->ps.onNext(oo)));
                }
            });

            ps.onNext(new Pair<>(x,y));
            ps.onComplete();
            s.onComplete();
        });
    }
}
