package kr.Tcrush.WakePenguinUp.View.Floating;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.VibratorSupport;

public class FloatingViewController {

    /*private static Context context;
    private static TextView tv_floating_count;
    private static LinearLayout ll_floating;
    private static ImageView iv_floating_lock;
    private static FloatingGauge fg_outGauge;
    private static RelativeLayout rl_outFloatingLayout;

    public FloatingTest(Context context, TextView tv_floating_count, LinearLayout ll_floating,
                        ImageView iv_floating_lock , FloatingGauge fg_outGauge, RelativeLayout rl_outFloatingLayout){
        this.context = context;
        this.tv_floating_count = tv_floating_count;
        this.ll_floating = ll_floating;
        this.iv_floating_lock = iv_floating_lock;
        this.fg_outGauge = fg_outGauge;
        this.rl_outFloatingLayout = rl_outFloatingLayout;

    }*/

    /**
     * 기능이 무엇이 있을까?
     * 1. 카운트 다운 게이지랑 숫자 움직이는거
     * 2. 자물쇠 이미지로 변경되고 애니메이션 좌우로 흔드는거
     * 3. 사라지는거
     * 4. 다시뜨는거
     * 5. 동영상 미디어 위로 뜨는거
     * */

    /**
     * Handler 기능
     * 1. 숫자 증가
     * 2. Gauge 1->100 으로 1초 애니메이션
     * 3. 이미지 보이고, 숫자 사라지는거
     * 4. 숫자 보이고 이미지 사라지는거
     * 5. 애니메이션
     * */


    public static Handler floatingHandler = null;
    public final int CountFloating = 1;
    public final int GaugeFloating = 2;
    public final int ImageViewFloating = 3;
    public final int TextViewFloating = 4;
    public final int AnimationFloating = 5;


    private static int gaugeValue = 0;
    public void initHandler(final Context context, final TextView tv_floating_count, LinearLayout ll_floating,
                            final ImageView iv_floating_lock , final FloatingGauge fg_outGauge, final RelativeLayout rl_outfloatingLayout){
        floatingHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case CountFloating :
                        break;
                    case GaugeFloating :
                        fg_outGauge.setValue(gaugeValue);

                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                if(gaugeValue >1000){
                                    gaugeValue = 0;
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            fg_outGauge.setVisibility(View.GONE);
                                        }
                                    });
                                    this.cancel();
                                }else{
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            fg_outGauge.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    gaugeValue = gaugeValue+10;
                                    fg_outGauge.setValue(gaugeValue);
                                }
                            }
                        },0,10);
                        break;
                    case ImageViewFloating :
                        iv_floating_lock.setVisibility(View.VISIBLE);
                        iv_floating_lock.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_lock_closeed,null));

                        final Animation animTransTwits = AnimationUtils.loadAnimation(
                                context,R.anim.animation_floating_lock);
                        rl_outfloatingLayout.startAnimation(animTransTwits);


                        break;
                    case TextViewFloating :
                        String count = String.valueOf(msg.obj);
                        if(count.equals("0")){
                            tv_floating_count.setVisibility(View.GONE);
                        }else{
                            iv_floating_lock.setVisibility(View.GONE);
                            tv_floating_count.setVisibility(View.VISIBLE);
                            tv_floating_count.setText(count);
                        }

                        break;
                    case AnimationFloating :
                        //timer
                        break;

                }
                return true;
            }
        });
    }



    private static Timer timer = null;
    private static int lockCount = 0;
    public void screenLock(final Context context){
        try{
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try{
                        lockCount ++;
                        switch (lockCount){
                            case 1 :
                                if(floatingHandler != null){
                                    floatingHandler.obtainMessage(TextViewFloating,"3").sendToTarget();
                                    floatingHandler.obtainMessage(GaugeFloating,null).sendToTarget();
                                }
                                break;
                            case 2 :
                                if(floatingHandler != null){
                                    floatingHandler.obtainMessage(TextViewFloating,"2").sendToTarget();
                                    floatingHandler.obtainMessage(GaugeFloating,null).sendToTarget();
                                }
                                break;
                            case 3 :
                                if(floatingHandler != null){
                                    floatingHandler.obtainMessage(TextViewFloating,"1").sendToTarget();
                                    floatingHandler.obtainMessage(GaugeFloating,null).sendToTarget();
                                }
                                break;
                            case 4 :
                                if(floatingHandler != null){
                                    floatingHandler.obtainMessage(TextViewFloating,"0").sendToTarget();
                                    floatingHandler.obtainMessage(ImageViewFloating,null).sendToTarget();
                                }
                                break;
                            case 7 :
                                try{
                                    if(MainActivity.intent!=null && context != null){
                                        context.stopService(MainActivity.intent);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                new VibratorSupport().doVibrator(context);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new MainActivity().touchLock();
                                    }
                                });

                                this.cancel();
                                lockCount  =0;
                                break;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            },0,1000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
