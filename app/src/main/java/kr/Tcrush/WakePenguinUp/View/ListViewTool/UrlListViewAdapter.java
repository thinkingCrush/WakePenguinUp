package kr.Tcrush.WakePenguinUp.View.ListViewTool;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.R;

public class UrlListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private ArrayList<UrlArray> urlListArrayList = null;
    private UrlListViewAdapter.ViewHolder viewHolder = null;

    private Context mContext = null;

    public UrlListViewAdapter(Context context , ArrayList<UrlArray> urlListArrayList){
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.urlListArrayList = urlListArrayList;
    }

    // Adapter가 관리할 Data의 개수를 설정 합니다.
    @Override
    public int getCount() {
        return urlListArrayList.size();
    }

    // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻어 옵니다.
    @Override
    public UrlArray getItem(int position) {
        return urlListArrayList.get(position);
    }

    // Adapter가 관리하는 Data의 Item 의 position 값의 ID 를 얻어 옵니다.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ListView의 뿌려질 한줄의 Row를 설정 합니다.
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        View v = convertview;

        if(v == null){
            viewHolder = new UrlListViewAdapter.ViewHolder();
            v = inflater.inflate(R.layout.listitem_urllist_fragment, null);
            viewHolder.iv_deleteIcon = v.findViewById(R.id.iv_deleteIcon);
            viewHolder.fl_urlList_icon = v.findViewById(R.id.fl_urlList_icon);
            viewHolder.tv_urlList_icon = v.findViewById(R.id.tv_urlList_icon);
            viewHolder.tv_urllist_name = v.findViewById(R.id.tv_urllist_name);
            viewHolder.iv_listDrag = v.findViewById(R.id.iv_listDrag);


            v.setTag(viewHolder);

        }else {
            viewHolder = (UrlListViewAdapter.ViewHolder)v.getTag();
        }

        try{
            String strUrl = getItem(position).url;
            String strUrlName = getItem(position).urlName;
            String textBgColor = getItem(position).textBgColor;
            String firstText = getItem(position).urlFirstText;


            Drawable roundDrawable = mContext.getResources().getDrawable(R.drawable.drawerlayout_listitem_icon_background,null);
            roundDrawable.setColorFilter(Color.parseColor(textBgColor), PorterDuff.Mode.SRC_ATOP);
            viewHolder.fl_urlList_icon.setBackground(roundDrawable);

            viewHolder.tv_urlList_icon.setText(firstText);

            viewHolder.tv_urllist_name.setText(strUrlName);
        }catch (Exception e){
            e.printStackTrace();
        }



        return v;
    }

    // Adapter가 관리하는 Data List를 교체 한다.
    // 교체 후 Adapter.notifyDataSetChanged() 메서드로 변경 사실을
    // Adapter에 알려 주어 ListView에 적용 되도록 한다.
    public void setArrayList(ArrayList<UrlArray> arrays){
        this.urlListArrayList = arrays;
    }

    public ArrayList<UrlArray> getArrayList(){
        return urlListArrayList;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {


            }
        }
    };

    class ViewHolder{

        public ImageView iv_deleteIcon = null;
        public FrameLayout fl_urlList_icon =null;
        public TextView tv_urlList_icon = null;
        public TextView tv_urllist_name = null;
        public ImageView iv_listDrag = null;



    }

    @Override
    protected void finalize() throws Throwable {
        init();
        super.finalize();
    }

    private void init(){
        inflater = null;
        urlListArrayList = null;
        viewHolder = null;
        mContext = null;
    }
}
