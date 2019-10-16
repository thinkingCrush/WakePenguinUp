package kr.Tcrush.WakePenguinUp.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.clans.fab.FloatingActionButton;

import java.util.Objects;

import kr.Tcrush.WakePenguinUp.MainActivity;
import kr.Tcrush.WakePenguinUp.R;
import kr.Tcrush.WakePenguinUp.Tool.Dlog;

public class HelpFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fb_help;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help,container,false);
        initView(view);

        return view;
    }
    private void initView (View view){
        fb_help = view.findViewById(R.id.fb_help);
        fb_help.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fb_help :
                //현재 어떤 이미지를 보여주고 있는지 확인하고, 마지막 이미지 면 화면 넘겨야한다.
                // 지금은 그냥 화면 넘기자
                ((MainActivity) Objects.requireNonNull(getActivity())).mainChageMenu(new WebViewFragment());
                /*Toast.makeText(getContext(), "CLICK", Toast.LENGTH_SHORT).show();
                if(!MainActivity.TouchLockFlag){
                    new MainActivity().touchLock();
                }*/

                break;
        }
    }
}
