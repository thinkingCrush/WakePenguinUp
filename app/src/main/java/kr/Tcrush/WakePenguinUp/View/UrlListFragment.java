package kr.Tcrush.WakePenguinUp.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.Objects;

import kr.Tcrush.WakePenguinUp.R;

public class UrlListFragment extends Fragment {

    SlideAndDragListView sd_urlEditList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview,container,false);
        initView(view);
        initListView();

        return view;
    }

    private void initView(View view){
        /**
         * 리스트 뷰 넣어야함
         * */

        sd_urlEditList = view.findViewById(R.id.sd_urlEditList);


    }

    private void initListView(){
        Menu menu = new Menu(true, 0);//the first parameter is whether can slide over
        menu.addItem(new MenuItem.Builder().setWidth(90)//set Width
                .setBackground(new ColorDrawable(Color.RED))// set background
                .setText("One")//set text string
                .setTextColor(Color.GRAY)//set text color
                .setTextSize(20)//set text size
                .setIcon(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.baseline_list_black_36,null))
                .build());
        menu.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)//set direction (default DIRECTION_LEFT)
                .setIcon(Objects.requireNonNull(getContext()).getResources().getDrawable(R.drawable.baseline_face_black_36,null))
                .build());
        sd_urlEditList.setMenu(menu);

        sd_urlEditList.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                switch (direction){
                    case MenuItem.DIRECTION_LEFT :
                        switch (buttonPosition){
                            case 0:
                                return Menu.ITEM_SCROLL_BACK ;
                        }
                        break;
                    case MenuItem.DIRECTION_RIGHT :
                        switch (buttonPosition){
                            case 0 :
                                return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP ;
                        }
                        break;
                        default:
                            return Menu.ITEM_NOTHING;
                }
                return Menu.ITEM_NOTHING;
            }
        });
        sd_urlEditList.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {

            }

            @Override
            public void onSlideClose(View view, View parentView, int position, int direction) {

            }
        });

    }


}
