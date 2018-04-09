package t32games.chameleon.model;

public class ModelCommandPauseResume extends ModelCommand{
    private boolean resumed;

    public ModelCommandPauseResume(boolean resumed) {
        this.resumed = resumed;
    }

    public boolean isResumed() {
        return resumed;
    }
}
