package kr.Tcrush.WakePenguinUp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
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
    static ImageView iv_star,iv_list;
    LinearLayout ll_webToolbar;
    static ImageView iv_noneWebView ;
    TextView tv_error_message ;
    ImageView iv_textCancel;


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
                        try{
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
                            }
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
                }else{
                    //VISIBLE
                    try{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                iv_textCancel.setVisibility(View.VISIBLE);
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
            public void onPageFinished(WebView view, String url) {
                try{
                    pb_webProgressbar.setVisibility(View.GONE);
                    et_url.setText(url);

                }catch (Exception e){
                    e.printStackTrace();
                }
                view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try{
                    pb_webProgressbar.setVisibility(View.VISIBLE);
                    if(et_url!=null && !url.equals("about:blank")) {
                        if (checkStar(getContext(),url)) {
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
        try{
            inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(et_url.getWindowToken(),0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }




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
                            //MainActivity.startService(MainActivity.mainContext);
                            MainActivity.visibleFloating();
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
                            new DialogSupport().editItemDialog(getContext(),urlArrays,i);
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

    public boolean checkStar (final Context context, String webViewUrl){
        try{
            String currentUrl = webViewUrl;
            if(currentUrl != null){
                ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(context);
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
                    iv_gifImage.setBackground(context.getResources().getDrawable(drawable,null));
                    final AnimationDrawable animationDrawable = (AnimationDrawable)iv_gifImage.getBackground();
                    animationDrawable.start();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationDrawable.stop();
                            iv_gifImage.setVisibility(View.GONE);
                        }
                    },5000);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
