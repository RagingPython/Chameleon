package t32games.chameleon.model;

public class ModelCommandSet extends ModelCommand{
    private FieldState fieldState;
    private int timerState;
    private int turnOfPlayer;
    private ModelConfiguration modelConfiguration;

    public ModelCommandSet(ModelConfiguration modelConfiguration, FieldState fieldState, int timerState, int turnOfPlayer) {
        this.fieldState = fieldState;
        this.timerState = timerState;
        this.turnOfPlayer = turnOfPlayer;
        this.modelConfiguration = modelConfiguration;
    }

    public FieldState getFieldState() {
        return fieldState;
    }

    public int getTimerState() {
        return timerState;
    }

    public int getTurnOfPlayer() {
        return turnOfPlayer;
    }

    public ModelConfiguration getModelConfiguration() {
        return modelConfiguration;
    }
}
