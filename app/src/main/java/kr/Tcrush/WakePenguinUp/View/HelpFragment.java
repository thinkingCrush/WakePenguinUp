package kr.Tcrush.WakePenguinUp.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;

import java.util.Objects;

import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.SharedWPU;
import me.relex.circleindicator.CircleIndicator;

public class HelpFragment extends Fragment implements View.OnClickListener {

    private static FloatingActionButton fb_help;
    //ImageView iv_help ;
    ViewPager vp_imageView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help,container,false);
        initView(view);
        MainActivity.setPageNum(PageNumber.HelpFragment.ordinal());
        return view;
    }
    private void initView (View view){
        fb_help = view.findViewById(R.id.fb_help);
        fb_help.setOnClickListener(this);
        vp_imageView = view.findViewById(R.id.vp_imageView);
        HelpViewPagerAdapter helpViewPagerAdapter = new HelpViewPagerAdapter(getContext());
        vp_imageView.setAdapter(helpViewPagerAdapter);
        final Animation animation1 = AnimationUtils.loadAnimation(getContext(),R.anim.animation_help_zoom_out);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fb_help.clearAnimation();
                fb_help.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        final Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.animation_help_zoom_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fb_help.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        vp_imageView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try{
                    if(position == 0){
                        fb_help.startAnimation(animation1);
                    }else if(position == 1){
                        fb_help.setVisibility(View.INVISIBLE);
                        fb_help.startAnimation(animation);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        new SharedWPU().setUrlArrayList(getContext(),new SharedWPU().getDefaultArray());
        MainActivity.listRefresh(getContext());

        CircleIndicator indicator = view.findViewById(R.id.ci_help);
        indicator.dip2px(3);
        indicator.setViewPager(vp_imageView);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fb_help :
                ((MainActivity) Objects.requireNonNull(getActivity())).mainChangeMenu(new WebViewFragment(),null);
                break;
        }
    }


}
