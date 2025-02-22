package com.ijoomer.tips;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.ijoomer.tips.customviews.CustomBoldTextView;
import com.ijoomer.tips.customviews.CustomTextView;
import com.ijoomer.tips.database.DataBaseProvider;
import com.ijoomer.tips.flipanimation.AnimationFactory;
import com.ijoomer.tips.utilities.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class TipsDetailFragment extends Fragment {

    private View view;
    private CustomBoldTextView txtSuiteNameFront;
    private CustomBoldTextView txtSuiteNameBack;
    private CustomBoldTextView txtDriverTipName;
    private CustomTextView txtTip;
    private CustomTextView txtQuestionOne;
    private CustomTextView txtQuestionTwo;
    private CustomTextView txtQuestionThree;
    private ProgressBar pbrLoading;
    private ViewFlipper viewFlipper;
    private LinearLayout lnrCardFront;
    private LinearLayout lnrCardBack;
    private ImageView imgSuggest;
    public ImageView imgShare;
    private ImageView imgFavourite;
    private ImageView imgSuiteIconFront;
    private ImageView imgSuiteIconBack;

    private int suiteId;
    private int driverId;
    private int tipId;
    private String suiteName;
    private String driverName;
    private String tipName;
    private String tipNote;
    private int tipFavourite;
    private String tipQuestionOne;
    private String tipQuestionTwo;
    private String tipQuestionThree;

    private DataBaseProvider dataBaseProvider;

    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    private int flipCount;

    public PopupWindow sharePopupWindow;
    private View sharePopupView;
    private LinearLayout shareUpperLayout;
    private ImageView imgShareFacebook;
    private ImageView imgShareEmail;
    private ImageView imgShareTwitter;
    private ImageView imgShareInstagram;
    private ImageView imgSharePinterest;
    private ImageView imgShareLinkdin;

    public PopupWindow successPopupWindow;
    private View successPopupView;
    private CustomTextView txtSuccessPopupClose;
    private CustomTextView txtSuccessPopupMessage;
    private CustomBoldTextView txtSuccessPopupTitle;
    public boolean isSharePopUpHide;

    private File image;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inspireme,null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));
        txtSuiteNameFront = (CustomBoldTextView) view.findViewById(R.id.txtSuiteNameFront);
        txtSuiteNameBack = (CustomBoldTextView) view.findViewById(R.id.txtSuiteNameBack);
        txtDriverTipName = (CustomBoldTextView) view.findViewById(R.id.txtDriverTipName);
        txtTip = (CustomTextView) view.findViewById(R.id.txtTip);
        txtQuestionOne = (CustomTextView) view.findViewById(R.id.txtQuestionOne);
        txtQuestionTwo = (CustomTextView) view.findViewById(R.id.txtQuestionTwo);
        txtQuestionThree = (CustomTextView) view.findViewById(R.id.txtQuestionThree);
        pbrLoading = (ProgressBar) view.findViewById(R.id.pbrLoading);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        lnrCardFront = (LinearLayout) view.findViewById(R.id.lnrCardFront);
        lnrCardBack = (LinearLayout) view.findViewById(R.id.lnrCardBack);
        imgSuggest = (ImageView) view.findViewById(R.id.imgSuggest);
        imgShare = (ImageView) view.findViewById(R.id.imgShare);
        imgFavourite = (ImageView) view.findViewById(R.id.imgFavourite);
        imgSuiteIconFront = (ImageView) view.findViewById(R.id.imgSuiteIconFront);
        imgSuiteIconBack = (ImageView) view.findViewById(R.id.imgSuiteIconBack);

        txtTip.setMovementMethod(new ScrollingMovementMethod());
        txtQuestionOne.setMovementMethod(new ScrollingMovementMethod());
        txtQuestionTwo.setMovementMethod(new ScrollingMovementMethod());
        txtQuestionThree.setMovementMethod(new ScrollingMovementMethod());

        dataBaseProvider = new DataBaseProvider(getActivity());

        suiteId = getArguments().getInt(Utility.SUITEID);
        driverId = getArguments().getInt(Utility.DRIVERID);
        tipId = getArguments().getInt(Utility.TIPID);
        suiteName = getArguments().getString(Utility.SUITENAME);
        driverName = getArguments().getString(Utility.DRIVERNAME);
        tipName = getArguments().getString(Utility.TIPNAME);
        tipNote = getArguments().getString(Utility.TIPNOTE);
        tipFavourite = getArguments().getInt(Utility.TIPFAVOURITE);
        tipQuestionOne = getArguments().getString(Utility.TIPQUESTIONONE);
        tipQuestionTwo = getArguments().getString(Utility.TIPQUESTIONTWO);
        tipQuestionThree = getArguments().getString(Utility.TIPQUESTIONTHREE);

        setTip(suiteId,suiteName,driverName,tipName,tipNote);

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });


        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
            }
        });

        if(tipFavourite == 1){
            imgFavourite.setImageResource(R.drawable.favourited_selector);
        }else{
            imgFavourite.setImageResource(R.drawable.favourit_selector);
        }

        imgFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSuccessPopupTitle.setText(getResources().getString(R.string.success));
                int favourite = tipFavourite == 1 ? 0 : 1;
                if(favourite==1){
                    txtSuccessPopupMessage.setText(getResources().getString(R.string.tip_added_favourite));
                }else{
                    txtSuccessPopupMessage.setText(getResources().getString(R.string.tip_remove_favourite));
                }
                dataBaseProvider.updateFavourite(tipId,favourite);
                tipFavourite = favourite;
                if(tipFavourite == 1){
                    imgFavourite.setImageResource(R.drawable.favourited_selector);
                }else{
                    imgFavourite.setImageResource(R.drawable.favourit_selector);
                }
                successPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
            }
        });

        imgSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SuggestTipActivity.class);
                startActivity(intent);
            }
        });


        sharePopupView = LayoutInflater.from(getActivity()).inflate(R.layout.share_options_popup,null);
        shareUpperLayout = (LinearLayout) sharePopupView.findViewById(R.id.shareUpperLayout);

        imgShareFacebook = (ImageView) sharePopupView.findViewById(R.id.imgShareFacebook);
        imgShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstall=false;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(image));
                PackageManager pm = v.getContext().getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                for (ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).toLowerCase().contains("facebook.composer.shareintent")){
                        isAppInstall=true;
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                        break;
                    }
                }
                showAppNotInstalledPopup(isAppInstall);
            }
        });
        imgShareEmail = (ImageView) sharePopupView.findViewById(R.id.imgShareEmail);
        imgShareEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(image));
                Uri data = Uri.parse("mailto:?subject="+getResources().getString(R.string.app_name)+" "+getResources().getString(R.string.tip)+"&body=&to=");
                emailIntent.setData(data);
                startActivity(emailIntent);
            }
        });
        imgShareInstagram = (ImageView) sharePopupView.findViewById(R.id.imgShareInstagram);
        imgShareInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstall=false;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                PackageManager pm = v.getContext().getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                for (ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).toLowerCase().contains("instagram")){
                        isAppInstall=true;
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                        break;
                    }
                }
                showAppNotInstalledPopup(isAppInstall);
            }
        });
        imgShareLinkdin = (ImageView) sharePopupView.findViewById(R.id.imgShareLinkdin);
        imgShareLinkdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstall=false;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                PackageManager pm = v.getContext().getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                for (ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).toLowerCase().contains("linkedin")){
                        isAppInstall=true;
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                        break;
                    }
                }
                showAppNotInstalledPopup(isAppInstall);
            }
        });
        imgSharePinterest = (ImageView) sharePopupView.findViewById(R.id.imgSharePinterest);
        imgSharePinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstall=false;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                PackageManager pm = v.getContext().getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                for (ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).toLowerCase().contains("pinterest")){
                        isAppInstall=true;
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                        break;
                    }
                }
                showAppNotInstalledPopup(isAppInstall);
            }
        });
        imgShareTwitter = (ImageView) sharePopupView.findViewById(R.id.imgShareTwitter);
        imgShareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstall=false;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                PackageManager pm = v.getContext().getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                for (ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).toLowerCase().contains("twitter")){
                        isAppInstall=true;
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        shareIntent.setComponent(name);
                        startActivity(shareIntent);
                        break;
                    }
                }
                showAppNotInstalledPopup(isAppInstall);
            }
        });
        shareUpperLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePopupWindow.dismiss();
            }
        });
        sharePopupWindow = new PopupWindow(sharePopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        sharePopupWindow.setAnimationStyle(R.style.anim_bottom_in_out_style);

        successPopupView = LayoutInflater.from(getActivity()).inflate(R.layout.success_popup,null);
        txtSuccessPopupTitle = (CustomBoldTextView) successPopupView.findViewById(R.id.txtSuccessPopupTitle);
        txtSuccessPopupClose = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupClose);
        txtSuccessPopupMessage = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupMessage);
        successPopupWindow = new PopupWindow(successPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        successPopupWindow.setAnimationStyle(R.style.anim_bottom_in_out_style);

        txtSuccessPopupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successPopupWindow.dismiss();
                if(isSharePopUpHide){
                    isSharePopUpHide=false;
                    sharePopupWindow.showAtLocation(imgShare, Gravity.NO_GRAVITY, 0, 0);
                }
            }
        });

        return view;
    }


    public void showAppNotInstalledPopup(boolean available){
        if(!available) {
            if (sharePopupWindow.isShowing()) {
                sharePopupWindow.dismiss();
            }
            isSharePopUpHide = true;
            txtSuccessPopupTitle.setText(getResources().getString(R.string.app_not_installed));
            txtSuccessPopupMessage.setText(getResources().getString(R.string.no_such_app_found_for_sharing));
            successPopupWindow.showAtLocation(imgShare, Gravity.NO_GRAVITY, 0, 0);
        }
    }


    private void setTip(int suiteId,String suiteName,String driverName,String tipName,String tipNote){
        if(suiteId == 1){
            txtSuiteNameFront.setTextColor(getResources().getColor(R.color.pink));
            txtSuiteNameBack.setTextColor(getResources().getColor(R.color.pink));
            imgSuiteIconFront.setImageResource(R.drawable.suite_one_corner);
            imgSuiteIconBack.setImageResource(R.drawable.suite_one_corner);
            txtDriverTipName.setText(Html.fromHtml("<font color='#ee2b7b'>" + driverName + " / </font> <br/>" + "<font color='#60605b'>" + tipName + "</font>"));
        }else if(suiteId == 2){
            txtSuiteNameFront.setTextColor(getResources().getColor(R.color.blue));
            txtSuiteNameBack.setTextColor(getResources().getColor(R.color.blue));
            imgSuiteIconFront.setImageResource(R.drawable.suite_two_corner);
            imgSuiteIconBack.setImageResource(R.drawable.suite_two_corner);
            txtDriverTipName.setText(Html.fromHtml("<font color='#009bdf'>" + driverName + " / </font> <br/>" + "<font color='#60605b'>" + tipName + "</font>"));
        }else if(suiteId == 3){
            txtSuiteNameFront.setTextColor(getResources().getColor(R.color.green));
            txtSuiteNameBack.setTextColor(getResources().getColor(R.color.green));
            imgSuiteIconFront.setImageResource(R.drawable.suite_three_corner);
            imgSuiteIconBack.setImageResource(R.drawable.suite_three_corner);
            txtDriverTipName.setText(Html.fromHtml("<font color='#3daf2c'>" + driverName + " / </font> <br/>" + "<font color='#60605b'>" + tipName + "</font>"));
        }else{
            txtSuiteNameFront.setTextColor(getResources().getColor(R.color.orange));
            txtSuiteNameBack.setTextColor(getResources().getColor(R.color.orange));
            imgSuiteIconFront.setImageResource(R.drawable.suite_four_corner);
            imgSuiteIconBack.setImageResource(R.drawable.suite_four_corner);
            txtDriverTipName.setText(Html.fromHtml("<font color='#f7931e'>" + driverName + " / </font> <br/>" + "<font color='#60605b'>" + tipName + "</font>"));
        }

        txtSuiteNameFront.setText(suiteName);
        txtSuiteNameBack.setText(suiteName);
        txtTip.setText(tipNote);

        txtQuestionOne.setText(tipQuestionOne!=null && tipQuestionOne.trim().length()>0 ? tipQuestionOne :"Question number 1 relating to this tip?");
        txtQuestionTwo.setText(tipQuestionTwo!=null && tipQuestionTwo.trim().length()>0 ? tipQuestionTwo :"Question number 2 relating to this tip?");
        txtQuestionThree.setText(tipQuestionThree!=null && tipQuestionThree.trim().length()>0 ? tipQuestionThree :"Question number 3 relating to this tip?");

        ((TipsCollectionActivity)getActivity()).getMainLayout().post(new Runnable() {
            @Override
            public void run() {
                captureScreen();
            }
        });


    }

    private void captureScreen(){

        // create bitmap screen capture
        ((TipsCollectionActivity)getActivity()).getMainLayout().setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(((TipsCollectionActivity)getActivity()).getMainLayout().getDrawingCache());
        ((TipsCollectionActivity)getActivity()).getMainLayout().setDrawingCacheEnabled(false);

        image = new File(Environment.getExternalStorageDirectory().toString(), "share.jpg");

        OutputStream fout = null;
        try {
            fout = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
            fout.flush();
            fout.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(flipCount == 0){
                        AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.RIGHT_LEFT);
                        flipCount++;
                    }else{
                        AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        flipCount--;
                    }
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(flipCount == 0){
                        AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        flipCount++;
                    }else{
                        AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.RIGHT_LEFT);
                        flipCount--;
                    }
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}
