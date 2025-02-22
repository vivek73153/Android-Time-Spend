package com.eosos.components.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;

import com.androidquery.AQuery;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.src.R;
import com.smart.framework.CustomAlertNeutral;

public class EososSearchResultActivity extends EososMasterActivity {
    private IjoomerTextView txtList;
    private IjoomerTextView  txtMap;
    public ImageView imgMapType;
	private EososMapFragment mapFragment;
	private EososSearchResultListFragment lstFragment;
    private AQuery aQuery;
    private ArrayList<HashMap<String, String>> IN_FIELD;


	@Override
	public int setLayoutId() {
		return R.layout.eosos_near_by_entry;
	}

	@Override
	public void initComponents() {
        txtList = (IjoomerTextView) findViewById(R.id.txtList);
        txtMap = (IjoomerTextView) findViewById(R.id.txtMap);
        imgMapType = (ImageView) findViewById(R.id.imgMapType);
        aQuery = new AQuery(this);
	}

	@Override
	public void prepareViews() {
		lstFragment = new EososSearchResultListFragment();
        ((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.planner));
        ((IjoomerTextView)getHeaderView().findViewById(R.id.txtBack)).setVisibility(View.VISIBLE);
        ((ImageView)getHeaderView().findViewById(R.id.imgHome)).setVisibility(View.GONE);
        txtList.setTextColor(getResources().getColor(R.color.red));
		addFragment(R.id.lnrFragment, lstFragment);
        getIntentData();
	}

	@Override
	public void setActionListeners() {
        txtMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(!IjoomerUtilities.isNetwokReachable()){
                    IjoomerUtilities.getCustomOkDialog(getString(R.string.planner), getString(R.string.code599), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
                            new CustomAlertNeutral() {

                                @Override
                                public void NeutralMethod() {
                                }
                            });
                }else {
                    if (imgMapType.getVisibility() != View.VISIBLE) {
                        txtMap.setTextColor(getResources().getColor(R.color.red));
                        txtList.setTextColor(getResources().getColor(R.color.txt_color));
                        imgMapType.setVisibility(View.VISIBLE);
                        mapFragment = new EososMapFragment(lstFragment.getClubs(), IN_FIELD.get(1).get("value"), "planner");
                        ((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.location));
                        addFragment(R.id.lnrFragment, mapFragment);
                    }
                }
			}
		});

        txtList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(imgMapType.getVisibility()!=View.INVISIBLE){
					txtList.setTextColor(getResources().getColor(R.color.red));
					txtMap.setTextColor(getResources().getColor(R.color.txt_color));
					imgMapType.setVisibility(View.INVISIBLE);
					((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.planner));
					addFragment(R.id.lnrFragment, lstFragment);
				}
			}
		});
        imgMapType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aQuery.id(imgMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_selected));
                mapFragment.showPopupMapType(v, new OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        aQuery.id(imgMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_normal));
                    }
                });
            }
        });

	}

    public void getIntentData() {
        IN_FIELD = (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra("IN_FIELD");
    }


}
