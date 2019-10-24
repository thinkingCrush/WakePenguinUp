package kr.Tcrush.WakePenguinUp.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import kr.Tcrush.WakePenguinUp.R;

public class HelpViewPagerAdapter extends PagerAdapter {


    private int[] images = {R.drawable.img_help_1,R.drawable.img_help_2};
    private LayoutInflater inflater;
    private Context context;
    public HelpViewPagerAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return images.length;
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
        iv_helpImage.setImageResource(images[position]);
        container.addView(v);
        return v;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
