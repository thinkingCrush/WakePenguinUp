package kr.Tcrush.WakePenguinUp.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.View.Floating.FloatingService;

public class WebViewFragment extends Fragment implements View.OnClickListener, AdvancedWebView.Listener {
    EditText et_url;
    ImageView iv_star,iv_list;
    LinearLayout ll_webToolbar;


    AdvancedWebView wv_webview;

    InputMethodManager inputMethodManager;

    Handler viewHandler ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview,container,false);
        initView(view);
        initHandler();

        return view;
    }
    private void initView (View view){
        et_url=view.findViewById(R.id.et_url);

        iv_star = view.findViewById(R.id.iv_star);
        iv_list = view.findViewById(R.id.iv_list);

        iv_star.setOnClickListener(this);
        iv_list.setOnClickListener(new MainActivity.DrawerClickListener(getContext()));

        wv_webview = view.findViewById(R.id.wv_webview);
        wv_webview.setListener(getActivity(),this);

        wv_webview.loadUrl("https://www.naver.com/");

        ll_webToolbar = view.findViewById(R.id.ll_webToolbar);

        new SharedWPU().setFirstUser(Objects.requireNonNull(getContext()));
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
        }


    }

    private final int viewStarOn = 1;
    private final int viewStarOff = 2;
    private void initHandler (){
        try{
            if(viewHandler == null){
                viewHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        switch (message.what){
                            case viewStarOn :
                                iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star,null));
                                break;
                            case viewStarOff :
                                iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star_off,null));
                                break;

                        }
                        return true;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void startFloating(){
        try{
            intent = new Intent(Objects.requireNonNull(getActivity()).getBaseContext(), FloatingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Objects.requireNonNull(getContext()).startForegroundService(intent);
            }else {
                Objects.requireNonNull(getContext()).startService(intent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopFloating(){
        try{
            if(intent != null){
                Objects.requireNonNull(getContext()).stopService(intent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Intent intent;
    @Override
    public void onResume() {
        super.onResume();
        try{
            if(wv_webview!=null){
                wv_webview.onResume();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        startFloating();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFloating();
    }

    public void loadUrl (String url){
        try{
            if(wv_webview!=null){
                wv_webview.loadUrl(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_star :

                break;
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
