package kr.Thinkingcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class CheckPermission {

    public static void checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context) && context !=null){
                Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,uri);
                context.startActivity(intent);
            }
        }
    }
}
