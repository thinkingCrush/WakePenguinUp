package kr.Tcrush.WakePenguinUp.View.Floating;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
    public void initHandler(Context context, TextView tv_floating_count, LinearLayout ll_floating,
                            ImageView iv_floating_lock , FloatingGauge fg_outGauge, RelativeLayout rl_outfloatingLayout){
        if(floatingHandler == null){
            floatingHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    switch (msg.what){
                        case CountFloating :
                            break;
                        case GaugeFloating :
                            break;
                        case ImageViewFloating :
                            break;
                        case TextViewFloating :
                            break;
                        case AnimationFloating :
                            break;
                    }
                    return true;
                }
            });
        }
    }


}
