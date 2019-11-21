package kr.Tcrush.WakePenguinUp.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;
import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.DialogSupport;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.Tool.ViewClickEffect;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class WebViewFragment extends Fragment implements View.OnClickListener, AdvancedWebView.Listener {
    static EditText et_url;
    static ImageView iv_star,iv_list;
    LinearLayout ll_webToolbar;
    static ImageView iv_noneWebView ;
    TextView tv_error_message ;
    ImageView iv_textCancel;
    LinearLayout ll_webviewPage;
    FrameLayout fl_helpLayout;


    static AdvancedWebView wv_webview;
    //WebView wv_webview;

    static InputMethodManager inputMethodManager;

    Handler viewHandler ;

    RelativeLayout rl_webview_error ;
    static ProgressBar pb_webProgressbar;



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
        ll_webviewPage = view.findViewById(R.id.ll_webviewPage);
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
                        try{
                            hideKeyboard(getContext());
                            et_url.clearFocus();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        iv_textCancel = view.findViewById(R.id.iv_textCancel);
        iv_textCancel.setOnClickListener(this);
        iv_textCancel.setVisibility(View.GONE);
        et_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText =s.toString();
                if(inputText.equals("")){
                    //GONE
                    try{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                iv_textCancel.setVisibility(View.GONE);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final String inputText = String.valueOf(et_url.getText());
                    if(!inputText.equals("")){
                        try{
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Dlog.e("inputLength : "+ inputText.length());
                                    et_url.setSelection(inputText.length());
                                    iv_textCancel.setVisibility(View.VISIBLE);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    //포커스 풀릴때
                    try{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                iv_textCancel.setVisibility(View.GONE);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
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
        wv_webview.getSettings().setLoadWithOverviewMode(true);
        wv_webview.getSettings().setSupportMultipleWindows(true);
        wv_webview.addJavascriptInterface(new MyJavaScriptInterface(),
                "android");

        wv_webview.getSettings().setDomStorageEnabled(true);
        wv_webview.setWebChromeClient(new ChromeClientController(getActivity()));

        WebViewClient mWebViewClient = new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try{
                    pb_webProgressbar.setVisibility(View.VISIBLE);
                    Dlog.e("onPageStarted url : " + url);
                    if(et_url!=null && !url.equals("about:blank") && !url.equals("https://") && !url.equals("http://")) {
                        if (checkStar(getContext(),url)) {
                            iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star, null));
                        } else {
                            iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star_off, null));
                        }
                        webViewVisible();
                    }else{
                        iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star_off, null));
                        urlFindFailError(getContext().getResources().getString(R.string.message_01));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        };
        wv_webview.setWebViewClient(mWebViewClient);

        ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(getContext());
        try{
            if(urlArrays != null && !urlArrays.isEmpty()){
                String url = urlArrays.get(0).url;
                loadUrl(getContext(),url);
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
        hideKeyboard(getContext());




        pb_webProgressbar = view.findViewById(R.id.pb_webProgressbar);

        if(!new SharedWPU().getFirstWebView(getContext())){
            fl_helpLayout = view.findViewById(R.id.fl_helpLayout);
            fl_helpLayout.setVisibility(View.VISIBLE);
            fl_helpLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        new SharedWPU().setFirstWebView(getContext());
                        fl_helpLayout.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }




    }

    public static void hideKeyboard (Context context){
        try{
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void checkEditTextTouch(Context context, MotionEvent event){
        try{
            if(et_url != null){
                if (et_url.isFocused()) {
                    Rect outRect = new Rect();
                    et_url.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        hideKeyboard(context);
                        et_url.clearFocus();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

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
                            Dlog.e("test WebViewFlag");
                            //MainActivity.startService(MainActivity.mainContext);
                            MainActivity.visibleFloating();
                            wv_webview.setVisibility(View.VISIBLE);
                            rl_webview_error.setVisibility(View.GONE);
                            tv_error_message.setVisibility(View.GONE);
                            iv_noneWebView.setVisibility(View.GONE);
                            break;
                        case ImageUnknownFlag :
                            Dlog.e("test ImageUnknownFlag");
                            wv_webview.setVisibility(View.GONE);
                            rl_webview_error.setVisibility(View.VISIBLE);
                            tv_error_message.setVisibility(View.VISIBLE);
                            iv_noneWebView.setVisibility(View.VISIBLE);
                            iv_noneWebView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_findfail_url,null));
                            tv_error_message.setText(getContext().getResources().getString(R.string.message_06));
                            MainActivity.checkSidebar(true);
                            break;
                        case ImageErrorFlag :
                            Dlog.e("test ImageErrorFlag");
                            wv_webview.setVisibility(View.GONE);
                            rl_webview_error.setVisibility(View.VISIBLE);
                            tv_error_message.setVisibility(View.VISIBLE);
                            iv_noneWebView.setVisibility(View.VISIBLE);
                            iv_noneWebView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_unknown_url,null));
                            String message = String.valueOf(msg.obj);
                            if(!message.equals("")){
                                tv_error_message.setText(message);
                            }else{
                                tv_error_message.setText(getContext().getResources().getString(R.string.message_04));
                            }

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
    public void urlFindFailError(String message){
        Dlog.e("test 1111");
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(ImageErrorFlag,message).sendToTarget();
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
        if(!MainActivity.destroyFlag){
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
                    Dlog.e("test 5555");
                    MainActivity.startService(getContext());
                    //MainActivity.visibleFloating();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
                        if(url.equals("http://") || url.equals("https://")){
                            urlFindFailError(getContext().getResources().getString(R.string.message_01));
                        }else{
                            webViewVisible();
                        }

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        urlFindFailError(null);
        return false;
    }

    public static void setStar(final Context context){
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(context!=null){
                        iv_star.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_star, null));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_star :
                if(checkStar(getContext(),et_url.getText().toString())){
                    ArrayList<UrlArray> urlArrays = new ArrayList<>();
                    urlArrays = new SharedWPU().getUrlArrayList(getContext());
                    for(int i = 0; i < urlArrays.size() ; i++){
                        if(urlArrays.get(i).url.equals(et_url.getText().toString())){
                            new DialogSupport().editItemDialog(getContext(),urlArrays,i,false);
                            break;
                        }
                    }

                }else{
                    new DialogSupport().addItemDialog(getContext(),String.valueOf(et_url.getText()));
                }
                break;

            case R.id.iv_textCancel :
                et_url.setText("");
                break;
        }
    }

    public static boolean checkStar (final Context context, String webViewUrl){
        try{
            String currentUrl = webViewUrl;
            if(currentUrl != null){
                ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(context);
                if(urlArrays!=null && !urlArrays.isEmpty()){
                    for(UrlArray urlArray : urlArrays){
                        String url = urlArray.url;
                        Dlog.e("webViewUrl : " + webViewUrl + " , url : " + url);
                        currentUrl = currentUrl.replace("https://www.","");
                        currentUrl = currentUrl.replace("http://www.","");
                        currentUrl = currentUrl.replace("https://m.","");
                        currentUrl = currentUrl.replace("http://m.","");
                        url = url.replace("https://www.","");
                        url = url.replace("http://www.","");
                        url = url.replace("https://m.","");
                        url = url.replace("http://m.","");

                        if(currentUrl.equals(url)){
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
        urlFindFailError(getContext().getResources().getString(R.string.message_01));

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


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

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

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            try{
                pb_webProgressbar.setVisibility(View.GONE);
                if(et_url != null){
                    if( view != null){
                        if(view.getUrl().contains("about:blank")){
                            et_url.setText("");
                        }else{
                            et_url.setText(view.getUrl());

                            if (checkStar(getContext(),view.getUrl())) {
                                iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star, null));
                            } else {
                                iv_star.setImageDrawable(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.icon_star_off, null));
                            }
                        }

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        class FullscreenHolder extends FrameLayout {

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
            }
        }




    }




}
