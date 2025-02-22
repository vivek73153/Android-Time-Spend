package com.ijoomer.common.classes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TimePicker;

import com.google.android.gms.internal.da;
import com.ijoomer.common.configuration.IjoomerApplicationConfiguration;
import com.ijoomer.common.configuration.IjoomerGlobalConfiguration;
import com.ijoomer.components.intafy.IjoomerIntafyEventAddFirstStepsActivity;
import com.ijoomer.custom.interfaces.CustomClickListner;
import com.ijoomer.custom.interfaces.HttpAccessListener;
import com.ijoomer.custom.interfaces.IjoomerClickListner;
import com.ijoomer.custom.interfaces.IjoomerSharedPreferences;
import com.ijoomer.custom.interfaces.ReportListner;
import com.ijoomer.custom.interfaces.SelectImageDialogListner;
import com.ijoomer.custom.interfaces.ShareListner;
import com.ijoomer.customviews.IjoomerButton;
import com.ijoomer.customviews.IjoomerCheckBox;
import com.ijoomer.customviews.IjoomerDataPickerView;
import com.ijoomer.customviews.IjoomerEditText;
import com.ijoomer.customviews.IjoomerRadioButton;
import com.ijoomer.customviews.IjoomerTextView;
import com.ijoomer.intafy.src.R;
import com.ijoomer.theme.ThemeManager;
import com.smart.framework.AlertMagnatic;
import com.smart.framework.AlertNeutral;
import com.smart.framework.CustomAlertMagnatic;
import com.smart.framework.CustomAlertNeutral;
import com.smart.framework.ItemView;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartListAdapterWithHolder;
import com.smart.framework.SmartListItem;

/**
 * This Class Contains All Method Related To IjoomerUtilities.
 *
 * @author tasol
 *
 */
public class IjoomerUtilities implements IjoomerSharedPreferences {

    public static Activity mSmartAndroidActivity;
    private static ProgressDialog progress = null;
    public static SeekBar skProgress;

    private static Geocoder geocoder;
    private static SmartListAdapterWithHolder contactAdapter;

    private static String progressMsg = "";

    public static int KILOMETER = 0;
    public static int METER = 1;
    public static int MILE = 2;
    public static int DEGREE = 3;
    public static LinkedHashMap<String, Integer> emojisHashMap = new LinkedHashMap<String, Integer>();

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog will be non cancellable by default. User can not
     * dismiss it by pressing back IjoomerButton.
     *
     * @param msg
     *            = String msg to be displayed in progress dialog. screen each
     *            time this method is called.
     */
    public static void showProgressDialog(String msg) {
        progressMsg = msg;

        mSmartAndroidActivity.runOnUiThread(new Runnable() {
            public void run() {
                progress = ProgressDialog.show(mSmartAndroidActivity, "", progressMsg);
            }
        });
    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * IjoomerButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param msg
     *            = String msg to be displayed in progress dialog.
     * @param isCancellable
     *            = is cancellable or not
     */
    public static void showProgressDialog(String msg, final boolean isCancellable) {
        progressMsg = msg;

        mSmartAndroidActivity.runOnUiThread(new Runnable() {
            public void run() {
                progress = ProgressDialog.show(mSmartAndroidActivity, "", progressMsg);
                progress.setCancelable(isCancellable);
            }
        });
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    public static void hideProgressDialog() {
        mSmartAndroidActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (progress.isShowing())
                        progress.dismiss();
                } catch (Throwable e) {

                }
            }
        });
    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message IjoomerButton.<br>
     *
     * @param title
     *            = String title will be the title of OK dialog.
     * @param msg
     *            = String msg will be the message in OK dialog.
     * @param IjoomerButtonCaption
     *            = String IjoomerButtonCaption will be the name of OK
     *            IjoomerButton.
     * @param target
     *            = String target is AlertNewtral callback for OK IjoomerButton
     *            click action.
     */
    public static void getOKDialog(final String title, final String msg, final String IjoomerButtonCaption, final boolean isCancelable,
                                   final AlertNeutral target) {
        if (!msg.equalsIgnoreCase(mSmartAndroidActivity.getString(R.string.code704))) {

            try {
                mSmartAndroidActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mSmartAndroidActivity);

                        int imageResource = android.R.drawable.stat_sys_warning;
                        Drawable image = mSmartAndroidActivity.getResources().getDrawable(imageResource);

                        builder.setTitle(title).setMessage(msg).setIcon(image).setCancelable(false)
                                .setNeutralButton(IjoomerButtonCaption, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        target.NeutralMathod(dialog, id);
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.setCancelable(isCancelable);
                        alert.show();
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method used to get custom ok dialog.
     *
     * @param title
     *            represented dialog title
     * @param msg
     *            represented message
     * @param IjoomerButtonCaption
     *            represented ok button caption
     * @param layoutID
     *            represented layout id
     * @param target
     *            represented {@link CustomAlertMagnatic}
     */
    public static void getCustomOkDialog(final String title, final String msg, final String IjoomerButtonCaption,final int layoutID,final CustomAlertNeutral target) {
        if (!msg.equals(mSmartAndroidActivity.getResources().getString(R.string.code704))) {
            mSmartAndroidActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(ThemeManager.getInstance().getOkDialog());

                    IjoomerTextView txtTitle = (IjoomerTextView) dialog.findViewById(R.id.txtTitle);
                    IjoomerTextView txtMessage = (IjoomerTextView) dialog.findViewById(R.id.txtMessage);
                    txtMessage.setMovementMethod(LinkMovementMethod.getInstance());
                    txtMessage.setText(Html.fromHtml(msg));
                    txtTitle.setText(title);
                    IjoomerButton ok = (IjoomerButton) dialog.findViewById(R.id.btnOk);
                    ok.setText(IjoomerButtonCaption);
                    ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    target.NeutralMethod();
                                    dialog.dismiss();
                                }
                            }, 1000);

                        }
                    });
                    dialog.show();
                }
            });
        }
    }


    /**
     * This method used to get loading dialog.
     *
     * @param message
     *            represented message
     * @return represented {@link SeekBar}
     */
    public static SeekBar getLoadingDialog(final String message) {

        mSmartAndroidActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(ThemeManager.getInstance().getLoadingDialog());
                    final IjoomerTextView txtMessage = (IjoomerTextView) dialog.findViewById(R.id.txtMessage);
                    final IjoomerTextView txtProgrss = (IjoomerTextView) dialog.findViewById(R.id.txtProgrss);
                    skProgress = (SeekBar) dialog.findViewById(R.id.skProgress);
                    txtMessage.setText(message);
                    txtProgrss.setText("0 %");
                    skProgress.setMax(100);
                    skProgress.setProgress(0);
                    skProgress.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View arg0, MotionEvent arg1) {
                            return true;
                        }
                    });
                    skProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            txtProgrss.setText(progress + " %");

                            if (progress >= 100) {
                                final Timer t = new Timer();
                                t.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        t.cancel();
                                        if (dialog != null && dialog.isShowing())
                                            dialog.dismiss();
                                    }
                                }, 10);
                            }
                        }
                    });
                    dialog.show();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });
        synchronized (skProgress) {
            return skProgress;
        }
    }

    /**
     * This method used to get loading dialog.
     *
     * @param message
     *            represented message
     * @param size
     *            represented size
     * @return represented {@link SeekBar}
     */
//    public static SeekBar getLoadingDialog(final String message, final String size) {
//
//        mSmartAndroidActivity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
//                    dialog.setContentView(ThemeManager.getInstance().getLoadingDialog());
//                    final IjoomerTextView txtMessage = (IjoomerTextView) dialog.findViewById(R.id.txtMessage);
//                    final IjoomerTextView txtProgrss = (IjoomerTextView) dialog.findViewById(R.id.txtProgrss);
//                    final IjoomerTextView txtSize = (IjoomerTextView) dialog.findViewById(R.id.txtSize);
//                    txtSize.setVisibility(View.VISIBLE);
//                    txtSize.setText(mSmartAndroidActivity.getString(R.string.size) + " : " + size);
//                    skProgress = (SeekBar) dialog.findViewById(R.id.skProgress);
//
//                    txtMessage.setText(message);
//                    txtProgrss.setText("0 %");
//                    skProgress.setMax(100);
//                    skProgress.setProgress(0);
//                    skProgress.setOnTouchListener(new OnTouchListener() {
//
//                        @Override
//                        public boolean onTouch(View arg0, MotionEvent arg1) {
//                            return true;
//                        }
//                    });
//                    skProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//                        }
//
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                            txtProgrss.setText(progress + " %");
//
//                            if (progress >= 100) {
//                                final Timer t = new Timer();
//                                t.schedule(new TimerTask() {
//
//                                    @Override
//                                    public void run() {
//                                        t.cancel();
//                                        if (dialog != null && dialog.isShowing())
//                                            dialog.dismiss();
//                                    }
//                                }, 10);
//                            }
//                        }
//                    });
//                    dialog.show();
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        synchronized (skProgress) {
//            return skProgress;
//        }
//    }

    /**
     * This method used to get custom confirm dialog.
     *
     * @param title
     *            represented dialog title
     * @param msg
     *            represented message
     * @param okIjoomerButtonCaption
     *            represented ok button caption
     * @param cancelIjoomerButtonCaption
     *            represented cancel button caption
     * @param target
     *            represented {@link CustomAlertMagnatic}
     */
    public static void getCustomConfirmDialog(final String title, final String msg, final String okIjoomerButtonCaption,
                                              final String cancelIjoomerButtonCaption, final CustomAlertMagnatic target) {
        mSmartAndroidActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(ThemeManager.getInstance().getConfirmDialog());

                IjoomerTextView txtTitle = (IjoomerTextView) dialog.findViewById(R.id.txtTitle);
                IjoomerTextView txtMessage = (IjoomerTextView) dialog.findViewById(R.id.txtMessage);
                txtMessage.setText(msg);
                txtTitle.setText(title);
                IjoomerButton ok = (IjoomerButton) dialog.findViewById(R.id.btnOk);
                IjoomerButton btnCancel = (IjoomerButton) dialog.findViewById(R.id.btnCancel);
                ok.setText(okIjoomerButtonCaption);
                btnCancel.setText(cancelIjoomerButtonCaption);

                ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                target.PositiveMethod();
                                dialog.dismiss();
                            }
                        }, 1000);


                    }
                });
                btnCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                target.NegativeMethod();
                                dialog.dismiss();
                            }
                        }, 1000);
                    }
                });
                dialog.show();
            }
        });
    }


    /**
     * This Method check whether Internet connection is available or not.
     *
     * @return <b>true</b> if actual network connection is there, otherwise
     *         <b>false.
     */
    public static boolean isNetwokReachable() {
        final ConnectivityManager connMgr = (ConnectivityManager) mSmartAndroidActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            try {
                return true;
                // URL url = new URL("http://www.google.com");
                // HttpURLConnection urlc = (HttpURLConnection)
                // url.openConnection();
                // urlc.setRequestProperty("User-Agent", "Android Application");
                // urlc.setRequestProperty("Connection", "close");
                // urlc.setConnectTimeout(10 * 1000);
                // urlc.connect();
                // if (urlc.getResponseCode() == 200) {
                // return true;
                // } else {
                // return false;
                // }
            } catch (Throwable e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void finishActivity() {
        if (mSmartAndroidActivity != null)
            mSmartAndroidActivity.finish();
    }

    /**
     * This method used to get multi-selection dialog.
     *
     * @param name
     *            represented dialog title
     * @param jsonString
     *            represented json data
     * @param value
     *            represented value pre-selected
     * @param target
     *            represented {@link CustomClickListner}
     */
    public static void getMultiSelectionDialog(final String name, String jsonString, final String value, final CustomClickListner target) {

        final ArrayList<String> values = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                values.add(((JSONObject) jsonArray.get(i)).getString("value").trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final boolean[] selections = new boolean[values.size()];
        final StringBuilder newValue = new StringBuilder();

        AlertDialog alert = null;

        if (value.length() > 0) {
            String[] oldValue = value.split(",");
            int size = values.size();
            for (int i = 0; i < size; i++) {
                int len = oldValue.length;
                for (int j = 0; j < len; j++) {
                    if (values.get(i).toString().trim().equalsIgnoreCase(oldValue[j].trim())) {
                        selections[i] = true;
                        break;
                    }
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mSmartAndroidActivity);
        builder.setTitle(name);
        builder.setMultiChoiceItems(values.toArray(new CharSequence[values.size()]), selections,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selections[which] = isChecked;
                    }
                });

        builder.setPositiveButton(mSmartAndroidActivity.getString(R.string.intafy_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int size = selections.length;
                for (int i = 0; i < size; i++) {
                    if (selections[i]) {
                        newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
                    }
                }
                target.onClick(newValue.toString());

            }
        });
        builder.setNegativeButton(mSmartAndroidActivity.getString(R.string.intafy_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                target.onClick(value);
            }
        });
        alert = builder.create();
        alert.show();

    }

    public static void getMultiSelectionDialogSobipro(final String name, String jsonString, final String value, final String id,
                                                      final IjoomerClickListner target) {

        final ArrayList<String> values = new ArrayList<String>();
        final ArrayList<String> ids = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                values.add(((JSONObject) jsonArray.get(i)).getString("name").trim());
                ids.add(((JSONObject) jsonArray.get(i)).getString("value").trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final boolean[] selections = new boolean[values.size()];
        final StringBuilder newValue = new StringBuilder();
        final StringBuilder newIds = new StringBuilder();

        AlertDialog alert = null;

        if (value.length() > 0) {
            String[] oldValue = value.split(",");
            int size = values.size();
            for (int i = 0; i < size; i++) {
                int len = oldValue.length;
                for (int j = 0; j < len; j++) {
                    if (values.get(i).toString().trim().equalsIgnoreCase(oldValue[j].trim())) {
                        selections[i] = true;
                        break;
                    }
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mSmartAndroidActivity);
        builder.setTitle(name);
        builder.setMultiChoiceItems(values.toArray(new CharSequence[values.size()]), selections,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selections[which] = isChecked;
                    }
                });

        builder.setPositiveButton(mSmartAndroidActivity.getString(R.string.intafy_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int size = selections.length;
                for (int i = 0; i < size; i++) {
                    if (selections[i]) {
                        newValue.append(newValue.length() > 0 ? "," + values.get(i) : values.get(i));
                        newIds.append(newIds.length() > 0 ? "," + ids.get(i) : ids.get(i));
                    }
                }
                target.onClick(newValue.toString(), newIds.toString());

            }
        });
        builder.setNegativeButton(mSmartAndroidActivity.getString(R.string.intafy_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                target.onClick(value, id);
            }
        });
        alert = builder.create();
        alert.show();

    }

    /**
     * This method used to get date dialog.
     *
     * @param strDate
     *            represented date
     * @param restrict
     *            represented isRestrict
     * @param target
     *            represented {@link CustomClickListner}
     */
    public static void getDateDialog(final String strDate, boolean restrict, final CustomClickListner target) {

        Calendar date = getDateFromString(strDate);
        Calendar today = Calendar.getInstance();

        if (restrict && date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date.add(Calendar.YEAR, -18);
        }

        IjoomerDataPickerView dateDlg = new IjoomerDataPickerView(mSmartAndroidActivity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Time chosenDate = new Time();
                chosenDate.set(dayOfMonth, monthOfYear, year);
                long dt = chosenDate.toMillis(true);
                CharSequence strDate = DateFormat.format(IjoomerApplicationConfiguration.dateFormat, dt);
                target.onClick(strDate.toString());
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

        dateDlg.show();

    }

    public static String convertDateStringFormat(String strDate, String fromFormat, String toFormat){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormat2 = new SimpleDateFormat(toFormat.trim());
            return dateFormat2.format(sdf.parse(strDate));
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * This method used to get date-time dialog.
     *
     * @param strDate
     *            represented date-time
     * @param target
     *            represented {@link CustomClickListner}
     */
    public static void getDateTimeDialog(final String strDate, final CustomClickListner target) {
        final Calendar date = getDateFromString(strDate);
        DatePickerDialog dateDialog = new DatePickerDialog(mSmartAndroidActivity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final int y = year;
                final int m = monthOfYear;
                final int d = dayOfMonth;

                new TimePickerDialog(mSmartAndroidActivity, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Time chosenDate = new Time();
                        chosenDate.set(0, minute, hourOfDay, d, m, y);
                        long dt = chosenDate.toMillis(true);
                        CharSequence strDate = DateFormat.format(IjoomerApplicationConfiguration.dateTimeFormat, dt);
                        target.onClick(strDate.toString());
                    }
                }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true).show();

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));

        dateDialog.show();

    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime
     *            represented time
     * @param target
     *            represented {@link CustomClickListner}
     */
    public static void getTimeDialog(final String strTime, final CustomClickListner target) {

        Calendar date = getTimeFromString(strTime);
        TimePickerDialog timeDialog = new TimePickerDialog(mSmartAndroidActivity, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar date = Calendar.getInstance();
                Time chosenDate = new Time();
                chosenDate.set(0, minute, hourOfDay, date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
                long dt = chosenDate.toMillis(true);
                CharSequence strDate = DateFormat.format(IjoomerApplicationConfiguration.timeFormat, dt);
                target.onClick(strDate.toString());
            }
        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

        timeDialog.show();

    }

    /**
     * This method used to get option spinner adapter.
     *
     * @param field
     *            represented {@link HashMap} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getSpinnerAdapter(JSONObject field) {

        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();

        try {

            JSONArray jsonArray = new JSONArray(field.getString("options"));
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject options = (JSONObject) jsonArray.get(i);

                if (options.has("title")) {
                    values.add(options.getString("title"));
                } else if (options.has("name")) {
                    values.add(options.getString("name").replace("&rsaquo;", " > "));
                } else if (options.has("caption")) {
                    values.add(options.getString("caption"));
                } else {
                    values.add(options.getString("value"));
                }
                if (options.getString("value").equals(field.getString("value")) || options.has("title")
                        && options.getString("title").equals(field.getString("value")) || options.has("name")
                        && options.getString("name").equals(field.getString("value"))) {
                    index = i;
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        final MyCustomAdapter adpater = new MyCustomAdapter(mSmartAndroidActivity, values);
        adpater.setDefaultPostion(index);
        return adpater;

    }

    /**
     * This method used to get privacy spinner adapter
     *
     * @param field
     *            represented {@link HashMap} data
     * @return represented {@link MyCustomAdapter}
     */
    public static MyCustomAdapter getPrivacySpinnerAdapter(HashMap<String, String> field) {

        int index = 0;
        final ArrayList<String> values = new ArrayList<String>();
        JSONArray jsonArray;

        try {
            jsonArray = new JSONObject(field.get("privacy")).getJSONArray("options");
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject options = (JSONObject) jsonArray.get(i);

                if (options.has("title")) {
                    values.add(options.getString("title"));
                } else if (options.has("name")) {
                    values.add(options.getString("name").replace("&rsaquo;", " > "));
                } else if (options.has("caption")) {
                    values.add(options.getString("caption"));
                } else {
                    values.add(options.getString("value"));
                }

                if (options.getString("value").equals(new JSONObject(field.get("privacy")).getString("value"))) {
                    index = i;
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        MyCustomAdapter adpater = new MyCustomAdapter(mSmartAndroidActivity, values);
        adpater.setDefaultPostion(index);
        return adpater;

    }

    /**
     * Custom adapter
     *
     * @author tasol
     *
     */
    public static class MyCustomAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> list;
        private int defaultPosition;

        public int getDefaultPosition() {
            return defaultPosition;
        }

        public MyCustomAdapter(Context context, ArrayList<String> objects) {
            super(context, 0, objects);
            this.context = context;
            list = objects;
        }

        public void setDefaultPostion(int position) {
            this.defaultPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustom(position, convertView, parent);
        }

        public View getCustom(int position, View convertView, ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(android.R.id.text1);
            label.setTextColor(context.getResources().getColor(R.color.white));
            label.setText(list.get(position));

            return row;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(R.layout.ijoomer_spinner_item, parent, false);
            IjoomerTextView label = (IjoomerTextView) row.findViewById(R.id.text1);
            label.setText(list.get(position));

            return row;
        }
    }

    /**
     * This method used to email validator.
     *
     * @param mailAddress
     *            represented email
     * @return represented {@link Boolean}
     */
    public static boolean emailValidator(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();

    }

    /**
     * This method used to birth date validator.
     *
     * @param birthDate
     *            represented birth date
     * @return represented {@link Boolean}
     */
    public static boolean birthdateValidator(String birthDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(IjoomerApplicationConfiguration.dateFormat);
        try {
            Date date = dateFormat.parse(birthDate);
            Calendar bdate = Calendar.getInstance();
            bdate.setTime(date);
            Calendar today = Calendar.getInstance();

            if (bdate.compareTo(today) == 1) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method used to get date from string.
     *
     * @param strDate
     *            represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(IjoomerApplicationConfiguration.dateFormat);
        Calendar calnder = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to get time from string.
     *
     * @param strTime
     *            represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(IjoomerApplicationConfiguration.timeFormat);
        Date date;
        Calendar calnder = Calendar.getInstance();
        try {
            date = dateFormat.parse(strTime);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to show select image selection dialog.
     *
     * @param target
     *            represented {@link SelectImageDialogListner}
     */
    public static void selectImageDialog(final SelectImageDialogListner target) {
        final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.ijoomer_select_image_dialog);
        final IjoomerTextView txtCapture = (IjoomerTextView) dialog.findViewById(R.id.txtCapture);
        final IjoomerTextView txtPhoneGallery = (IjoomerTextView) dialog.findViewById(R.id.txtPhoneGallery);
        txtCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onCapture();
                dialog.dismiss();

            }
        });
        txtPhoneGallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onPhoneGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * This method used to indexing(a-z) list data.
     *
     * @param oldData
     *            represented list
     * @param indexOn
     *            represented index on
     * @return represented {@link HashMap} list
     */
    public static ArrayList<HashMap<String, String>> getListIndexedData(ArrayList<HashMap<String, String>> oldData, String indexOn) {

        ArrayList<HashMap<String, String>> newData = new ArrayList<HashMap<String, String>>();

        String idx1 = null;
        String idx2 = null;

        for (HashMap<String, String> data : oldData) {
            idx1 = (data.get(indexOn).substring(0, 1)).toLowerCase();
            if (!idx1.equalsIgnoreCase(idx2)) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("listindexheader", idx1.toUpperCase());
                newData.add(map);
                idx2 = idx1;
            }
            newData.add(data);
        }
        return newData;
    }

    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address
     *            represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(String address) {
        if (address != null && address.length() > 0) {
            geocoder = new Geocoder(mSmartAndroidActivity);

            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(address, 1);
                return list.get(0);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat
     *            represented latitude (0-for current latitude)
     * @param lng
     *            represented longitude (0-for current longitude)
     * @return represented {@link Address}
     */
    public static Address getAddressFromLatLong(double lat, double lng) {

        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(((SmartActivity) mSmartAndroidActivity).getLatitude());
            lng = Double.parseDouble(((SmartActivity) mSmartAndroidActivity).getLongitude());
        }
        geocoder = new Geocoder(mSmartAndroidActivity);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
            return list.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat
     *            represented latitude (0-for current latitude)
     * @param lng
     *            represented longitude (0-for current longitude)
     * @return represented {@link Address} list
     */
    public static List<Address> getAddressListFromLatLong(double lat, double lng) {

        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(((SmartActivity) mSmartAndroidActivity).getLatitude());
            lng = Double.parseDouble(((SmartActivity) mSmartAndroidActivity).getLongitude());
        }
        geocoder = new Geocoder(mSmartAndroidActivity);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This method used to get contact list from device.
     *
     * @return represented {@link ArrayList}
     */
    @SuppressWarnings("serial")
    public static ArrayList<HashMap<String, Object>> getContacts() {

        ArrayList<HashMap<String, Object>> contacts = new ArrayList<HashMap<String, Object>>();
        final String[] projection = new String[] { RawContacts.CONTACT_ID, RawContacts.DELETED };

        @SuppressWarnings("deprecation")
        final Cursor rawContacts = mSmartAndroidActivity.managedQuery(RawContacts.CONTENT_URI, projection, null, null, null);

        final int contactIdColumnIndex = rawContacts.getColumnIndex(RawContacts.CONTACT_ID);
        final int deletedColumnIndex = rawContacts.getColumnIndex(RawContacts.DELETED);

        if (rawContacts.moveToFirst()) {
            while (!rawContacts.isAfterLast()) {
                final int contactId = rawContacts.getInt(contactIdColumnIndex);
                final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);

                if (!deleted) {
                    HashMap<String, Object> contactInfo = new HashMap<String, Object>() {
                        {
                            put("contactId", "");
                            put("name", "");
                            put("email", "");
                            put("address", "");
                            put("photo", "");
                            put("phone", "");
                        }
                    };
                    contactInfo.put("contactId", "" + contactId);
                    contactInfo.put("name", getName(contactId));
                    contactInfo.put("email", getEmail(contactId));
                    contactInfo.put("photo", getPhoto(contactId) != null ? getPhoto(contactId) : "");
                    contactInfo.put("address", getAddress(contactId));
                    contactInfo.put("phone", getPhoneNumber(contactId));
                    contactInfo.put("isChecked", "false");
                    contacts.add(contactInfo);
                }
                rawContacts.moveToNext();
            }
        }

        rawContacts.close();

        return contacts;
    }

    /**
     * This method used to get name from contact id.
     *
     * @param contactId
     *            represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    private static String getName(int contactId) {
        String name = "";
        final String[] projection = new String[] { Contacts.DISPLAY_NAME };

        final Cursor contact = mSmartAndroidActivity.managedQuery(Contacts.CONTENT_URI, projection, Contacts._ID + "=?",
                new String[] { String.valueOf(contactId) }, null);

        if (contact.moveToFirst()) {
            name = contact.getString(contact.getColumnIndex(Contacts.DISPLAY_NAME));
            contact.close();
        }
        contact.close();
        return name;

    }

    /**
     * This method used to get mail id from contact id.
     *
     * @param contactId
     *            represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    private static String getEmail(int contactId) {
        String emailStr = "";
        final String[] projection = new String[] { Email.DATA, // use
                // Email.ADDRESS
                // for API-Level
                // 11+
                Email.TYPE };

        final Cursor email = mSmartAndroidActivity.managedQuery(Email.CONTENT_URI, projection, Data.CONTACT_ID + "=?",
                new String[] { String.valueOf(contactId) }, null);

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);

            while (!email.isAfterLast()) {
                emailStr = emailStr + email.getString(contactEmailColumnIndex) + ";";
                email.moveToNext();
            }
        }
        email.close();
        return emailStr;

    }

    /**
     * This method used to get {@link Bitmap} From contact id.
     *
     * @param contactId
     *            represented contact id
     * @return represented {@link Bitmap}
     */
    @SuppressWarnings("deprecation")
    private static Bitmap getPhoto(int contactId) {
        Bitmap photo = null;
        final String[] projection = new String[] { Contacts.PHOTO_ID };

        final Cursor contact = mSmartAndroidActivity.managedQuery(Contacts.CONTENT_URI, projection, Contacts._ID + "=?",
                new String[] { String.valueOf(contactId) }, null);

        if (contact.moveToFirst()) {
            final String photoId = contact.getString(contact.getColumnIndex(Contacts.PHOTO_ID));
            if (photoId != null) {
                photo = getBitmap(photoId);
            } else {
                photo = null;
            }
        }
        contact.close();

        return photo;
    }

    /**
     * This method used to get {@link Bitmap} From photo id.
     *
     * @param photoId
     *            represented photo id
     * @return represented {@link Bitmap}
     */
    @SuppressWarnings("deprecation")
    private static Bitmap getBitmap(String photoId) {
        final Cursor photo = mSmartAndroidActivity.managedQuery(Data.CONTENT_URI, new String[] { Photo.PHOTO }, Data._ID + "=?",
                new String[] { photoId }, null);

        final Bitmap photoBitmap;
        if (photo.moveToFirst()) {
            byte[] photoBlob = photo.getBlob(photo.getColumnIndex(Photo.PHOTO));
            photoBitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
        } else {
            photoBitmap = null;
        }
        photo.close();
        return photoBitmap;
    }

    /**
     * This method used to get address from contact id.
     *
     * @param contactId
     *            represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    private static String getAddress(int contactId) {
        String postalData = "";
        String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] addrWhereParams = new String[] { String.valueOf(contactId),
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };

        Cursor addrCur = mSmartAndroidActivity.managedQuery(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);

        if (addrCur.moveToFirst()) {
            postalData = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        }
        addrCur.close();
        return postalData;
    }

    /**
     * This method used to get phone number from contact id.
     *
     * @param contactId
     *            represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    private static String getPhoneNumber(int contactId) {

        String phoneNumber = "";
        final String[] projection = new String[] { Phone.NUMBER, Phone.TYPE, };
        final Cursor phone = mSmartAndroidActivity.managedQuery(Phone.CONTENT_URI, projection, Data.CONTACT_ID + "=?",
                new String[] { String.valueOf(contactId) }, null);

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(Phone.DATA);

            while (!phone.isAfterLast()) {
                phoneNumber = phoneNumber + phone.getString(contactNumberColumnIndex) + ";";
                phone.moveToNext();
            }

        }
        phone.close();
        return phoneNumber;
    }

    /**
     * This method used to resize {@link TextView}.
     *
     * @param tv
     *            represented {@link TextView}
     * @param maxLine
     *            represented max line
     * @param expandText
     *            represented expand text
     */
    public static void IjoomerTextViewResizable(final IjoomerTextView tv, final int maxLine, final String expandText) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine <= 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartIjoomerTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText),
                            BufferType.SPANNABLE);
                } else if (tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartIjoomerTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText),
                            BufferType.SPANNABLE);
                }
            }
        });

    }

    /**
     * This method used to add clickable part on {@link TextView}.
     *
     * @param strSpanned
     *            represented {@link Spanned} string
     * @param tv
     *            represented {@link TextView}
     * @param maxLine
     *            represented max line
     * @param expandText
     *            represented expand text
     * @return represented {@link SpannableStringBuilder}
     */
    private static SpannableStringBuilder addClickablePartIjoomerTextViewResizable(final Spanned strSpanned, final IjoomerTextView tv,
                                                                                   final int maxLine, final String expandText) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(expandText)) {
            ssb.setSpan(new IjoomerSpannable(Color.parseColor(mSmartAndroidActivity.getString(R.color.blue)), true) {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), BufferType.SPANNABLE);
                    tv.invalidate();
                }

            }, str.indexOf(expandText), str.indexOf(expandText) + expandText.length(), 0);

        }
        return ssb;

    }

    /**
     * This method used to get date in string from time zone.
     *
     * @param timestamp
     *            represented {@link Long} time stamp
     * @return represented {@link String}
     */
    public static String getDateStringCurrentTimeZone(long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat(IjoomerApplicationConfiguration.dateTimeFormat);
        String dateString = sdf.format(timestamp);
        return dateString;
    }

    /**
     * This method used to get current date from time zone.
     *
     * @param timestamp
     *            represented {@link Long} time stamp
     * @return represented {@link Date}
     */
    public static Date getDateCurrentTimeZone(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone(IjoomerGlobalConfiguration.getServerTimeZone());

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));

        return (Date) calendar.getTime();
    }

    /**
     * This method used to get milliseconds from time zone.
     *
     * @param timestamp
     *            represented {@link Long} time stamp
     * @return represented {@link Long}
     */
    public static long getMillisecondsTimeZone(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone(IjoomerGlobalConfiguration.getServerTimeZone());

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));
        System.out.println("Date : " + calendar.getTime());
        return calendar.getTimeInMillis();
    }

    /**
     * This method used to get difference from minute.
     *
     * @param miliseconds
     *            represented {@link Long} milliseconds
     * @return represented {@link Long}
     */
    public static long getDfferenceInMinute(long miliseconds) {
        long diff = (Calendar.getInstance().getTimeInMillis() - miliseconds);
        diff = diff / 60000L;
        return Math.abs(diff);
    }

    /**
     * This method used to calculate times ago from milliseconds.
     *
     * @param miliseconds
     *            represented {@link Long} milliseconds
     * @return represented {@link String}
     */
    public static String calculateTimesAgo(long miliseconds) {
        Date start = new Date(miliseconds);
        Date end = new Date();

        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

        long diff[] = new long[] { 0, 0, 0, 0 };
		/* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		/* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		/* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		/* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));

        System.out.println(String.format("%d day%s, %d hour%s, %d minute%s, %d second%s ago", diff[0], diff[0] > 1 ? "s" : "", diff[1],
                diff[1] > 1 ? "s" : "", diff[2], diff[2] > 1 ? "s" : "", diff[3], diff[3] > 1 ? "s" : ""));

        if (diff[0] > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);

            if (c.getMaximum(Calendar.DATE) <= diff[0]) {
                return (String) DateFormat.format(IjoomerApplicationConfiguration.dateFormat, start);
            } else {
                return diff[0] > 1 ? String.format("%d days ago", diff[0]) : String.format("%d day ago", diff[0]);
            }
        } else if (diff[1] > 0) {
            return diff[1] > 1 ? String.format("%d hours ago", diff[1]) : String.format("%d hour ago", diff[1]);
        } else if (diff[2] > 0) {
            return diff[2] > 1 ? String.format("%d minutes ago", diff[2]) : String.format("%d minute ago", diff[2]);
        } else if (diff[3] > 0) {
            return diff[3] > 1 ? String.format("%d seconds ago", diff[3]) : String.format("%d second ago", diff[3]);
        } else {
            return (String) DateFormat.format(IjoomerApplicationConfiguration.dateFormat, start);
        }

    }

    /**
     * This method used to auto login user params.
     *
     * @return represented {@link JSONObject}
     */
    public static JSONObject getLoginParams() {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject(((SmartActivity) mSmartAndroidActivity).getSmartApplication().readSharedPreferences()
                    .getString(SP_LOGIN_REQ_OBJECT, ""));
            JSONObject taskData = loginParams.getJSONObject("taskData");
            taskData.put("lat", ((SmartActivity) mSmartAndroidActivity).getLatitude());
            taskData.put("long", ((SmartActivity) mSmartAndroidActivity).getLongitude());
            String udid = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_GCM_REGID, "");
            if (udid.length() > 0) {
                taskData.put("devicetoken", udid);
            }
        } catch (Exception e) {
        }
        return loginParams;
    }

    /**
     * This method used to prepare mail body for sharing.
     *
     * @param parms
     *            represented params array
     * @return represented {@link String}
     */
    public static String prepareEmailBody(String... parms) {

        String data = "";
        if (parms != null && parms.length > 0) {
            for (String row : parms) {
                if (row.contains("http") || row.contains("https")) {
                    row = "<a href='" + row + "'> " + row + " </a>";
                }
                data += "<br/> <br/>" + row;
            }
        }

        return data;
    }

    /**
     * This method used to get readable file size from {@link Long} size.
     *
     * @param size
     *            represented long size
     * @return represented {@link String}
     */
    public static String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    /**
     * This method used to add custom notification.
     *
     * @param ticker
     *            represented notification ticker
     * @param title
     *            represented notification title
     * @param message
     *            represented notification message
     */
    @SuppressWarnings("deprecation")
    public static void addToNotificationBar(String ticker, String title, String message) {
        long when = System.currentTimeMillis();
        int icon = R.drawable.ijoomer_push_notification_icon;

        NotificationManager notificationManager = (NotificationManager) mSmartAndroidActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(mSmartAndroidActivity, 0, new Intent(), 0);
        Notification notification = new Notification(icon, ticker, when);
        notification.setLatestEventInfo(mSmartAndroidActivity, title, message, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    /**
     * This method used to get custom ok and cancel dialog.
     *
     * @param title
     *            represented dialog title
     * @param msg
     *            represented custom message
     * @param IjoomerButtonCaption
     *            represented ok button caption
     * @param IjoomerButtonCancelCaption
     *            represented cancel button caption
     * @param layoutID
     *            represented custom layout id
     * @param target
     *            represented {@link CustomAlertMagnatic}
     */
    public static void getCustomOkCancelDialog(final String title, final String msg, final String IjoomerButtonCaption,
                                               final String IjoomerButtonCancelCaption, final int layoutID, final CustomAlertNeutral target) {
        if (!msg.equals(mSmartAndroidActivity.getResources().getString(R.string.code704))) {
            mSmartAndroidActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    final Dialog dialog = new Dialog(mSmartAndroidActivity, android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(layoutID);

                    IjoomerTextView txtTitle = (IjoomerTextView) dialog.findViewById(R.id.txtTitle);
                    IjoomerTextView txtMessage = (IjoomerTextView) dialog.findViewById(R.id.txtMessage);
                    txtMessage.setMovementMethod(LinkMovementMethod.getInstance());
                    txtMessage.setText(Html.fromHtml(msg));
                    txtTitle.setText(title);
                    IjoomerButton ok = (IjoomerButton) dialog.findViewById(R.id.btnOk);
                    ok.setText(IjoomerButtonCaption);
                    ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            target.NeutralMethod();
                            dialog.dismiss();
                        }
                    });
                    IjoomerButton cancel = (IjoomerButton) dialog.findViewById(R.id.btnCancel);
                    cancel.setText(IjoomerButtonCancelCaption);
                    cancel.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    public static double convertDistance(String valueToConvert, int fromUnit, int toUnit) {

        double dvalueToConvert = Double.parseDouble(valueToConvert);
        if (dvalueToConvert > 0) {
            if (fromUnit == IjoomerUtilities.KILOMETER) {
                dvalueToConvert = dvalueToConvert * 1000d;
            } else if (fromUnit == IjoomerUtilities.MILE) {
                dvalueToConvert = dvalueToConvert * 1609.34d;
            } else if (fromUnit == IjoomerUtilities.DEGREE) {
                dvalueToConvert = (Math.acos(dvalueToConvert) * 6371) * 1000d;
            }

            if (toUnit == IjoomerUtilities.KILOMETER) {
                dvalueToConvert = dvalueToConvert / 1000d;
            } else if (toUnit == IjoomerUtilities.MILE) {
                dvalueToConvert = dvalueToConvert / 1609.34d;
            } else if (fromUnit == IjoomerUtilities.DEGREE) {
                dvalueToConvert = Math.acos(((dvalueToConvert / 1000d) / 6371d));
            }

            String s = String.format(String.format("%.2f", dvalueToConvert));
            return Double.parseDouble(s);
        }
        return 0;
    }

    public static class RichTextUtils {
        public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(Spanned original, Class<A> sourceType,
                                                                                                SpanConverter<A, B> converter) {
            SpannableStringBuilder result = new SpannableStringBuilder(original);
            A[] spans = result.getSpans(0, result.length(), sourceType);

            for (A span : spans) {
                int start = result.getSpanStart(span);
                int end = result.getSpanEnd(span);
                int flags = result.getSpanFlags(span);

                result.removeSpan(span);
                result.setSpan(converter.convert(span), start, end, flags);
            }

            return (result);
        }

        public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle> {
            B convert(A span);
        }
    }

    public static class URLSpanConverter implements RichTextUtils.SpanConverter<URLSpan, CustumClicableSpan> {

        private SpnebleListener local;

        public URLSpanConverter() {
            super();
        }

        public URLSpanConverter(SpnebleListener target) {
            this();
            this.local = target;
        }

        @Override
        public CustumClicableSpan convert(URLSpan span) {
            return new CustumClicableSpan(span.getURL(), local);
        }
    }

    private static class CustumClicableSpan extends ClickableSpan {
        private String url = "";
        private SpnebleListener local;

        public CustumClicableSpan(String url, SpnebleListener target) {
            super();
            this.url = url;
            this.local = target;
        }

        @Override
        public void onClick(View widget) {
            System.out.println("Url :" + url);
            if (local == null) {
                Intent intent = new Intent(mSmartAndroidActivity, IjoomerWebviewClient.class);
                intent.putExtra("url", url);
                mSmartAndroidActivity.startActivity(intent);
            } else {
                local.onUrlClick(url);
            }

        }

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public interface SpnebleListener {
        void onUrlClick(String url);
    }

    public static LinkedHashMap<String, Integer> getEmojisHashMap() {
        if (emojisHashMap == null || emojisHashMap.size() == 0) {
            emojisHashMap = new LinkedHashMap<String, Integer>();
            return emojisHashMap;
        } else {
            return emojisHashMap;
        }

    }

    public static  String applyCss(String content){
        return  "<head> <style> a {color:#5ac8fa;}  body {color:white;background-color:#40474d;} </style> </head>"+content;
    }

    public static long getTimeStampFormatedFromDate(String strDate,String formate) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(formate);
            return sdf.parse(strDate).getTime();
        }catch (Exception e) {
        }
        return 0;
    }

}
