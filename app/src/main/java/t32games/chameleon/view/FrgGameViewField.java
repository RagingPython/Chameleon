package t32games.chameleon.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import t32games.chameleon.R;
import t32games.chameleon.model.FieldState_;

public class FrgGameViewField extends View {

    FieldState_ fieldState;
    Paint[] palette;

    public FrgGameViewField(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int[] colors = context.getResources().getIntArray(R.array.palette);
        palette=new Paint[colors.length];
        for(int i=0;i<colors.length;i++){
            palette[i]=new Paint();
            palette[i].setColor(colors[i]);
        }
    }

    public void setFieldState(FieldState_ fieldState){
        this.fieldState=fieldState;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY); //TODO:RF
        if (fieldState != null) {
            float cellSizeX = ((float) canvas.getWidth())/fieldState.getXSize();
            float cellSizeY = ((float) canvas.getHeight())/fieldState.getYSize();
            
            for (int x=0;x<fieldState.getXSize();x++){
                for (int y=0;y<fieldState.getYSize();y++){
                    if (fieldState.isVisible(x,y)){
                        canvas.drawRect(x*cellSizeX,y*cellSizeY,(x+1)*cellSizeX,(y+1)*cellSizeY, palette[fieldState.getColor(x,y)]);
                    }
                }
            }
             
        }
    }
}
