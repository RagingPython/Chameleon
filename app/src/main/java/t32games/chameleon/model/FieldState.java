package t32games.chameleon.model;


public class FieldState {
    private   int xSize, ySize;
    private int[][] field;
    private boolean[][] visible;

    public FieldState(int xSize, int maxY, int[][] field, boolean[][] visible) {
        this.xSize = xSize;
        this.ySize = maxY;
        this.field = new int[xSize][ySize];
        this.visible = new boolean[xSize][ySize];
        for (int i =0; i<xSize; i++){
            for (int j =0; j<xSize; j++){
                this.field[i][j]=field[i][j];
                this.visible[i][j]=visible[i][j];
            }
        }
    }

    public FieldState(ModelState ms){
        this.xSize=ms.getXSize();
        this.ySize=ms.getYSize();
        this.field= ms.getField();
        this.visible=ms.getVisible();
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getColor(int x, int y) {
        return field[x][y];
    }

    public boolean isVisible(int x, int y) {
        return visible[x][y];
    }
}
