package kr.Tcrush.WakePenguinUp.Tool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.Data.UrlArrayManager;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.View.UrlListFragment;
import kr.Tcrush.WakePenguinUp.View.WebViewFragment;

public class DialogManager  extends AlertDialog.Builder {

    public DialogManager(Context context) {
        super(context);
    }

    // 확인, 취소 버튼 다이얼로그
    public static class addItemDialog extends Dialog {
        Context context;

        FrameLayout fl_dialog_icon_background;
        TextView tv_dialog_firstText;

        EditText et_dialog_urlName;
        EditText et_dialog_url;

        static Button btn_dialogPositive;
        static Button btn_dialogNagative;



        String deleteDialogText = null;
        String cancleDialogText = null;

        String color ;
        String url;

        public addItemDialog(@NonNull Context context, String color, String url ) {
            super(context);

            this.context = context;
            this.color = color;
            this.url = url;

        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            MainActivity.startFloating(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_positive_negative);

            MainActivity.stopFloating(context);

            fl_dialog_icon_background = findViewById(R.id.fl_dialog_icon_background);
            tv_dialog_firstText = findViewById(R.id.tv_dialog_firstText);
            tv_dialog_firstText.setText(context.getResources().getString(R.string.init_shortcuts));

            Drawable roundDrawable = context.getResources().getDrawable(R.drawable.drawerlayout_listitem_icon_background,null);
            roundDrawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
            fl_dialog_icon_background.setBackground(roundDrawable);


            et_dialog_urlName = findViewById(R.id.et_dialog_urlName);
            et_dialog_url = findViewById(R.id.et_dialog_url);
            et_dialog_url.setText(url);


            et_dialog_urlName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        String inputName = String.valueOf(charSequence);

                        String checkName = inputName.replace(" ","");
                        if(inputName!=null && !inputName.equals("") &&!checkName.equals("")){
                            try{
                                tv_dialog_firstText.setText(inputName.substring(0,1));
                            }catch (Exception e){
                                tv_dialog_firstText.setText(context.getResources().getString(R.string.init_shortcuts));
                                e.printStackTrace();
                            }

                        }else{
                            tv_dialog_firstText.setText("?");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputName = String.valueOf(et_dialog_urlName.getText());
                    String inputUrl = String.valueOf(et_dialog_url.getText());
                    String firstName = inputName.substring(0,1);

                    ArrayList<UrlArray> urlArrays = new ArrayList<>();
                    urlArrays = new SharedWPU().getUrlArrayList(context);
                    urlArrays.add(new UrlArray(inputUrl,inputName,firstName,color));

                    new UrlArrayManager().setUrlArrayList(context,urlArrays);

                    MainActivity.listRefresh(context);
                    UrlListFragment.listRefresh(context);
                    WebViewFragment.setStar(context);
                    dismiss();
                }
            });
            btn_dialogNagative = findViewById(R.id.btn_dialogNagative);
            btn_dialogNagative.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogNagative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });



            Window window = getWindow();
            if( window != null ) {
                // 백그라운드 투명
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams params = window.getAttributes();
                // 화면에 가득 차도록
                params.width         = WindowManager.LayoutParams.MATCH_PARENT;
                params.height        = WindowManager.LayoutParams.MATCH_PARENT;

                // 열기&닫기 시 애니메이션 설정
                params.windowAnimations = R.style.AnimationPopupStyle;
                window.setAttributes( params );
                window.setGravity( Gravity.BOTTOM );
            }
        }
    }

    public static class editItemDialog extends Dialog {
        Context context;

        FrameLayout fl_dialog_icon_background;
        TextView tv_dialog_firstText;

        EditText et_dialog_urlName;
        EditText et_dialog_url;

        static Button btn_dialogPositive;
        static Button btn_dialogNagative;



        int itemPosition;
        ArrayList<UrlArray> urlArrays;

        public editItemDialog(@NonNull Context context,ArrayList<UrlArray> urlArrays , int itemPosition ) {
            super(context);

            this.context = context;
            this.urlArrays = urlArrays;
            this.itemPosition = itemPosition;

        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Dlog.e("test 2222");
            MainActivity.startFloating(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            MainActivity.stopFloating(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_positive_negative);


            final String color = urlArrays.get(itemPosition).textBgColor;
            String url = urlArrays.get(itemPosition).url;
            String urlName = urlArrays.get(itemPosition).urlName;
            String firstText = urlArrays.get(itemPosition).urlFirstText;



            fl_dialog_icon_background = findViewById(R.id.fl_dialog_icon_background);
            tv_dialog_firstText = findViewById(R.id.tv_dialog_firstText);
            tv_dialog_firstText.setText(firstText);

            Drawable roundDrawable = context.getResources().getDrawable(R.drawable.drawerlayout_listitem_icon_background,null);
            roundDrawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
            fl_dialog_icon_background.setBackground(roundDrawable);


            et_dialog_urlName = findViewById(R.id.et_dialog_urlName);
            et_dialog_urlName.setText(urlName);
            et_dialog_url = findViewById(R.id.et_dialog_url);
            et_dialog_url.setText(url);


            et_dialog_urlName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        String inputName = String.valueOf(charSequence);

                        String checkName = inputName.replace(" ","");
                        if(inputName!=null && !inputName.equals("") &&!checkName.equals("")){
                            try{
                                tv_dialog_firstText.setText(inputName.substring(0,1));
                            }catch (Exception e){
                                tv_dialog_firstText.setText(context.getResources().getString(R.string.init_shortcuts));
                                e.printStackTrace();
                            }

                        }else{
                            tv_dialog_firstText.setText("?");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setText(context.getResources().getString(R.string.basic_edit));
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputName = String.valueOf(et_dialog_urlName.getText());
                    String inputUrl = String.valueOf(et_dialog_url.getText());
                    String firstName = inputName.substring(0,1);


                    urlArrays.get(itemPosition).url = inputUrl;
                    urlArrays.get(itemPosition).urlName = inputName;
                    urlArrays.get(itemPosition).urlFirstText = firstName;

                    new UrlArrayManager().setUrlArrayList(context,urlArrays);

                    MainActivity.listRefresh(context);
                    UrlListFragment.listRefresh(context);
                    dismiss();
                }
            });
            btn_dialogNagative = findViewById(R.id.btn_dialogNagative);
            btn_dialogNagative.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogNagative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });



            Window window = getWindow();
            if( window != null ) {
                // 백그라운드 투명
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams params = window.getAttributes();
                // 화면에 가득 차도록
                params.width         = WindowManager.LayoutParams.MATCH_PARENT;
                params.height        = WindowManager.LayoutParams.MATCH_PARENT;

                // 열기&닫기 시 애니메이션 설정
                params.windowAnimations = R.style.AnimationPopupStyle;
                window.setAttributes( params );
                window.setGravity( Gravity.BOTTOM );
            }
        }
    }

    public static class urlList_addItemDialog extends Dialog {
        Context context;

        FrameLayout fl_dialog_icon_background;
        TextView tv_dialog_firstText;

        EditText et_dialog_urlName;
        EditText et_dialog_url;

        static Button btn_dialogPositive;
        static Button btn_dialogNagative;



        String deleteDialogText = null;
        String cancleDialogText = null;

        String color ;
        String url;

        public urlList_addItemDialog(@NonNull Context context, String color, String url ) {
            super(context);

            this.context = context;
            this.color = color;
            this.url = url;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_positive_negative);


            fl_dialog_icon_background = findViewById(R.id.fl_dialog_icon_background);
            tv_dialog_firstText = findViewById(R.id.tv_dialog_firstText);
            tv_dialog_firstText.setText(context.getResources().getString(R.string.init_shortcuts));

            Drawable roundDrawable = context.getResources().getDrawable(R.drawable.drawerlayout_listitem_icon_background,null);
            roundDrawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
            fl_dialog_icon_background.setBackground(roundDrawable);


            et_dialog_urlName = findViewById(R.id.et_dialog_urlName);
            et_dialog_url = findViewById(R.id.et_dialog_url);
            et_dialog_url.setText(url);


            et_dialog_urlName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        String inputName = String.valueOf(charSequence);

                        String checkName = inputName.replace(" ","");
                        if(inputName!=null && !inputName.equals("") &&!checkName.equals("")){
                            try{
                                tv_dialog_firstText.setText(inputName.substring(0,1));
                            }catch (Exception e){
                                tv_dialog_firstText.setText(context.getResources().getString(R.string.init_shortcuts));
                                e.printStackTrace();
                            }

                        }else{
                            tv_dialog_firstText.setText("?");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputName = String.valueOf(et_dialog_urlName.getText());
                    String inputUrl = String.valueOf(et_dialog_url.getText());
                    String firstName = inputName.substring(0,1);

                    ArrayList<UrlArray> urlArrays = new ArrayList<>();
                    urlArrays = new SharedWPU().getUrlArrayList(context);
                    urlArrays.add(new UrlArray(inputUrl,inputName,firstName,color));

                    new UrlArrayManager().setUrlArrayList(context,urlArrays);
                    MainActivity.listRefresh(context);
                    UrlListFragment.listRefresh(context);
                    UrlListFragment.setUrlArrays(context);
                    WebViewFragment.setStar(context);
                    dismiss();
                }
            });
            btn_dialogNagative = findViewById(R.id.btn_dialogNagative);
            btn_dialogNagative.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogNagative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });



            Window window = getWindow();
            if( window != null ) {
                // 백그라운드 투명
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams params = window.getAttributes();
                // 화면에 가득 차도록
                params.width         = WindowManager.LayoutParams.MATCH_PARENT;
                params.height        = WindowManager.LayoutParams.MATCH_PARENT;

                // 열기&닫기 시 애니메이션 설정
                params.windowAnimations = R.style.AnimationPopupStyle;
                window.setAttributes( params );
                window.setGravity( Gravity.BOTTOM );
            }
        }
    }
}
