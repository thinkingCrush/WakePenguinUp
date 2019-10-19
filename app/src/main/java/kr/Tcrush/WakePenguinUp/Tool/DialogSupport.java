package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import java.util.Random;

public class DialogSupport {

    private static DialogManager.positiveNegativeDialog positiveNegativeDialog;
    public void doDialog(Context context, String url){
        try{
            positiveNegativeDialog = new DialogManager.positiveNegativeDialog(context,randomColor(),url );

            positiveNegativeDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String randomColor(){
        try{
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            return String.format("#%06X", 0xFFFFFF & color);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "#FC1234";
    }
}
