package com.ijoomer.tips;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by tasol on 19/12/14.
 */
public class NotificationAlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificationServices.class);
        startWakefulService(context, service);
    }


    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger to next day if set alram after 8 a.m.
        if(calendar.get(Calendar.HOUR_OF_DAY)>8){
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        // Set the alarm's trigger time to 8:00 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, NotificationBroadCastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, NotificationBroadCastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }
}
