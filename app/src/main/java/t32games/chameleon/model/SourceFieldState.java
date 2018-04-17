package t32games.chameleon.model;


import io.reactivex.Observable;

public interface SourceFieldState extends SourceNumberOfColors {
    int getXSize();
    int getYSize();
    Observable<SourceCellState> getVisibleCells();

}
