//package com.example.matchinggame;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//
//import java.io.Serializable;
//import java.util.Date;
//
//public class closeTwoAlarm {
//    public static void Start(Context context, int secs, int pos1, int pos2) {
//        Date when = new Date(System.currentTimeMillis());
//
////        Bundle extras = new Bundle();
////        extras.putSerializable("firstItem", (Serializable) firstItem);
////        extras.putSerializable("secondItem", (Serializable) secondItem);
//
//        Intent intent = new Intent(context, MyReceiver.class);
////        intent.setAction("CLOSE_TWO_CARDS");
////        intent.putExtras(extras);
//
////
////        intent.putExtra("firstItem", (Serializable) firstItem);
////        intent.putExtra("secondItem", (Serializable) secondItem);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarm.setExact(AlarmManager.RTC_WAKEUP,when.getTime() + secs * 1000, pendingIntent) ;
//    }
//
//    public static void Stop(Context context)
//    {
//        Intent intent = new Intent(context, MyReceiver.class);
//        intent.setAction("DISPLAY_QUOTE");
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarm =
//                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pendingIntent);
//    }
//}
