package com.ijoomer.tips;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ijoomer.tips.customviews.CustomBoldTextView;
import com.ijoomer.tips.database.DataBaseProvider;
import com.ijoomer.tips.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;


public class TipsCollectionActivity extends BaseActivity {

    private View view;
    private LinearLayout lnrBottomMenu;
    private LinearLayout container;
    public ProgressBar pbrLoading;
    private RadioGroup rdgTab;
    private RadioButton rdbSuiteOne;

    private ArrayList<HashMap<String,String>> suiteList;

    private DataBaseProvider dataBaseProvider;

    private DriversListFragment driversListFragment;
    private TipsListFragment tipsListFragment;
    private TipsDetailFragment tipsDetailFragment;

    private int currentTab=1;
    private int currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.tipscollection,content);
        lnrBottomMenu = (LinearLayout) view.findViewById(R.id.lnrBottomMenu);
        container = (LinearLayout) view.findViewById(R.id.container);
        pbrLoading = (ProgressBar) view.findViewById(R.id.pbrLoading);
        rdgTab = (RadioGroup) view.findViewById(R.id.rdgTab);
        rdbSuiteOne = (RadioButton) view.findViewById(R.id.rdbSuiteOne);


        CustomBoldTextView txtHeader = (CustomBoldTextView) findViewById(R.id.txtHeader);
        txtHeader.setText(Html.fromHtml(getString(R.string.tips_collection)));

        dataBaseProvider = new DataBaseProvider(this);
        getSuiteList();

        imgSlideBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSlideMenu.setVisibility(View.VISIBLE);
        imgSlideBack.setVisibility(View.GONE);


        rdgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdbSuiteTwo:
                        setTabTwo();
                        break;
                    case R.id.rdbSuiteThree:
                        setTabThree();
                        break;
                    case R.id.rdbSuiteFour:
                        setTabFour();
                        break;
                    default:
                        setTabOne();
                        break;
                }
            }
        });
    }

    private void setTabOne(){
        currentFragment=1;

        driversListFragment = new DriversListFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID, Integer.parseInt(suiteList.get(0).get(Utility.ID)));
        bundle.putString(Utility.SUITENAME, suiteList.get(0).get(Utility.NAME));
        driversListFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(currentTab>1){
            ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        }else{
            ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
        }
        currentTab=1;
        ft.replace(container.getId(),driversListFragment);
        ft.commit();

        imgSlideMenu.setVisibility(View.VISIBLE);
        imgSlideBack.setVisibility(View.GONE);

    }

    public LinearLayout getMainLayout(){
        return main;
    }

    private void setTabTwo(){
        currentFragment=1;


        driversListFragment = new DriversListFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID, Integer.parseInt(suiteList.get(1).get(Utility.ID)));
        bundle.putString(Utility.SUITENAME, suiteList.get(1).get(Utility.NAME));
        driversListFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(currentTab>2){
            ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        }else{
            ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
        }
        currentTab=2;
        ft.replace(container.getId(),driversListFragment);
        ft.commit();

        imgSlideMenu.setVisibility(View.VISIBLE);
        imgSlideBack.setVisibility(View.GONE);
    }

    private void setTabThree(){
        currentFragment=1;


        driversListFragment = new DriversListFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID, Integer.parseInt(suiteList.get(2).get(Utility.ID)));
        bundle.putString(Utility.SUITENAME, suiteList.get(2).get(Utility.NAME));
        driversListFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(currentTab>3){
            ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        }else{
            ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
        }
        currentTab=3;
        ft.replace(container.getId(),driversListFragment);
        ft.commit();

        imgSlideMenu.setVisibility(View.VISIBLE);
        imgSlideBack.setVisibility(View.GONE);

    }

    private void setTabFour(){
        currentFragment=1;

        driversListFragment = new DriversListFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID, Integer.parseInt(suiteList.get(3).get(Utility.ID)));
        bundle.putString(Utility.SUITENAME, suiteList.get(3).get(Utility.NAME));
        driversListFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(currentTab>4){
            ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        }else{
            ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
        }
        currentTab=4;
        ft.replace(container.getId(),driversListFragment);
        ft.commit();

        imgSlideMenu.setVisibility(View.VISIBLE);
        imgSlideBack.setVisibility(View.GONE);

    }

    public void getSuiteList(){
        showProgressBar();
        new AsyncTask<Void,Void,ArrayList<HashMap<String,String>>>(){
            @Override
            protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
                ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String, String>>();
                dataBaseProvider.open();
                try {
                    data = dataBaseProvider.getSuite();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(ArrayList<HashMap<String,String>> response) {
                super.onPostExecute(response);
                suiteList = response;
                dataBaseProvider.close();

                currentTab=2;
                rdbSuiteOne.setChecked(true);
            }
        }.execute();
    }


    public void gotoTips(int suiteId,int driverId,String suiteName,String driverName){
        tipsListFragment = new TipsListFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID,suiteId);
        bundle.putString(Utility.SUITENAME,suiteName);
        bundle.putInt(Utility.DRIVERID,driverId);
        bundle.putString(Utility.DRIVERNAME,driverName);
        tipsListFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        ft.replace(container.getId(),tipsListFragment);
        ft.commit();

        currentFragment++;

        imgSlideMenu.setVisibility(View.GONE);
        imgSlideBack.setVisibility(View.VISIBLE);
    }

    public void gotoTipDetail(int suiteId,int driverId,String suiteName,String driverName,int tipId,String tipName,String tipNote,int favourite,String questionOne,String questionTwo,String questionThree){
        tipsDetailFragment = new TipsDetailFragment();
        Bundle bundle =new Bundle();
        bundle.putInt(Utility.SUITEID,suiteId);
        bundle.putString(Utility.SUITENAME,suiteName);
        bundle.putInt(Utility.DRIVERID,driverId);
        bundle.putString(Utility.DRIVERNAME,driverName);
        bundle.putInt(Utility.TIPID,tipId);
        bundle.putString(Utility.TIPNAME,tipName);
        bundle.putString(Utility.TIPNOTE,tipNote);
        bundle.putInt(Utility.TIPFAVOURITE,favourite);
        bundle.putString(Utility.TIPQUESTIONONE,questionOne);
        bundle.putString(Utility.TIPQUESTIONTWO,questionTwo);
        bundle.putString(Utility.TIPQUESTIONTHREE,questionThree);

        tipsDetailFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
        ft.replace(container.getId(),tipsDetailFragment);
        ft.commit();

        currentFragment++;
        lnrBottomMenu.setVisibility(View.GONE);

        CustomBoldTextView txtHeader = (CustomBoldTextView) findViewById(R.id.txtHeader);
        txtHeader.setText(tipName);
        txtHeader.setTextColor(getResources().getColor(R.color.grey));

        imgSlideMenu.setVisibility(View.GONE);
        imgSlideBack.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        pbrLoading.setVisibility(View.GONE);
    }
    public void showProgressBar(){
        pbrLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment==3){

            if(tipsDetailFragment.sharePopupWindow.isShowing()){
                tipsDetailFragment.sharePopupWindow.dismiss();
            }else if(tipsDetailFragment.successPopupWindow.isShowing()){
                tipsDetailFragment.successPopupWindow.dismiss();
                if(tipsDetailFragment.isSharePopUpHide){
                    tipsDetailFragment.isSharePopUpHide=false;
                    tipsDetailFragment.sharePopupWindow.showAtLocation(tipsDetailFragment.imgShare, Gravity.NO_GRAVITY, 0, 0);
                }
            }else {
                CustomBoldTextView txtHeader = (CustomBoldTextView) findViewById(R.id.txtHeader);
                txtHeader.setText(Html.fromHtml(getString(R.string.tips_collection)));

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
                ft.replace(container.getId(), tipsListFragment);
                ft.commit();

                currentFragment--;
                lnrBottomMenu.setVisibility(View.VISIBLE);
            }

        }else if(currentFragment==2){

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
            ft.replace(container.getId(),driversListFragment);
            ft.commit();

            currentFragment--;

            imgSlideMenu.setVisibility(View.VISIBLE);
            imgSlideBack.setVisibility(View.GONE);

        }else{
            finish();
        }
    }
}
