package kr.Thinkingcrush.WakePenguinUp.Tool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import kr.Thinkingcrush.WakePenguinUp.Data.UrlArray;
import kr.Thinkingcrush.WakePenguinUp.Data.UrlArrayManager;
import kr.Thinkingcrush.WakePenguinUp.MainActivity;
import kr.Thinkingcrush.WakePenguinUp.R;
import kr.Thinkingcrush.WakePenguinUp.View.UrlListFragment;
import kr.Thinkingcrush.WakePenguinUp.View.WebViewFragment;

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
            //MainActivity.startService(context);
            MainActivity.visibleFloating();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_positive_negative);

            //MainActivity.finishService(context);
            MainActivity.stopFloating();

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

        boolean urlFragment;
        public editItemDialog(@NonNull Context context,ArrayList<UrlArray> urlArrays , int itemPosition , boolean urlFragment) {
            super(context);

            this.context = context;
            this.urlArrays = urlArrays;
            this.itemPosition = itemPosition;
            this.urlFragment = urlFragment;
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            //MainActivity.startService(context);
            if(!urlFragment){
                MainActivity.visibleFloating();
            }

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //MainActivity.finishService(context);
            MainActivity.stopFloating();
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

    public static class AlarmDialog extends Dialog {
        Context context;

        Switch sw_timerOnOff;
        TimePicker tp_timerPicker;

        static Button btn_dialogPositive;
        static Button btn_dialogNagative;


        String hour;
        String min;

        static int selectHour = 0;
        static int selectMin = 0;

        public AlarmDialog(@NonNull Context context, String hour , String min ) {
            super(context);

            this.context = context;
            this.hour = hour;
            this.min = min;
            selectHour = Integer.parseInt(hour);
            selectMin = Integer.parseInt(min);


        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_alarm);


            sw_timerOnOff = findViewById(R.id.sw_timerOnOff);
            tp_timerPicker = findViewById(R.id.tp_timerPicker);
            tp_timerPicker.setIs24HourView(true);
            tp_timerPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(final TimePicker timePicker, int i, int i1) {
                    selectHour = i;
                    selectMin = i1;
                    if(i == 0 && i1 == 0){
                        try{
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(selectHour==0 && selectMin == 0){
                                        timePicker.setMinute(1);
                                    }
                                }
                            },500);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            });

            try{
                tp_timerPicker.setHour(Integer.parseInt(hour));
                tp_timerPicker.setMinute(Integer.parseInt(min));
            }catch (Exception e){
                tp_timerPicker.setHour(0);
                tp_timerPicker.setMinute(1);
                e.printStackTrace();
            }


            boolean alarmEnable = new SharedWPU().getAlarm(context);
            sw_timerOnOff.setChecked(alarmEnable);


            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SharedWPU().setAlarm(context,sw_timerOnOff.isChecked());
                    new SharedWPU().setAlarmTime(context,String.valueOf(tp_timerPicker.getHour()), String.valueOf(tp_timerPicker.getMinute()));
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



    public static class SensorDialog extends Dialog implements View.OnClickListener {
        Context context;


        TextView tv_sensorValue1,tv_sensorValue2,tv_sensorValue3,tv_sensorValue4,tv_sensorValue5;
        static Button btn_dialogPositive;
        static Button btn_dialogNagative;
        int value = 0;

        public SensorDialog(@NonNull Context context) {
            super(context);

            this.context = context;


        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_sensor);

            tv_sensorValue1 = findViewById(R.id.tv_sensorValue1);
            tv_sensorValue1.setOnClickListener(this);
            tv_sensorValue2 = findViewById(R.id.tv_sensorValue2);
            tv_sensorValue2.setOnClickListener(this);
            tv_sensorValue3 = findViewById(R.id.tv_sensorValue3);
            tv_sensorValue3.setOnClickListener(this);
            tv_sensorValue4 = findViewById(R.id.tv_sensorValue4);
            tv_sensorValue4.setOnClickListener(this);
            tv_sensorValue5 = findViewById(R.id.tv_sensorValue5);
            tv_sensorValue5.setOnClickListener(this);
            initViewHandler();

            int sensorValue = new SharedWPU().getSensor(context);
            value = sensorValue;
            switch (value){
                case 1:
                    select1();
                    break;
                case 2:
                    select2();
                    break;
                case 3:
                    select3();
                    break;
                case 4:
                    select4();
                    break;
                case 5:
                    select5();
                    break;
            }

            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            btn_dialogPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SharedWPU().setSensor(context,value);
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

        @Override
        public void onClick(View v) {
            try{
                switch (v.getId()){
                    case R.id.tv_sensorValue1 :
                        value = 1;
                        select1();
                        break;
                    case R.id.tv_sensorValue2 :
                        value = 2;
                        select2();
                        break;
                    case R.id.tv_sensorValue3 :
                        value = 3;
                        select3();
                        break;
                    case R.id.tv_sensorValue4 :
                        value = 4;
                        select4();
                        break;
                    case R.id.tv_sensorValue5 :
                        value = 5;
                        select5();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void select1(){
            if(viewHandler!=null){
                viewHandler.obtainMessage(1,null).sendToTarget();
            }
        }
        private void select2(){
            if(viewHandler!=null){
                viewHandler.obtainMessage(2,null).sendToTarget();
            }
        }
        private void select3(){
            if(viewHandler!=null){
                viewHandler.obtainMessage(3,null).sendToTarget();
            }
        }
        private void select4(){
            if(viewHandler!=null){
                viewHandler.obtainMessage(4,null).sendToTarget();
            }
        }
        private void select5(){
            if(viewHandler!=null){
                viewHandler.obtainMessage(5,null).sendToTarget();
            }
        }
        private static Handler viewHandler ;
        private void initViewHandler(){
            try{
                if(viewHandler == null){
                    viewHandler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(@NonNull Message msg) {
                            switch (msg.what){
                                case 1 :
                                    tv_sensorValue1.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_select,null));
                                    tv_sensorValue2.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue3.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue4.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue5.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue1.setTextColor(context.getResources().getColor(R.color.defaultWhite,null));
                                    tv_sensorValue2.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue3.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue4.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue5.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    break;
                                case 2 :
                                    tv_sensorValue1.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue2.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_select,null));
                                    tv_sensorValue3.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue4.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue5.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue1.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue2.setTextColor(context.getResources().getColor(R.color.defaultWhite,null));
                                    tv_sensorValue3.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue4.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue5.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    break;
                                case 3 :
                                    tv_sensorValue1.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue2.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue3.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_select,null));
                                    tv_sensorValue4.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue5.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue1.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue2.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue3.setTextColor(context.getResources().getColor(R.color.defaultWhite,null));
                                    tv_sensorValue4.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue5.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    break;
                                case 4 :
                                    tv_sensorValue1.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue2.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue3.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue4.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_select,null));
                                    tv_sensorValue5.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue1.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue2.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue3.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue4.setTextColor(context.getResources().getColor(R.color.defaultWhite,null));
                                    tv_sensorValue5.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    break;
                                case 5 :
                                    tv_sensorValue1.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue2.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue3.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue4.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_unselect,null));
                                    tv_sensorValue5.setBackground(context.getResources().getDrawable(R.drawable.background_popup_sensor_select,null));
                                    tv_sensorValue1.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue2.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue3.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue4.setTextColor(context.getResources().getColor(R.color.unselect,null));
                                    tv_sensorValue5.setTextColor(context.getResources().getColor(R.color.defaultWhite,null));
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
    }
}
