package kr.Tcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import kr.Tcrush.WakePenguinUp.Data.UrlArray;
import kr.Tcrush.WakePenguinUp.R;

public class SharedWPU {
    public boolean getNotFirstUser(Context context){
        SharedPreferences firstUser =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
        return firstUser.getBoolean("FirstUser",false);

    }
    public void setFirstUser(Context context){

        try{
            SharedPreferences firstUser =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firstUser.edit();
            editor.putBoolean("FirstUser",true);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * URL ArrayList ( UrlArray )
     * */
    private final String urlListKey = "UrlList";
    public void setUrlArrayList(Context context, ArrayList<UrlArray> urlArrayList){
        try{
            SharedPreferences urlArray =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = urlArray.edit();
            if (!urlArrayList.isEmpty()) {
                editor.putString(urlListKey, arrayToJson(urlArrayList));
            } else {
                editor.putString(urlListKey, null);
            }
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public ArrayList<UrlArray> getUrlArrayList(Context context){
        try{
            SharedPreferences urlArray =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
            ArrayList<UrlArray> urlArrayList = new ArrayList<>();

            String json = urlArray.getString(urlListKey, null);
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject jsonObject = a.getJSONObject(i);
                        UrlArray urlArrayData = new UrlArray(
                                jsonObject.get("url").toString(),
                                jsonObject.get("urlName").toString(),
                                jsonObject.get("urlFirstText").toString(),
                                jsonObject.get("textBgColor").toString());
                        urlArrayList.add(urlArrayData);
                    }
                    return urlArrayList;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                return new ArrayList<UrlArray>();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    private String arrayToJson(ArrayList<UrlArray> urlArrayList){
        try{
            JSONArray a = new JSONArray();
            for (int i = 0; i < urlArrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url",urlArrayList.get(i).url);
                jsonObject.put("urlName",urlArrayList.get(i).urlName);
                jsonObject.put("urlFirstText",urlArrayList.get(i).urlFirstText);
                jsonObject.put("textBgColor",urlArrayList.get(i).textBgColor);

                a.put(jsonObject);
            }

            return a.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<UrlArray> getDefaultArray (Context context){
        try{
            ArrayList<UrlArray> urlArrays = new ArrayList<>();
            urlArrays.add(new UrlArray("https://www.youtube.com/",context.getResources().getString(R.string.item_youtube),"Y","#f44336"));
            urlArrays.add(new UrlArray("https://www.naver.com/",context.getResources().getString(R.string.item_naver),"N","#00FF40"));
            urlArrays.add(new UrlArray("https://www.google.com/",context.getResources().getString(R.string.item_google),"G","#3f51b5"));
            return urlArrays;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public boolean getFirstWebView(Context context){
        SharedPreferences firstUser =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
        return firstUser.getBoolean("FirstWebView",false);

    }
    public void setFirstWebView(Context context){

        try{
            SharedPreferences firstUser =  context.getSharedPreferences("WPU",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firstUser.edit();
            editor.putBoolean("FirstWebView",true);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
