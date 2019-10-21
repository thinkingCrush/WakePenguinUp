package kr.Tcrush.WakePenguinUp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.Tcrush.WakePenguinUp.Tool.Dlog;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dlog.e("test 1111");
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
