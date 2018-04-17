package t32games.chameleon.model;

import io.reactivex.Observable;

public interface SourcePlayerPanelState extends SourceNumberOfColors {
    boolean isTwoPlayers();
    Observable<Integer> getAllowedColors(int player);
}
