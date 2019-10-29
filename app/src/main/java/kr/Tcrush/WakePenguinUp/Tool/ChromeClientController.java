package kr.Tcrush.WakePenguinUp.Tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class ChromeClientController extends WebChromeClient {

    private View mCustomView;
    private Activity mActivity;

    public ChromeClientController(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        result.confirm();
        return super.onJsAlert(view, url, message, result);
    }


    private FullscreenHolder mFullscreenContainer;
    private CustomViewCallback mCustomViewCollback;




    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        Dlog.e("onShowCustomView");
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }


        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

        mFullscreenContainer = new FullscreenHolder(mActivity);
        mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
        decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
        mCustomView = view;
        mCustomViewCollback = callback;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

    }

    @Override
    public void onHideCustomView() {
        Dlog.e("onHideCustomView");
        if (mCustomView == null) {

            return;
        }

        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCollback.onCustomViewHidden();

        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }




    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black,null));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }

        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            Dlog.e("onConfigurationChanged");
        }
    }




}
