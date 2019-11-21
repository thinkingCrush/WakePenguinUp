package kr.Tcrush.WakePenguinUp.View.Floating;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
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
import kr.Tcrush.WakePenguinUp.Tool.SoundPlay;
import kr.Tcrush.WakePenguinUp.Tool.VibratorSupport;
import kr.Tcrush.WakePenguinUp.Tool.WebViewController;
import kr.Tcrush.WakePenguinUp.View.WebViewFragment;

public class FloatingViewController {

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
    public final int UnTouchLockFloating = 6;
    public final int GoneFloating = 7;
    public final int VisibleFloating = 8;
    public final int StartGIF = 9;


    private static int gaugeValue = 0;
    private static Timer gaugeTimer = null;
    public void initHandler(final Context context, final TextView tv_floating_count, final LinearLayout ll_floating,
                            final ImageView iv_floating_lock , final FloatingGauge fg_outGauge, final RelativeLayout rl_outfloatingLayout, final ImageView iv_gifImage){
        floatingHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case CountFloating :
                        break;
                    case GaugeFloating :
                        try{
                            fg_outGauge.setValue(gaugeValue);

                            gaugeTimer = new Timer();
                            gaugeTimer.scheduleAtFixedRate(new TimerTask() {
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
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                fg_outGauge.setValue(gaugeValue);
                                            }
                                        });

                                    }
                                }
                            },0,10);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;
                    case ImageViewFloating :
                        try{
                            iv_floating_lock.setVisibility(View.VISIBLE);
                            iv_floating_lock.setImageDrawable(context.getResources().getDrawable(R.drawable.img_lock,null));

                            final Animation animTransTwits = AnimationUtils.loadAnimation(
                                    context,R.anim.animation_floating_lock);
                            rl_outfloatingLayout.startAnimation(animTransTwits);
                        }catch (Exception e){
                            e.printStackTrace();
                        }



                        break;
                    case TextViewFloating :
                        try{
                            String count = String.valueOf(msg.obj);
                            if(count.equals("0")){
                                tv_floating_count.setVisibility(View.GONE);
                                fg_outGauge.setVisibility(View.GONE);
                            }else{
                                iv_floating_lock.setVisibility(View.GONE);
                                tv_floating_count.setVisibility(View.VISIBLE);
                                tv_floating_count.setText(count);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        break;
                    case AnimationFloating :
                        //timer
                        break;

                    case UnTouchLockFloating :
                        try{
                            final Animation animTransTwits2 = AnimationUtils.loadAnimation(
                                    context,R.anim.animation_floating_unlock);
                            rl_outfloatingLayout.startAnimation(animTransTwits2);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case GoneFloating :
                        try{
                            Dlog.e("GoneFloating");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    iv_floating_lock.setVisibility(View.GONE);
                                    rl_outfloatingLayout.setVisibility(View.GONE);
                                    fg_outGauge.setVisibility(View.GONE);
                                    tv_floating_count.setVisibility(View.GONE);
                                    ll_floating.setVisibility(View.GONE);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        break;

                    case VisibleFloating :
                        try{
                            Dlog.e("VisibleFloating");
                            iv_floating_lock.setVisibility(View.VISIBLE);
                            iv_floating_lock.setImageDrawable(context.getResources().getDrawable(R.drawable.img_unlock,null));
                            rl_outfloatingLayout.setVisibility(View.VISIBLE);
                            ll_floating.setVisibility(View.VISIBLE);
                            fg_outGauge.setVisibility(View.GONE);
                            tv_floating_count.setVisibility(View.GONE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case StartGIF :
                        try{
                            iv_gifImage.setVisibility(View.INVISIBLE);
                            iv_gifImage.setBackground(context.getResources().getDrawable((int)msg.obj,null));
                            final AnimationDrawable animationDrawable = (AnimationDrawable)iv_gifImage.getBackground();
                            animationDrawable.start();
                            iv_gifImage.setVisibility(View.VISIBLE);
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animationDrawable.stop();
                                    iv_gifImage.setVisibility(View.GONE);
                                }
                            },5000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

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
                                    new VibratorSupport().doVibrator(context,300);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MainActivity().touchLock();
                                        }
                                    });
                                    floatingHandler.obtainMessage(TextViewFloating,"0").sendToTarget();
                                    floatingHandler.obtainMessage(ImageViewFloating,null).sendToTarget();
                                    startGif(R.drawable.change_image_sleep);
                                    new SoundPlay().playSound(context);
                                }
                                break;
                            case 6 :
                                new FloatingViewController().floatingGone();
                                FloatingService.setFloatingClicked(false);
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

    public void screenLockCancel(){
        try{
            if(floatingHandler != null){
                floatingHandler.obtainMessage(VisibleFloating,null).sendToTarget();
            }
            lockCount = 0;
            if(timer != null){
                timer.purge();
                timer.cancel();
            }
            gaugeValue =0;
            if(gaugeTimer != null){
                gaugeTimer.purge();
                gaugeTimer.cancel();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void floatingGone(){
        try{
            if(floatingHandler!= null){
                floatingHandler.obtainMessage(GoneFloating,null).sendToTarget();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void floatingVisible(){
        try{
            if(floatingHandler!= null){
                floatingHandler.obtainMessage(VisibleFloating,null).sendToTarget();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void wakeUpAnimation(){
        try{
            if(floatingHandler!= null){
                floatingHandler.obtainMessage(UnTouchLockFloating,null).sendToTarget();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void startGif(int drawable){
        try{
            if(floatingHandler != null){
                floatingHandler.obtainMessage(StartGIF,drawable).sendToTarget();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
