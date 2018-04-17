package t32games.chameleon.model;

import java.util.HashSet;

import io.reactivex.Observable;


public class Block {
    private int color;
    private HashSet<Pair<Integer,Integer>> cells;

    public Block(int color) {
        cells=new HashSet<>();
        this.color = color;
    }

    public void addCell(Pair<Integer,Integer> cell){
        cells.add(cell);
    }

    public void addCell(int x,int y){
        cells.add(new Pair<>(x,y));
    }

    public int getColor() {
        return color;
    }

    public Block mergeWith(Block block, int color){
        Block ans = new Block(color);
        this.getCells().subscribe(o->ans.addCell(o.getXY()));
        block.getCells().subscribe(o->ans.addCell(o.getXY()));
        return ans;
    }

    public Observable<SourceCellState> getCells(){
        return Observable.create(s->{
            for (Pair<Integer,Integer> p:cells){
                s.onNext(new Cell(p,color));
            }
            s.onComplete();
        });
    }

    private class Cell implements SourceCellState{
        private Pair<Integer,Integer> xy;
        private int color;

        public Cell(Pair<Integer, Integer> xy, int color) {
            this.xy = xy;
            this.color = color;
        }

        @Override
        public Pair<Integer, Integer> getXY() {
            return xy;
        }

        @Override
        public int getColor() {
            return color;
        }


    }
}

