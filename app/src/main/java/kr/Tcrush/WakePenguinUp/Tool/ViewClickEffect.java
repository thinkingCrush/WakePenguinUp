package kr.Tcrush.WakePenguinUp.Tool;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewClickEffect implements View.OnTouchListener {
    int textColor = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable drawable = null;

        try {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    v.setScaleX(0.95f);
                    v.setScaleY(0.95f);
                    if (v instanceof ImageView){
                        drawable = ((ImageView)v).getDrawable();
                        drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                    }else if(v instanceof Button){
                        drawable = v.getBackground();
                        drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                    }else if(v instanceof TextView){
                        textColor = ((TextView)v).getCurrentTextColor();
                        ((TextView)v).setTextColor(((textColor & 0x00FFFFFF) | 0x77000000));
                    }else if (v instanceof RelativeLayout){
                        if(v.getBackground() != null){
                            v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        }

                    }
                    break;
                case MotionEvent.ACTION_UP :
                case MotionEvent.ACTION_CANCEL :
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    if (v instanceof ImageView){
                        drawable = ((ImageView)v).getDrawable();
                        drawable.clearColorFilter();
                        v.invalidate();
                    }else if(v instanceof Button){
                        drawable = ((Button)v).getBackground();
                        drawable.clearColorFilter();
                        v.invalidate();
                    }else if(v instanceof TextView){
                        ((TextView)v).setTextColor(textColor);
                    }else if (v instanceof RelativeLayout){
                        if(v.getBackground() != null){
                            v.getBackground().clearColorFilter();
                        }

                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
