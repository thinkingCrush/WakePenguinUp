package kr.Tcrush.WakePenguinUp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.Tool.CheckPermission;
import kr.Tcrush.WakePenguinUp.Tool.DialogSupport;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.Tool.VibratorSupport;
import kr.Tcrush.WakePenguinUp.Tool.ViewClickEffect;
import kr.Tcrush.WakePenguinUp.View.Floating.FloatingService;
import kr.Tcrush.WakePenguinUp.View.Floating.FloatingViewController;
import kr.Tcrush.WakePenguinUp.View.HelpFragment;
import kr.Tcrush.WakePenguinUp.View.ListViewTool.UrlListAdapter;
import kr.Tcrush.WakePenguinUp.View.PageNumber;
import kr.Tcrush.WakePenguinUp.View.UrlListFragment;
import kr.Tcrush.WakePenguinUp.View.WebViewFragment;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    static DrawerLayout mDrawerLayout;
    static SlideAndDragListView sd_listview;
    static RelativeLayout drawerContainer;
    static TextView tv_emptySide;
    ImageView iv_sideListEdit ;
    ImageView iv_editTimer;

    public static Context mainContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dlog.e("MainActivity onCreate");
        setContentView(R.layout.activity_main);
        mainContext=getBaseContext();
        initFragment(getBaseContext());
        initView();
        CheckPermission.checkPermission(this);
        initTouchListener();
    }


    private static long time =0;
    @Override
    public void onBackPressed() {
        try{
            if(mDrawerLayout.isDrawerOpen(drawerContainer)){
                mDrawerLayout.closeDrawer(drawerContainer);
            }else{
                if(pageNumber== PageNumber.HelpFragment.ordinal()||
                        pageNumber == PageNumber.WebViewFragment.ordinal()){
                    if(new WebViewFragment().canGoback()){
                        new WebViewFragment().goBack();
                    }else{
                        if(System.currentTimeMillis()-time>=2000){
                            time = System.currentTimeMillis();
                            Toast.makeText(getBaseContext(),getBaseContext().getResources().getString(R.string.popup_mainBackPressNoti),Toast.LENGTH_LONG).show();

                        }else if(System.currentTimeMillis() -time < 2000){
                            stopFloatingBackPressed(getBaseContext());
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }
                            },300);
                        }
                    }
                }else if(pageNumber == PageNumber.UrlListFragment.ordinal()){
                    mainChangeMenu(new WebViewFragment(),null);
                    //startService(getBaseContext());
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(new SharedWPU().getNotFirstUser(getBaseContext())){
        }
    }

    public static Intent intent ;
    public static boolean isFloating = false;

    public static void startService(Context context){
        Dlog.e("startService isFloating : " + isFloating);
        try{
            if(!isFloating){
                if(context != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(Settings.canDrawOverlays(context) ){
                            if(intent == null){
                                intent = new Intent(context, FloatingService.class);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Objects.requireNonNull(context).startForegroundService(intent);
                            }else {
                                Objects.requireNonNull(context).startService(intent);
                            }
                        }
                    }


                }
                isFloating = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void visibleFloating(){
        new FloatingViewController().floatingVisible();
    }

    public static boolean destroyFlag = false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyFlag = true;
        //Dlog.e("onDestroy!!!!");
        finishService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishService(this);
    }

    private static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        try{
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null) {
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (serviceClass.getName().equals(service.service.getClassName())) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void finishService(final Context context){
        try{
            Dlog.e("finishService touchLockFlag : " + TouchLockFlag);
            if(!TouchLockFlag){
                if(context!=null){
                    if(isMyServiceRunning(context,FloatingService.class)){
                        if(intent != null){
                            context.stopService(intent);

                        }
                    }
                }
                isFloating = false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void stopFloating(){
        new FloatingViewController().floatingGone();
    }

    public static void stopFloatingBackPressed(final Context context){
        try{
            if(context!=null){
                if(isMyServiceRunning(context,FloatingService.class)){
                    if(intent != null){
                        context.stopService(intent);

                    }
                }
            }
            isFloating = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mainChangeMenu(Fragment changeFragment, String animation){
        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("Fragment");
            if(fragment != null){
                try{
                    if(fragmentManager!=null){
                        fragmentManager.popBackStackImmediate("Fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(fragmentManager!=null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,changeFragment,"Fragment");
                try{
                    fragmentTransaction.commitNowAllowingStateLoss();
                }catch (Exception e){
                    e.printStackTrace();
                    fragmentTransaction.commitNow();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initFragment(Context context){
        //도움말 먼저 보여줄 것인지 아닌지 보고 mainChangeMenu
        if(new SharedWPU().getNotFirstUser(context)){
            //한번 들어왔던 사람임
            mainChangeMenu(new WebViewFragment(),null);
        }else{
            //한번도 안들어온 사람
            mainChangeMenu(new HelpFragment(),null);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(){
        sd_listview = findViewById(R.id.sd_listview);
        tv_emptySide = findViewById(R.id.tv_emptySide);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //inishService(mainContext);
                MainActivity.stopFloating();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(pageNumber != PageNumber.UrlListFragment.ordinal()){
                    Dlog.e("test 3333");
                    //startService(mainContext);
                    MainActivity.visibleFloating();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        drawerContainer = findViewById(R.id.drawerContainer);
        iv_sideListEdit = findViewById(R.id.iv_sideListEdit);
        iv_sideListEdit.setOnTouchListener(new ViewClickEffect());
        iv_sideListEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(drawerContainer);
                mainChangeMenu(new UrlListFragment(),"Right");
                //finishService(getBaseContext());
                MainActivity.stopFloating();

            }
        });

        iv_editTimer = findViewById(R.id.iv_editTimer);
        iv_editTimer.setOnTouchListener(new ViewClickEffect());
        iv_editTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(drawerContainer);
                MainActivity.stopFloating();

                //Dialog 가 생성되야함.
                new DialogSupport().alarmDialog(MainActivity.this);
            }
        });
        initDrawerListView();
    }

    private static BaseAdapter sideListAdapter ;
    private void initDrawerListView(){
        Menu menu = new Menu(false, 0);
        menu.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.parseColor("#ffffff")))
                .build());

        sd_listview.setMenu(menu);

        sideListAdapter =new UrlListAdapter(getBaseContext(),new SharedWPU().getUrlArrayList(getBaseContext()));
        sd_listview.setAdapter(sideListAdapter);
        sd_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try{

                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                    ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(getBaseContext());
                    UrlArray urlArray = urlArrays.get(position);
                    new WebViewFragment().loadUrl(getBaseContext(),urlArray.url);
                    mDrawerLayout.closeDrawer(drawerContainer);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        });


    }


    public static void listRefresh(Context context){
        try{
            if(sideListAdapter != null){
                ArrayList<UrlArray> urlArrays = new SharedWPU().getUrlArrayList(context);
                if(urlArrays != null && !urlArrays.isEmpty()){
                    checkSidebar(false);
                    sideListAdapter =new UrlListAdapter(context,new SharedWPU().getUrlArrayList(context));
                    sideListAdapter.notifyDataSetChanged();
                    sd_listview.setAdapter(sideListAdapter);
                }else{
                    checkSidebar(true);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 사이드바 나오면 플로팅 버튼 없애고,
     * 사이드바 들어가면 다시 플로팅 버튼 나오게
     * */
    public static class DrawerClickListener implements View.OnClickListener {
        Context context;
        public DrawerClickListener (Context context){
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            try{
                Dlog.e("test 1111");
                if(mDrawerLayout != null){
                    mDrawerLayout.openDrawer(drawerContainer);
                }

                try{
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }



            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public static boolean isPermission(Context context, String strPermission){
        return ActivityCompat.checkSelfPermission(context, strPermission) == PackageManager.PERMISSION_GRANTED;
    }



    /**
     * TEST
     * */

    public static boolean TouchLockFlag = false;
    private static Timer finishTimer = null;
    public void touchLock(){
        try{
            Dlog.e("touch Lock");
            TouchLockFlag = true;
            registerTouch();

            boolean isFinishTimer = new SharedWPU().getAlarm(mainContext);
            if(isFinishTimer){
                String timerTime = new SharedWPU().getAlarmTime(mainContext);
                int hour = Integer.parseInt(timerTime.split("/")[0])*1000*60*60;
                int min = Integer.parseInt(timerTime.split("/")[1])*1000*60;
                Dlog.e("hour : " + hour + " , min : " + min);
                //hour -> ms
                //min -> ms

                finishTimer = new Timer();
                finishTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        new FloatingViewController().finishText(mainContext.getResources().getString(R.string.popup_timer_finish));
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Dlog.e("Timer Finish!!");
                                stopFloatingBackPressed(getBaseContext());
                                finishAffinity();
                                System.runFinalization();
                                System.exit(0);
                                finishTimer.cancel();
                                finishTimer = null;
                            }
                        },3000);
                    }
                },hour+min,hour+min);


            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public void touchUnLock(Context context){
        try{
            Dlog.e("touch Un Lock");
            TouchLockFlag = false;
            unRegisterTouch();
            new FloatingViewController().floatingVisible();
            new FloatingViewController().wakeUpAnimation();
            new FloatingViewController().startGif(R.drawable.change_image_wakeup);
            if(finishTimer!=null){
                finishTimer.cancel();
                finishTimer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try{
            if(ev.getAction() == MotionEvent.ACTION_DOWN){
                if(TouchLockFlag){
                    return false;
                }else{
                    WebViewFragment.checkEditTextTouch(getBaseContext(),ev);
                    return super.dispatchTouchEvent(ev);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            return super.dispatchTouchEvent(ev);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;

    }



    private static SensorManager sensorManager;
    private static Sensor sensor;

    private void initTouchListener(){
        try{
            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            if(sensorManager!=null){
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void registerTouch(){
        try{
            if(sensorManager != null){
                sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void unRegisterTouch(){
        try{
            sensorManager.unregisterListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private long shakeTime ;
    private static final int SHAKE_SKIP_TIME = 600;
    private static final float SHAKE_THRESHOLD_GRAVITY = 6.0f;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float axisX = sensorEvent.values[0];
            float axisY = sensorEvent.values[1];
            float axisZ = sensorEvent.values[2];

            float gravityX = axisX/SensorManager.GRAVITY_EARTH;
            float gravityY = axisY/SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ/SensorManager.GRAVITY_EARTH;

            Float f = (gravityX * gravityX) +(gravityY * gravityY) + (gravityZ * gravityZ);
            double squaredD = Math.sqrt(f.doubleValue());
            float gForce = (float)squaredD;
            if(gForce > SHAKE_THRESHOLD_GRAVITY){
                long currentTime = System.currentTimeMillis();
                if(shakeTime + SHAKE_SKIP_TIME > currentTime){
                    return;
                }
                shakeTime = currentTime;
                shakeTime ++;
                new VibratorSupport().doVibrator(mainContext,300);
                touchUnLock(mainContext);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private static int pageNumber = -1;
    public static void setPageNum(int num){
        pageNumber = num;
    }


    public static void checkSidebar(final boolean empty){
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(empty){
                        sd_listview.setVisibility(View.GONE);
                        tv_emptySide.setVisibility(View.VISIBLE);
                    }else{
                        sd_listview.setVisibility(View.VISIBLE);
                        tv_emptySide.setVisibility(View.GONE);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
