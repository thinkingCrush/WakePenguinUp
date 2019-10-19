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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;

public class UrlListAdapter extends BaseAdapter{

    private LayoutInflater inflater = null;
    private ArrayList<UrlArray> urlListArrayList = null;
    private ViewHolder viewHolder = null;

    private Context mContext = null;

    public UrlListAdapter(Context context , ArrayList<UrlArray> urlListArrayList){
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
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.listitem_urllist, null);
            viewHolder.fl_drawer_icon_background = v.findViewById(R.id.fl_drawer_icon_background);
            viewHolder.tv_urlFirstText = v.findViewById(R.id.tv_urlFirstText);
            viewHolder.tv_strAddressName = v.findViewById(R.id.tv_strAddressName);
            viewHolder.ll_drawerList = v.findViewById(R.id.ll_drawerList);


            v.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)v.getTag();
        }

        String strUrl = getItem(position).url;
        String strUrlName = getItem(position).urlName;
        String textBgColor = getItem(position).textBgColor;
        String firstText = getItem(position).urlFirstText;

        Dlog.e("strUrl : " + strUrl);
        Dlog.e("strUrlName : " + strUrlName);
        viewHolder.tv_strAddressName.setText(String.valueOf(strUrlName));
        Drawable roundDrawable = mContext.getResources().getDrawable(R.drawable.drawerlayout_listitem_icon_background,null);

        roundDrawable.setColorFilter(Color.parseColor(textBgColor), PorterDuff.Mode.SRC_ATOP);
        viewHolder.fl_drawer_icon_background.setBackground(roundDrawable);

        viewHolder.tv_urlFirstText.setText(firstText);



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

        public LinearLayout ll_drawerList = null;

        public FrameLayout fl_drawer_icon_background = null;
        public TextView tv_strAddressName = null;
        public TextView tv_urlFirstText = null;



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

