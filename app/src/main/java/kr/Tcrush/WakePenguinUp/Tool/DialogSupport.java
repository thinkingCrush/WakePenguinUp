package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;

public class DialogSupport {

    private static DialogManager.addItemDialog addItemDialog;
    public void addItemDialog(Context context, String url){
        try{
            if(addItemDialog!=null){
                if(!addItemDialog.isShowing()){
                    addItemDialog = new DialogManager.addItemDialog(context,randomColor(),url );
                    addItemDialog.show();
                }
            }else{
                addItemDialog = new DialogManager.addItemDialog(context,randomColor(),url );
                addItemDialog.show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static DialogManager.editItemDialog editItemDialog ;
    public void editItemDialog (Context context, ArrayList<UrlArray> urlArrays, int itemPosition, boolean urlFragment){
        try{

            if(editItemDialog != null){
                if(!editItemDialog.isShowing()){
                    editItemDialog = new DialogManager.editItemDialog(context,urlArrays,itemPosition,urlFragment);
                    editItemDialog.show();
                }
            }else{
                editItemDialog = new DialogManager.editItemDialog(context,urlArrays,itemPosition,urlFragment);
                editItemDialog.show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static DialogManager.urlList_addItemDialog urlList_addItemDialog;
    public void urlList_addItemDialog(Context context, String url){
        try{
            if(urlList_addItemDialog != null){
                if(!urlList_addItemDialog.isShowing()){
                    urlList_addItemDialog = new DialogManager.urlList_addItemDialog(context,randomColor(),url);
                    urlList_addItemDialog.show();
                }
            }else{
                urlList_addItemDialog = new DialogManager.urlList_addItemDialog(context,randomColor(),url);
                urlList_addItemDialog.show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String randomColor(){
        try{
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            return String.format("#%06X", 0xFFFFFF & color);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "#FC1234";
    }
}
