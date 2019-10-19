package kr.Tcrush.WakePenguinUp.View;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.ChromeClientController;
import kr.Tcrush.WakePenguinUp.Tool.DialogSupport;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.View.Floating.FloatingService;

public class WebViewFragment extends Fragment implements View.OnClickListener, AdvancedWebView.Listener {
    EditText et_url;
    ImageView iv_star,iv_list;
    LinearLayout ll_webToolbar;


    static AdvancedWebView wv_webview;
    //WebView wv_webview;

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
        //TEST
        et_url=view.findViewById(R.id.et_url);
        et_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    try{
                        String inputData =String.valueOf(textView.getText()) ;
                        inputData = checkUrlText(inputData);
                        loadUrl(inputData);
                        //키보드 숨기기
                        if (inputMethodManager != null) {
                            inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        iv_star = view.findViewById(R.id.iv_star);
        iv_list = view.findViewById(R.id.iv_list);

        iv_star.setOnClickListener(this);
        iv_list.setOnClickListener(new MainActivity.DrawerClickListener(getContext()));

        wv_webview = view.findViewById(R.id.wv_webview);
        wv_webview.getSettings().setJavaScriptEnabled(true);
        wv_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_webview.getSettings().setSupportMultipleWindows(true);

        wv_webview.setWebChromeClient(new ChromeClientController(getActivity()));
        wv_webview.setWebViewClient(new WebViewClient());

        loadUrl("https://sports.news.naver.com/index.nhn");

        ll_webToolbar = view.findViewById(R.id.ll_webToolbar);

        new SharedWPU().setFirstUser(Objects.requireNonNull(getContext()));
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
        }

        if(!MainActivity.FloatingStart){
            try{
                Context context = getContext();
                if(context != null){
                    Intent intent = MainActivity.intent;
                    if(intent == null){
                        intent = new Intent(context, FloatingService.class);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Objects.requireNonNull(context).startForegroundService(intent);
                    }else {
                        Objects.requireNonNull(context).startService(intent);
                    }

                }
                MainActivity.FloatingStart = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private String checkUrlText(String data){
        //URL 이 어떻게 이상한지 체크해야하는데,??
        try{
            data = data.replace("\r","");
            data = data.replace("\n","");

            if(data.length()>=5 && !data.substring(0,5).contains("http")){
                data = "https://"+data;
            }


            return data;
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
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


    public boolean canGoback (){
        if(wv_webview != null){
            return wv_webview.canGoBack();
        }
        return false;
    }
    public void goBack (){
        if(wv_webview != null){
            wv_webview.goBack();
        }

    }




    @Override
    public void onPause() {
        super.onPause();
    }




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


    }

    public void loadUrl (String url){
        try{
            if(wv_webview!=null){
                wv_webview.loadUrl(url);
                et_url.setText(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_star :

                new DialogSupport().doDialog(getContext(),String.valueOf(et_url.getText()));
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
