package com.ijoomer.tips;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.ijoomer.tips.customviews.CustomBoldTextView;
import com.ijoomer.tips.customviews.CustomEditTex;
import com.ijoomer.tips.customviews.CustomTextView;
import com.ijoomer.tips.utilities.Utility;
import com.ijoomer.tips.webservices.WebServiceListener;
import com.ijoomer.tips.webservices.WebServicesProvider;


public class VTREBookActivity extends BaseActivity {

    private View view;
    private Button btnSubmit;
    private Button btnContactUs;
    private CustomEditTex edtEmailAddress;
    private ProgressBar pbrLoading;

    private PopupWindow successPopupWindow;
    private View successPopupView;
    private CustomTextView txtSuccessPopupClose;
    private CustomTextView txtSuccessPopupMessage;
    private CustomBoldTextView txtSuccessPopupTitle;
    private WebServicesProvider servicesProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.vtrebook,content);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnContactUs = (Button) view.findViewById(R.id.btnContactUs);
        edtEmailAddress = (CustomEditTex) view.findViewById(R.id.edtEmailAddress);
        pbrLoading = (ProgressBar) view.findViewById(R.id.pbrLoading);

        servicesProvider = new WebServicesProvider(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean isValid=true;
                if(edtEmailAddress.getText().toString().trim().length()<=0){
                    edtEmailAddress.setError(getResources().getString(R.string.value_required));
                    isValid=false;
                }else if(!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.getText().toString()).matches()){
                    edtEmailAddress.setError(getResources().getString(R.string.invalid_email));
                    isValid=false;
                }
                if(isValid){
                    pbrLoading.setVisibility(View.VISIBLE);
                    servicesProvider.vtrEBook(edtEmailAddress.getText().toString().trim(),new WebServiceListener() {
                        @Override
                        public void onComplete(int code) {
                            pbrLoading.setVisibility(View.GONE);
                            if(code==200 || code == 500) {
                                edtEmailAddress.setText("");
                                txtSuccessPopupTitle.setText(getResources().getString(R.string.thank_you));
                                txtSuccessPopupMessage.setText(getResources().getString(R.string.ebook_email_submission));
                            }else{
                                txtSuccessPopupTitle.setText(getResources().getString(R.string.error));
                                txtSuccessPopupMessage.setText(getResources().getString(R.string.no_internet_connection));
                            }
                            successPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                        }
                    });
                }
            }
        });
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VTREBookActivity.this,ContactUsActivity.class);
                intent.putExtra("FromVTR",1);
                startActivity(intent);
            }
        });

        CustomBoldTextView txtHeader = (CustomBoldTextView) findViewById(R.id.txtHeader);
        txtHeader.setText(Html.fromHtml(getString(R.string.vtr_ebook)));

        successPopupView = LayoutInflater.from(this).inflate(R.layout.success_popup,null);
        txtSuccessPopupClose = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupClose);
        txtSuccessPopupTitle = (CustomBoldTextView) successPopupView.findViewById(R.id.txtSuccessPopupTitle);
        txtSuccessPopupMessage = (CustomTextView) successPopupView.findViewById(R.id.txtSuccessPopupMessage);

        successPopupWindow = new PopupWindow(successPopupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        successPopupWindow.setAnimationStyle(R.style.anim_bottom_in_out_style);

        txtSuccessPopupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successPopupWindow.dismiss();
            }
        });

        imgSlideBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSlideMenu.setVisibility(View.GONE);
        imgSlideBack.setVisibility(View.VISIBLE);


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
