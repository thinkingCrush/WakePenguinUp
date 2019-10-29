package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorSupport {


    public void doVibrator(Context context, int miiliseconds){
        try{
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(300, -1));
            }else{
                vibrator.vibrate(miiliseconds);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void doTick(final Context context){
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        vibrator.vibrate(VibrationEffect.createOneShot(20,-1));
                    }else{
                        vibrator.vibrate(20);
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
