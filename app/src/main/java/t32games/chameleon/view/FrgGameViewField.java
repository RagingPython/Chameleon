package t32games.chameleon.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import t32games.chameleon.R;
import t32games.chameleon.model.SourceFieldState;

public class FrgGameViewField extends View {

    SourceFieldState fieldState;
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

    public void setFieldState(SourceFieldState fieldState){
        this.fieldState=fieldState;
        this.invalidate();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY); //TODO:RF
        if (fieldState != null) {
            float cellSizeX = ((float) canvas.getWidth())/fieldState.getXSize();
            float cellSizeY = ((float) canvas.getHeight())/fieldState.getYSize();
            fieldState.getVisibleCells()
                .subscribe(o->{
                    float x =o.getXY().getKey();
                    float y =o.getXY().getValue();
                    int color = o.getColor();
                    canvas.drawRect(x*cellSizeX,y*cellSizeY,(x+1)*cellSizeX,(y+1)*cellSizeY, palette[color]);
                });
        }
    }
}
