package com.ijoomer.tips;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.ToggleButton;

import com.ijoomer.tips.customviews.CustomBoldTextView;
import com.ijoomer.tips.customviews.CustomTextView;
import com.ijoomer.tips.utilities.Utility;


public class MySettingsActivity extends BaseActivity {

    private View view;
    private Button btnSave;
    private CustomTextView txtNotificationOnOff;
    private ToggleButton toggleNotification;
    private NotificationAlarmReceiver alarm = new NotificationAlarmReceiver();

    private PopupWindow successPopupWindow;
    private View successPopupView;
    private CustomTextView txtSuccessPopupClose;
    private CustomTextView txtSuccessPopupMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.mysettings,content);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        toggleNotification = (ToggleButton) view.findViewById(R.id.toggleNotification);
        txtNotificationOnOff = (CustomTextView) view.findViewById(R.id.txtNotificationOnOff);


        CustomBoldTextView txtHeader = (CustomBoldTextView) findViewById(R.id.txtHeader);
        txtHeader.setText(Html.fromHtml(getString(R.string.my_settings)));

        if(getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE).getBoolean(Utility.SHAREDPREFERENCES_MYSETTING,false)){
            toggleNotification.setChecked(true);
            txtNotificationOnOff.setText(getString(R.string.notification_on));
        }else{
            toggleNotification.setChecked(false);
            txtNotificationOnOff.setText(getString(R.string.notification_off));
        }

        toggleNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtNotificationOnOff.setText(getString(R.string.notification_on));
                }else{
                    txtNotificationOnOff.setText(getString(R.string.notification_off));
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.app_name),MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Utility.SHAREDPREFERENCES_MYSETTING,toggleNotification.isChecked());
                editor.commit();
                if(toggleNotification.isChecked()){
                    alarm.cancelAlarm(MySettingsActivity.this);
                    alarm.setAlarm(MySettingsActivity.this);
                }else{
                    alarm.cancelAlarm(MySettingsActivity.this);
                }
                successPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);

            }
        });

        successPopupView = LayoutInflater.from(this).inflate(R.layout.success_popup,null);
        txtSuccessPopupClose = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupClose);
        txtSuccessPopupMessage = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupMessage);
        txtSuccessPopupMessage.setText(getString(R.string.your_settings_updated));
        successPopupWindow = new PopupWindow(successPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        successPopupWindow.setAnimationStyle(R.style.anim_bottom_in_out_style);

        txtSuccessPopupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successPopupWindow.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(successPopupWindow.isShowing()){
            successPopupWindow.dismiss();
        }else{
            super.onBackPressed();
        }
    }
}
