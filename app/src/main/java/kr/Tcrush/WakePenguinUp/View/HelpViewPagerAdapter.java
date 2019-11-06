package kr.Tcrush.WakePenguinUp.View;

import android.content.Context;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import java.util.Locale;

import kr.Tcrush.WakePenguinUp.R;

public class HelpViewPagerAdapter extends PagerAdapter {


    private int[] images_ko = {R.drawable.img_page_help_ko_1,R.drawable.img_page_help_ko_2};
    private int[] images_en = {R.drawable.img_page_help_en_1,R.drawable.img_page_help_en_2};
    private LayoutInflater inflater;
    private Context context;
    public HelpViewPagerAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return images_ko.length;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.viewpager_help, container, false);
        ImageView iv_helpImage = v.findViewById(R.id.iv_helpImage);
        if(checkKorea(context)){
            iv_helpImage.setImageResource(images_ko[position]);
        }else{
            iv_helpImage.setImageResource(images_en[position]);
        }

        container.addView(v);
        return v;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

    private boolean checkKorea (Context context){
        try{
            Locale locale = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = context.getResources().getConfiguration().getLocales().get(0);
            }else{
                locale = context.getResources().getConfiguration().locale;
            }

            if(locale!= null){
                String country = locale.getCountry();
                if(country.equals("KR")){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
