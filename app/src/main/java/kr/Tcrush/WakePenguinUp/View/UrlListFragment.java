package kr.Tcrush.WakePenguinUp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.DialogSupport;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import kr.Tcrush.WakePenguinUp.Tool.VibratorSupport;
import kr.Tcrush.WakePenguinUp.Tool.ViewClickEffect;
import kr.Tcrush.WakePenguinUp.View.ListViewTool.UrlListViewAdapter;

public class UrlListFragment extends Fragment {

    static SlideAndDragListView sd_urlEditList;

    ImageView iv_itemAdd;

    RelativeLayout rl_urlArray;
    TextView tv_url_errorMessage;
    ImageView iv_url_errorImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urllist,container,false);
        MainActivity.setPageNum(PageNumber.UrlListFragment.ordinal());
        initView(view);
        initListView();

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view){

        sd_urlEditList = view.findViewById(R.id.sd_urlEditList);
        iv_itemAdd = view.findViewById(R.id.iv_itemAdd);
        iv_itemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogSupport().urlList_addItemDialog(getContext(),"https://");
            }
        });
        iv_itemAdd.setOnTouchListener(new ViewClickEffect());


        rl_urlArray = view.findViewById(R.id.rl_urlArray);
        tv_url_errorMessage = view.findViewById(R.id.tv_url_errorMessage);
        iv_url_errorImage = view.findViewById(R.id.iv_url_errorImage);

        initImageViewHandler();


    }

    private static ArrayList<UrlArray> urlArrays = new ArrayList<>();
    private static UrlArray dragUrlArray = null;
    private static BaseAdapter urlArrayListAdapter;
    private void initListView(){


        urlArrays = new SharedWPU().getUrlArrayList(getContext());

        try{
            setListView();
            if(urlArrays != null && !urlArrays.isEmpty()){
                urlArrayListAdapter = new UrlListViewAdapter(getContext(),urlArrays);
                sd_urlEditList.setAdapter(urlArrayListAdapter);
                listViewVisible();
            }else{
                listViewEmpty(getContext());
            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public static void listRefresh(Context context){
        try{
            if(sd_urlEditList != null){
                ArrayList<UrlArray> urlArrays =new SharedWPU().getUrlArrayList(context);
                if(urlArrays != null && !urlArrays.isEmpty()){
                    sd_urlEditList.deferNotifyDataSetChanged();
                    urlArrayListAdapter = new UrlListViewAdapter(context,urlArrays);
                    sd_urlEditList.setAdapter(urlArrayListAdapter);
                    listViewVisible();
                }else{
                    listViewEmpty(context);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private static Handler viewImageHandler = null;
    private static final int ListViewFlag = 1;
    private static final int ListEmptyFlag = 2;
    private void initImageViewHandler(){
        try{
            viewImageHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {

                    switch (msg.what){
                        case ListViewFlag :
                            sd_urlEditList.setVisibility(View.VISIBLE);
                            rl_urlArray.setVisibility(View.GONE);
                            tv_url_errorMessage.setVisibility(View.GONE);
                            iv_url_errorImage.setVisibility(View.GONE);
                            break;
                        case ListEmptyFlag :
                            sd_urlEditList.setVisibility(View.GONE);
                            rl_urlArray.setVisibility(View.VISIBLE);
                            tv_url_errorMessage.setVisibility(View.VISIBLE);
                            iv_url_errorImage.setVisibility(View.VISIBLE);
                            iv_url_errorImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_findfail_url,null));
                            tv_url_errorMessage.setText(getContext().getResources().getString(R.string.message_02));
                            break;
                    }

                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void listViewVisible(){
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(ListViewFlag,null).sendToTarget();
        }
    }
    public static void listViewEmpty(Context context){
        if(viewImageHandler != null){
            viewImageHandler.obtainMessage(ListEmptyFlag,null).sendToTarget();
        }
        MainActivity.listRefresh(context);
    }


    private void setListView(){
        try{
            Menu menu = new Menu(true, 0);//the first parameter is whether can slide over
            menu.addItem(new MenuItem.Builder().setWidth(200) // buttonPosition : 0
                    .setBackground(new ColorDrawable(Color.RED))
                    .setText(getContext().getResources().getString(R.string.basic_delete))
                    .setTextColor(Color.WHITE)
                    .setTextSize(15)
                    .setDirection(MenuItem.DIRECTION_RIGHT)
                    .build());
            menu.addItem(new MenuItem.Builder().setWidth(200) // buttonPosition : 1
                    .setBackground(new ColorDrawable(Color.GRAY))
                    .setText(getContext().getResources().getString(R.string.basic_edit))
                    .setTextColor(Color.WHITE)
                    .setTextSize(15)
                    .setDirection(MenuItem.DIRECTION_RIGHT)
                    .build());

            sd_urlEditList.setMenu(menu);

            sd_urlEditList.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
                @Override
                public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                    switch (direction){
                        case MenuItem.DIRECTION_RIGHT :
                            switch (buttonPosition){
                                case 0 : // 삭제
                                    urlArrays.remove(itemPosition);
                                    new SharedWPU().setUrlArrayList(getContext(),urlArrays);
                                    MainActivity.listRefresh(getContext());
                                    if(urlArrays != null && !urlArrays.isEmpty()){
                                        listViewVisible();
                                        sd_urlEditList.deferNotifyDataSetChanged();
                                        BaseAdapter urlArrayList = new UrlListViewAdapter(getContext(),urlArrays);
                                        sd_urlEditList.setAdapter(urlArrayList);
                                    }else{
                                        listViewEmpty(getContext());
                                    }


                                    return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP ;
                                case 1 : // 수정
                                    new DialogSupport().editItemDialog(getContext(),urlArrays,itemPosition);
                                    return Menu.ITEM_NOTHING;
                            }
                            break;
                        default:
                            return Menu.ITEM_NOTHING;
                    }
                    return Menu.ITEM_NOTHING;
                }
            });

            sd_urlEditList.setOnDragDropListener(new SlideAndDragListView.OnDragDropListener() {
                @Override
                public void onDragViewStart(int beginPosition) {
                    try{
                        dragUrlArray = urlArrays.get(beginPosition);
                        new VibratorSupport().doTick(getContext());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onDragDropViewMoved(int fromPosition, int toPosition) {
                    try{
                        UrlArray fromUrlArray = urlArrays.remove(fromPosition);
                        urlArrays.add(toPosition,fromUrlArray);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onDragViewDown(int finalPosition) {
                    try{
                        urlArrays.set(finalPosition,dragUrlArray);
                        new SharedWPU().setUrlArrayList(getContext(),urlArrays);
                        MainActivity.listRefresh(getContext());

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setUrlArrays(Context context){
        try{
            urlArrays = new SharedWPU().getUrlArrayList(context);
            urlArrayListAdapter = new UrlListViewAdapter(context,urlArrays);
            sd_urlEditList.setAdapter(urlArrayListAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
