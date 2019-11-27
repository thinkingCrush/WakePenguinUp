package kr.Thinkingcrush.WakePenguinUp.Data;

public class UrlArray {
    //url - "https://??????"
    //urlName - "????"
    //urlFirstText - "?"
    //TextBgColor - "#??????"

    public String url ;
    public String urlName;
    public String urlFirstText;
    public String textBgColor;

    public UrlArray(String url, String urlName , String urlFirstText , String textBgColor){
        this.url = url;
        this.urlName = urlName;
        this.urlFirstText  = urlFirstText;
        this.textBgColor = textBgColor;
    }
}
