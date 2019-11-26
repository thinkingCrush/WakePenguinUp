package kr.Thinkingcrush.WakePenguinUp.Data;

import android.content.Context;

import java.util.ArrayList;

import kr.Thinkingcrush.WakePenguinUp.Tool.SharedWPU;

public class UrlArrayManager {

    //ArrayList 관리해야함
    //ArrayLsit 추가 하는거랑, 편집, 삭제 필요.


    //URL ArrayList 가져오기
    public ArrayList<UrlArray> initUrlArrayList(Context context){
        try{
            ArrayList<UrlArray> arrays = new ArrayList<>();
            arrays = new SharedWPU().getUrlArrayList(context);
            if(arrays != null && !arrays.isEmpty()){
                //필요할때마다 가져다 쓸것인지..
                return arrays;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //URL ArrayList 세팅
    public void setUrlArrayList(Context context, ArrayList<UrlArray> urlArrays){
        try{
            new SharedWPU().setUrlArrayList(context,urlArrays);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //item 삭제 , click position 알고잇겟지
    public void deleteUrlItem(Context context, ArrayList<UrlArray> urlArrays , int clickItem){
        try{
            urlArrays.remove(clickItem);
            new SharedWPU().setUrlArrayList(context,urlArrays);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
