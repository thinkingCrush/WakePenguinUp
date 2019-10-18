package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorSupport {


    public void doVibrator(Context context){
        try{
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(300, -1));
            }else{
                vibrator.vibrate(300);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
