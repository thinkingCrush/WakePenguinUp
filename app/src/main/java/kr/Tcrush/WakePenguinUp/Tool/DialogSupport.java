package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class DialogSupport {

    private static DialogManager.positiveNegativeDialog positiveNegativeDialog;
    public void doDialog(Context context, String title, String message){
        try{
            positiveNegativeDialog = new DialogManager.positiveNegativeDialog(context,
                    title,
                    message,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Dialog Click
                            try{

                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                            positiveNegativeDialog.cancel();
                        }
                    }, null);
            positiveNegativeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            positiveNegativeDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
