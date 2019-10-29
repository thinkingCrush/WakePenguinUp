package kr.Tcrush.WakePenguinUp.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;
import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.ChromeClientController;
import kr.Tcrush.WakePenguinUp.Tool.DialogSupport;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.Tool.ViewClickEffect;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class WebViewFragment extends Fragment implements View.OnClickListener, AdvancedWebView.Listener {
    static EditText et_url;
    ImageView iv_star,iv_list;
    LinearLayout ll_webToolbar;
    static ImageView iv_noneWebView ;
    TextView tv_error_message ;


    static AdvancedWebView wv_webview;
    //WebView wv_webview;

    InputMethodManager inputMethodManager;

    Handler viewHandler ;

    RelativeLayout rl_webview_error ;
    ProgressBar pb_webProgressbar;

    static ImageView iv_gifImage;

    static String lastPage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview,container,false);
        MainActivity.setPageNum(PageNumber.WebViewFragment.ordinal());
        initView(view);
        initHandler();
        Dlog.e("onCreate");

        return view;
    }
    @SuppressLint("ClickableViewAccessibility")
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
                        loadUrl(getContext(),inputData);
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
        iv_star.setOnTouchListener(new ViewClickEffect());
        iv_list = view.findViewById(R.id.iv_list);
        iv_list.setOnTouchListener(new ViewClickEffect());
        iv_noneWebView = view.findViewById(R.id.iv_noneWebView);
        rl_webview_error = view.findViewById(R.id.rl_webview_error);
        tv_error_message = view.findViewById(R.id.tv_error_message);

        initImageViewHandler();


        iv_star.setOnClickListener(this);
        iv_list.setOnClickListener(new MainActivity.DrawerClickListener(getContext()));

        wv_webview = view.findViewById(R.id.wv_webview);
        wv_webview.getSettings().setJavaScriptEnabled(true);
        wv_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_webview.getSettings().setSupportMultipleWindows(true);
        wv_webview.addJavascriptInterface(new MyJavaScriptInterface(),
                "android");

        wv_webview.getSettings().setDomStorageEnabled(true);
        wv_webview.setWebChromeClient(new ChromeClientController(getActivity()));

        WebViewClient mWebViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Dlog.e("onPageFinished url  : " + url);
                try{
                    pb_webProgressbar.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
            };

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try{
                    pb_webProgressbar.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                /*switch (error.getErrorCode()){
                    case ERROR_AUTHENTICATION:
                    case ERROR_BAD_URL:
                    case ERROR_CONNECT:
                    case ERROR_FAILED_SSL_HANDSHAKE:
                    case ERROR_FILE:
                    case ERROR_FILE_NOT_FOUND:
                    case ERROR_HOST_LOOKUP:
                    case ERROR_IO:
                    case ERROR_PROXY_AUTHENTICATION:
                    case ERROR_REDIRECT_LOOP:
                    case ERROR_TIMEOUT:
                    case ERROR_TOO_MANY_REQUESTS:
                    case ERROR_UNKNOWN:
                    case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    case ERROR_UNSUPPORTED_SCHEME:
                }*/
                Dlog.e("error : " + error.getErrorCode());
                urlFindFailError();
            }

        };
        wv_webview.setWebViewClient(mWebViewClient);

        ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(getContext());
        try{
            if(urlArrays != null && !urlArrays.isEmpty()){
                loadUrl(getContext(),urlArrays.get(0).url);
            }else{
                if(lastPage != null ){
                    loadUrl(getContext(),lastPage);
                }else{
                    //내용이 없음
                    urlUnknownError();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ll_webToolbar = view.findViewById(R.id.ll_webToolbar);

        new SharedWPU().setFirstUser(Objects.requireNonNull(getContext()));
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
        }

        /*if(!MainActivity.isFloating){
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
                MainActivity.isFloating = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/

        pb_webProgressbar = view.findViewById(R.id.pb_webProgressbar);

        iv_gifImage = view.findViewById(R.id.iv_gifImage);




    }



    private static Handler viewImageHandler = null;
    private final int WebViewFlag = 1;
    private final int ImageUnknownFlag =2;
    private final int ImageErrorFlag = 3;
    private void initImageViewHandler(){
        try{
            viewImageHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {

                    switch (msg.what){
                        case WebViewFlag :
                            MainActivity.startFloating(MainActivity.mainContext);
                            wv_webview.setVisibility(View.VISIBLE);
                            rl_webview_error.setVisibility(View.GONE);
                            tv_error_message.setVisibility(View.GONE);
                            iv_noneWebView.setVisibility(View.GONE);
                            break;
                        case ImageUnknownFlag :
                            wv_webview.setVisibility(View.GONE);
                            rl_webview_error.setVisibility(View.VISIBLE);
                            tv_error_message.setVisibility(View.VISIBLE);
                            iv_noneWebView.setVisibility(View.VISIBLE);
                            iv_noneWebView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_findfail_url,null));
                            tv_error_message.setText(getContext().getResources().getString(R.string.message_06));
                            MainActivity.checkSidebar(true);
                            break;
                        case ImageErrorFlag :
                            wv_webview.setVisibility(View.GONE);
                            rl_webview_error.setVisibility(View.VISIBLE);
                            tv_error_message.setVisibility(View.VISIBLE);
                            iv_noneWebView.setVisibility(View.VISIBLE);
                            iv_noneWebView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_unknown_url,null));
                            tv_error_message.setText(getContext().getResources().getString(R.string.message_04));
                            break;
                    }

                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void webViewVisible(){
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(WebViewFlag,null).sendToTarget();
        }
    }
    public void urlUnknownError(){
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(ImageUnknownFlag,null).sendToTarget();
        }
    }
    public void urlFindFailError(){
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(ImageErrorFlag,null).sendToTarget();
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

        ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(getContext());
        try{
            if(urlArrays != null && !urlArrays.isEmpty()) {
                MainActivity.startFloating(getContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean loadUrl (Context context, String url){

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    if(wv_webview!=null){
                        wv_webview.setVisibility(View.VISIBLE);
                        iv_noneWebView.setVisibility(View.GONE);
                        wv_webview.loadUrl(url);
                        et_url.setText(checkUrlText(url));
                        lastPage = url;
                        webViewVisible();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        urlFindFailError();
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_star :
                if(checkStar(String.valueOf(et_url.getText()))){
                    new DialogSupport().editItemDialog(getContext(),new SharedWPU().getUrlArrayList(getContext()),0);
                }else{
                    new DialogSupport().addItemDialog(getContext(),String.valueOf(et_url.getText()));
                }
                break;
        }
    }

    private boolean checkStar (String webViewUrl){
        try{
            String currentUrl = webViewUrl;
            if(currentUrl != null){
                ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(getContext());
                if(urlArrays!=null && !urlArrays.isEmpty()){
                    for(UrlArray urlArray : urlArrays){
                        String url = urlArray.url;
                        currentUrl = currentUrl.replace("https://www.","");
                        currentUrl = currentUrl.replace("http://www.","");
                        currentUrl = currentUrl.replace("https://m.","");
                        currentUrl = currentUrl.replace("http://m.","");
                        url = url.replace("https://www.","");
                        url = url.replace("http://www.","");
                        url = url.replace("https://m.","");
                        url = url.replace("http://m.","");

                        if(currentUrl.contains(url)||url.contains(currentUrl)){
                            return true;
                        }
                    }
                }

            }else{
                Dlog.e("currentUrl = null");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        pb_webProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
        pb_webProgressbar.setVisibility(View.GONE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Dlog.e("pageError!!!!!!");
        urlFindFailError();

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

    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void onUrlChange(String url) {
            try{
                if(et_url!=null && !url.equals("about:blank")) {
                    et_url.setText(url);
                    if (checkStar(url)) {
                        iv_star.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_star, null));
                    } else {
                        iv_star.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_star_off, null));
                    }
                    webViewVisible();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public static void startGif(final Context context,final int drawable){
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    iv_gifImage.setVisibility(View.VISIBLE);
                    final GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(iv_gifImage);
                    Glide.with(context).load(drawable).into(gifImage);
                    //5초뒤에 없애야함.
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Glide.clear(gifImage);
                            iv_gifImage.setVisibility(View.GONE);
                        }
                    },5000);
                }
            });
        }catch (Exception e){
            if(iv_gifImage!=null){
                iv_gifImage.setVisibility(View.GONE);
            }
            e.printStackTrace();
        }

    }
}
