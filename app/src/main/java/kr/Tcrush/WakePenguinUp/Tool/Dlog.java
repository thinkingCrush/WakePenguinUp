package kr.Tcrush.WakePenguinUp.Tool;

import android.util.Log;

public class Dlog {
    static final String TAG = "InfoCar";


    /** Log Level Error **/
    public static final void e(String message) {
        if (BaseApplication.DEBUG) Log.e(TAG, buildLogMsg(message));
    }
    /** Log Level Warning **/
    public static final void w(String message) {
        if (BaseApplication.DEBUG)Log.w(TAG, buildLogMsg(message));
    }
    /** Log Level Information **/
    public static final void i(String message) {
        if (BaseApplication.DEBUG)Log.i(TAG, buildLogMsg(message));
    }
    /** Log Level Debug **/
    public static final void d(String message) {
        if (BaseApplication.DEBUG)Log.d(TAG, buildLogMsg(message));
    }
    /** Log Level Verbose **/
    public static final void v(String message) {
        if (BaseApplication.DEBUG)Log.v(TAG, buildLogMsg(message));
    }


    public static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[")
                .append(ste.getMethodName())
                .append("()")
                .append("]")
                .append(" :: ")
                .append(message)
                .append(" (")
                .append(ste.getFileName())
                .append(":")
                .append(ste.getLineNumber())
                .append(")");

        return sb.toString();

    }
}