package kr.Tcrush.WakePenguinUp.Tool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.Tcrush.WakePenguinUp.R;

public class DialogManager  extends AlertDialog.Builder {

    public DialogManager(Context context) {
        super(context);
    }

    // 확인, 취소 버튼 다이얼로그
    public static class positiveNegativeDialog extends Dialog {
        Context context;
        TextView tv_dialogPositiveNagative_title;
        TextView tv_dialogPositiveNagative_message;
        static Button btn_dialogPositive;
        static Button btn_dialogNagative;

        String title;
        String message;

        View.OnClickListener onClickListener;
        View.OnClickListener onNegativeClickListener;

        String deleteDialogText = null;
        String cancleDialogText = null;

        EditText et_dialog_urlName, et_dialog_url;

        public positiveNegativeDialog(@NonNull Context context, String title, String message, View.OnClickListener onClickListener,View.OnClickListener onNegativeClickListener) {
            super(context);

            this.context = context;
            this.title = title;
            this.message = message;
            this.onClickListener = onClickListener;
            this.onNegativeClickListener = onNegativeClickListener;
        }

        public void changeBtnText(String deleteDialogText, String cancleDialogText){
            this.deleteDialogText = deleteDialogText;
            this.cancleDialogText = cancleDialogText;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_positive_negative);

            et_dialog_url = findViewById(R.id.et_dialog_url);
            et_dialog_urlName = findViewById(R.id.et_dialog_urlName);

            tv_dialogPositiveNagative_title = findViewById(R.id.tv_dialogPositiveNagative_title);
            tv_dialogPositiveNagative_title.setText(title);
            tv_dialogPositiveNagative_message = findViewById(R.id.tv_dialogPositiveNagative_message);
            tv_dialogPositiveNagative_message.setText(message);

            btn_dialogPositive = findViewById(R.id.btn_dialogPositive);
            btn_dialogPositive.setOnTouchListener(new DialogButtonClickEffect());
            if(onClickListener != null){
                btn_dialogPositive.setOnClickListener(onClickListener);
            }
            btn_dialogNagative = findViewById(R.id.btn_dialogNagative);
            btn_dialogNagative.setOnTouchListener(new DialogButtonClickEffect());
            if(onNegativeClickListener != null){
                btn_dialogNagative.setOnClickListener(onNegativeClickListener);
                btn_dialogPositive.setText(deleteDialogText);
                btn_dialogNagative.setText(cancleDialogText);
            }else {
                btn_dialogNagative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
