package com.ijoomer.components.intafy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.androidquery.AQuery;
import com.ijoomer.common.classes.IjoomerIntafyMaster;
import com.ijoomer.common.classes.IjoomerUtilities;
import com.ijoomer.common.configuration.IjoomerApplicationConfiguration;
import com.ijoomer.cropimage.CropImage;
import com.ijoomer.custom.interfaces.CustomClickListner;
import com.ijoomer.custom.interfaces.SelectImageDialogListner;
import com.ijoomer.customviews.IjoomerEditText;
import com.ijoomer.customviews.IjoomerTextView;
import com.ijoomer.customviews.InternalStorageContentProvider;
import com.ijoomer.customviews.RoundedImageView;
import com.ijoomer.intafy.src.R;
import com.ijoomer.library.intafy.IntafyEventDataProvider;
import com.ijoomer.weservice.WebCallListener;
import com.smart.framework.CustomAlertNeutral;
import com.smart.framework.SmartApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Class Contains All Method Related To IjoomerHomeActivity.
 *
 * @author tasol
 *
 */
public class IjoomerIntafyEventAddFirstStepsActivity extends IjoomerIntafyMaster implements IntafyTagHolder {

    private IjoomerEditText edtEventTitle;
    private IjoomerEditText edtEventLocation;
    private IjoomerEditText edtEventDate;
    private IjoomerEditText edtEventStartTime;
    private IjoomerEditText edtEventEndTime;
    private IjoomerEditText edtEventDescription;
    private ProgressBar pbrImageLoad;
    private RoundedImageView imgEventAvatar;
    private ImageView imgAddImage;
    private ImageView imgNext;
    private String selectedImagePathEventAvatar="";
    final private int PICK_IMAGE_USER_AVATAR = 1;
    final private int CAPTURE_IMAGE_USER_AVATAR = 2;
    private final int CROPED_IMAGE_USER_AVATAR = 3;
    public final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private File mFileTemp;
    private IntafyEventDataProvider eventDataProvider;
    private HashMap<String,String> eventDetails;
    private String IN_EVENT_ID;
    private AQuery aQuery;
    private LinearLayout lnrHeaderLeft;
    private IjoomerTextView headerLeftText;
    private IjoomerTextView headerText;
    Uri mImageCaptureUri;
    /**
     * Overrides methods
     */


    @Override
    public int setLayoutId() {
        return R.layout.ijoomer_intafy_event_add_first_step;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public int setHeaderLayoutId() {
        return R.layout.ijoomer_intafy_header;
    }

    @Override
    public int setFooterLayoutId() {
        return 0;
    }

    @Override
    public void initComponents() {

        edtEventTitle = (IjoomerEditText)findViewById(R.id.edtEventTitle);
        edtEventLocation = (IjoomerEditText)findViewById(R.id.edtEventLocation);
        edtEventDate = (IjoomerEditText)findViewById(R.id.edtEventDate);
        edtEventStartTime = (IjoomerEditText)findViewById(R.id.edtEventStartTime);
        edtEventEndTime = (IjoomerEditText)findViewById(R.id.edtEventEndTime);
        edtEventDescription = (IjoomerEditText)findViewById(R.id.edtEventDescription);
        pbrImageLoad = (ProgressBar)findViewById(R.id.pbrImageLoad);
        imgEventAvatar = (RoundedImageView)findViewById(R.id.imgEventAvatar);
        imgAddImage = (ImageView)findViewById(R.id.imgAddImage);
        imgNext = (ImageView)findViewById(R.id.imgNext);
        lnrHeaderLeft = (LinearLayout) getHeaderView().findViewById(R.id.lnrHeaderLeft);
        headerLeftText = (IjoomerTextView) getHeaderView().findViewById(R.id.txtFirst);
        headerText = (IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader);

        eventDataProvider = new IntafyEventDataProvider(this);
        aQuery = new AQuery(this);

    }

    @Override
    public void prepareViews() {
        IN_EVENT_ID = getIntent().getStringExtra("IN_EVENT_ID")!=null ?getIntent().getStringExtra("IN_EVENT_ID"):"0";
        if(!IN_EVENT_ID.equals("0")){
            String connectedUserId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_CONNECTEDUSERID,"0");
            String networkId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_NETWORKID,"0");
            final SeekBar seekBar =  IjoomerUtilities.getLoadingDialog(getString(R.string.intafy_loading_sending_request));
            eventDataProvider.getEventDetails(IN_EVENT_ID, networkId, connectedUserId, new WebCallListener() {
                @Override
                public void onCallComplete(int responseCode, String errorMessage, ArrayList<HashMap<String, String>> data1, Object data2) {
                    if(responseCode==200){
                        eventDetails = (HashMap<String,String>)data2;
                        setEventDetails();
                    }else{
                        responseErrorMessageHandler(responseCode);
                    }
                }

                @Override
                public void onProgressUpdate(int progressCount) {
                    seekBar.setProgress(progressCount);
                }
            });
            headerText.setText(getString(R.string.intafy_edit_event));
        }else{
            pbrImageLoad.setVisibility(View.GONE);
            headerText.setText(getString(R.string.intafy_add_event));
            imgEventAvatar.setImageResource(R.drawable.ijoomer_background);
        }
        headerLeftText.setText(getString(R.string.intafy_back));

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        }
        else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    private void setEventDetails(){
        edtEventTitle.setText(eventDetails.get(TITLE));
        edtEventLocation.setText(eventDetails.get(LOCATION));
        edtEventDescription.setText(Html.fromHtml(eventDetails.get(DESCRIPTION)));
        edtEventDate.setText(IjoomerUtilities.convertDateStringFormat(eventDetails.get(DATE), "yyyy-MM-dd", IjoomerApplicationConfiguration.dateFormat));
        edtEventStartTime.setText(eventDetails.get(STARTTIME));
        edtEventEndTime.setText(eventDetails.get(ENDTIME));
        if(eventDetails.get(AVATAR).trim().length()>0){
            aQuery.id(imgEventAvatar).progress(pbrImageLoad).image(eventDetails.get(AVATAR),true,true,150,R.drawable.ic_launcher);
        }else{
            pbrImageLoad.setVisibility(View.GONE);
            imgEventAvatar.setImageResource(R.drawable.ijoomer_background);
        }
    }

    private void responseErrorMessageHandler(final int responseCode) {
        IjoomerUtilities.getCustomOkDialog(getString(R.string.intafy_event_details), getString(getResources().getIdentifier("code" + responseCode, "string", getPackageName())), getString(R.string.intafy_ok), R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

            @Override
            public void NeutralMethod() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(IjoomerApplicationConfiguration.isReloadRequired()){
            finish();
        }
    }

    @Override
    public void setActionListeners() {

        edtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IjoomerUtilities.getDateDialog(edtEventDate.getText().toString(),false,new CustomClickListner() {
                    @Override
                    public void onClick(String value) {
                        edtEventDate.setText(value);
                    }
                });
            }
        });

        edtEventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IjoomerUtilities.getDateTimeDialog(edtEventStartTime.getText().toString(),new CustomClickListner() {
                    @Override
                    public void onClick(String value) {
                        edtEventStartTime.setText(value);
                    }
                });
            }
        });
        edtEventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IjoomerUtilities.getDateTimeDialog(edtEventEndTime.getText().toString(),new CustomClickListner() {
                    @Override
                    public void onClick(String value) {
                        edtEventEndTime.setText(value);
                    }
                });
            }
        });
        lnrHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerLeftText.setTextColor(getResources().getColor(R.color.blue));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        headerLeftText.setTextColor(getResources().getColor(R.color.white));
                        finish();
                    }
                }, 1000);
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgNext.setImageResource(getIcon(NEXTICON,true));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        imgNext.setImageResource(getIcon(NEXTICON,false));
                        boolean isValidate =true;

                        if(edtEventTitle.getText().toString().trim().length()<=0){
                            edtEventTitle.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(edtEventLocation.getText().toString().trim().length()<=0){
                            edtEventLocation.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(edtEventDate.getText().toString().trim().length()<=0){
                            edtEventDate.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(edtEventStartTime.getText().toString().trim().length()<=0){
                            edtEventStartTime.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(edtEventEndTime.getText().toString().trim().length()<=0){
                            edtEventEndTime.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(edtEventDescription.getText().toString().trim().length()<=0){
                            edtEventDescription.setError(getString(R.string.intafy_validation_value_required));
                            isValidate=false;
                        }
                        if(isValidate) {
                            String avatar;
                            if(eventDetails!=null && selectedImagePathEventAvatar.trim().length()<=0){
                                avatar = eventDetails.get(AVATAR);
                            }else{
                                avatar = selectedImagePathEventAvatar;
                            }
                            try{
                                loadNew(IjoomerIntafyEventAddSecondStepsActivity.class,IjoomerIntafyEventAddFirstStepsActivity.this,false,"IN_EVENT_ID",IN_EVENT_ID,"IN_EVENT_TITLE",edtEventTitle.getText().toString(),"IN_EVENT_LOCATION",edtEventLocation.getText().toString(),"IN_EVENT_STARTDATE",IjoomerUtilities.convertDateStringFormat(edtEventDate.getText().toString(), IjoomerApplicationConfiguration.dateFormat, "yyyy-MM-dd")+" "+edtEventStartTime.getText().toString(),"IN_EVENT_ENDDATE",IjoomerUtilities.convertDateStringFormat(edtEventDate.getText().toString(), IjoomerApplicationConfiguration.dateFormat, "yyyy-MM-dd")+" "+edtEventEndTime.getText().toString(),"IN_EVENT_AVATAR",avatar,"IN_EVENT_DESCRIPTIONS",edtEventDescription.getText().toString(),"IN_EVENT_TYPE",eventDetails!=null ?eventDetails.get("type"):"","IN_EVENT_USERS",eventDetails!=null?eventDetails.get("memberIds"):"","IN_EVENT_CIRCLE",eventDetails!=null?eventDetails.get("groupIds"):"");
                            }catch (Throwable e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1000);
            }
        });
        imgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgAddImage.setImageResource(R.drawable.plus_icon_en_selected);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        imgAddImage.setImageResource(R.drawable.plus_icon_en);
                        selectPhotoPopup();
                    }
                }, 1000);
            }
        });
    }


    private void selectPhotoPopup(){
        final PopupWindow popup = new PopupWindow(this);
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.ijoomer_intafy_profile_change_photo_popup, viewGroup);

        final IjoomerTextView txtTakePhoto = (IjoomerTextView) layout.findViewById(R.id.txtTakePhoto);
        final IjoomerTextView txtUploadPhoto = (IjoomerTextView) layout.findViewById(R.id.txtUploadPhoto);
        final IjoomerTextView txtCancel = (IjoomerTextView) layout.findViewById(R.id.txtCancel);

        txtTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTakePhoto.setTextColor(getResources().getColor(R.color.blue));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtTakePhoto.setTextColor(getResources().getColor(R.color.white));
                        try{
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                mImageCaptureUri = Uri.fromFile(mFileTemp);
                            }
                            else {
                                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
                            }
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, CAPTURE_IMAGE_USER_AVATAR);
                            popup.dismiss();
                        } catch (Throwable e) {
                            popup.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            }
        });

        txtUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUploadPhoto.setTextColor(getResources().getColor(R.color.blue));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtUploadPhoto.setTextColor(getResources().getColor(R.color.white));
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE_USER_AVATAR);
                        txtUploadPhoto.setTextColor(getResources().getColor(R.color.white));
                        popup.dismiss();
                    }
                }, 1000);
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCancel.setTextColor(getResources().getColor(R.color.blue));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtUploadPhoto.setTextColor(getResources().getColor(R.color.white));
                        popup.dismiss();
                    }
                }, 1000);
            }
        });

        popup.setContentView(layout);
        popup.setWidth(getDeviceWidth() - convertSizeToDeviceDependent(20));
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable(getResources()));
        popup.showAtLocation(layout, Gravity.BOTTOM, 0,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_USER_AVATAR) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                startCropImage(mFileTemp);
            } else if (requestCode == CAPTURE_IMAGE_USER_AVATAR) {
                startCropImage(mFileTemp);
            }  else if(requestCode == CROPED_IMAGE_USER_AVATAR){
                selectedImagePathEventAvatar = data.getStringExtra(CropImage.IMAGE_PATH);
                if (selectedImagePathEventAvatar == null) {
                    return;
                }
                imgEventAvatar.setImageBitmap(BitmapFactory.decodeFile(selectedImagePathEventAvatar));
            }else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }else{
            if(IN_EVENT_ID.equals("0")  && selectedImagePathEventAvatar==null || selectedImagePathEventAvatar.length()<=0){
                imgEventAvatar.setImageResource(R.drawable.ijoomer_background);
            }else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    private void startCropImage(final File file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap b = decodeFileFromPath(file.getPath());
                try {
                    ExifInterface ei = new ExifInterface(file.getPath());
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    Matrix matrix = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                            break;
                        default:
                            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                            break;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                FileOutputStream out1 = null;
                try {
                    if (mFileTemp.exists())
                        mFileTemp.delete();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
                    } else {
                        mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
                    }
                    out1 = new FileOutputStream(mFileTemp);
                    b.compress(Bitmap.CompressFormat.JPEG, 90, out1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out1.close();
                    } catch (Throwable ignore) {

                    }
                }


                Intent intent = new Intent(IjoomerIntafyEventAddFirstStepsActivity.this, CropImage.class);
                intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
                intent.putExtra(CropImage.SCALE, true);

                intent.putExtra(CropImage.ASPECT_X, 1);
                intent.putExtra(CropImage.ASPECT_Y, 1);
                intent.putExtra(CropImage.OUTPUT_X, 150);
                intent.putExtra(CropImage.OUTPUT_Y, 150);

                startActivityForResult(intent, CROPED_IMAGE_USER_AVATAR);
            }
        });

    }

    private Bitmap decodeFileFromPath(String path){
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            if (o.outHeight > 1024 || o.outWidth > 1024) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(1024 / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }


    public void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}
