package com.eosos.components.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.eosos.common.classes.IjoomerMapAddress;
import com.eosos.common.classes.IjoomerMapDirection;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.customviews.IjoomerAutoCompleteTextView;
import com.eosos.customviews.IjoomerButton;
import com.eosos.customviews.IjoomerEditText;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.library.EososDataProvider;
import com.eosos.src.R;
import com.smart.framework.CustomAlertNeutral;

public class EososRouteActivity extends EososMasterActivity {

	private IjoomerTextView txtRoute;
	private IjoomerEditText edtLocation;
	private IjoomerAutoCompleteTextView edtClub;
	private Spinner spnMethod;
	private ProgressBar pbr;
	final private int GET_ADDRESS_FROM_MAP = 1;
	private ArrayList<HashMap<String,String>> clubs;
	private EososDataProvider dataProvider;
	private ArrayList<String> methodList;
	private HashMap<String, String> selectedClub;
	private String[] trtravelModes;
	private String fromLatitude = "";
	private String fromLongitude = "";
	private ArrayList<HashMap<String, String>> autoCompleteResultList;
	ArrayList<String> nightList;
	private String IN_PARENT_ID = "55", IN_SECTION_ID = "54";
	private String IN_FEATUREDFIRST = "No";
	

	@Override
	public int setLayoutId() {
		return R.layout.eosos_route;
	}

	@Override
	public void initComponents() {

        txtRoute = (IjoomerTextView) findViewById(R.id.txtRoute);
		spnMethod = (Spinner) findViewById(R.id.spnSelectMethod);
		edtLocation = (IjoomerEditText) findViewById(R.id.edtLocation);
		edtClub = (IjoomerAutoCompleteTextView) findViewById(R.id.edtClub);
		pbr = (ProgressBar) findViewById(R.id.pbr);
		dataProvider = new EososDataProvider(EososRouteActivity.this);

		fromLatitude = getLatitude();
		fromLongitude = getLongitude();
		String[] methodArray = getResources().getStringArray(R.array.methods);
		trtravelModes = getResources().getStringArray(R.array.travelmodes);
		methodList = new ArrayList<String>(Arrays.asList(methodArray));

	}

    @Override
    protected void onResume() {
        super.onResume();
        txtRoute.setTextColor(getResources().getColor(R.color.txt_color));
    }

    @Override
	public void prepareViews() {
//		getIntentData();
		clubs=dataProvider.getClubList();
		edtClub.setAdapter(new autoCompleteAdapter(this, R.layout.ijoomer_spinner_dropdown_item));

		((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.route));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.ijoomer_spinner_item, methodList);
		dataAdapter.setDropDownViewResource(R.layout.ijoomer_spinner_dropdown_item);
		spnMethod.setAdapter(dataAdapter);
	}

	@Override
	public void setActionListeners() {

		edtClub.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos, long arg3) {

				selectedClub = (HashMap<String, String>) autoCompleteResultList.get(pos);

			}
		});
		edtClub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edtClub.setText("");
				selectedClub = null;
				edtClub.setFocusable(true);
				edtClub.setFocusableInTouchMode(true);
			}
		});

		edtLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                if(!IjoomerUtilities.isNetwokReachable()){
                    IjoomerUtilities.getCustomOkDialog(getString(R.string.route), getString(R.string.code599), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
                            new CustomAlertNeutral() {

                                @Override
                                public void NeutralMethod() {
                                    finish();
                                }
                            });
                }else {
                    Intent intent = new Intent(EososRouteActivity.this, IjoomerMapAddress.class);
                    startActivityForResult(intent, GET_ADDRESS_FROM_MAP);
                }
			}
		});

		txtRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                txtRoute.setTextColor(getResources().getColor(R.color.red));
                if(!IjoomerUtilities.isNetwokReachable()){
                    IjoomerUtilities.getCustomOkDialog(getString(R.string.route), getString(R.string.code599), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
                            new CustomAlertNeutral() {

                                @Override
                                public void NeutralMethod() {
                                    finish();
                                }
                            });
                }else{
                    if (validateInputs()) {

                        try {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr="+fromLatitude+","+fromLongitude+"&daddr="+selectedClub.get(LATITUDE)+","+selectedClub.get(LONGITUDE)));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            edtClub.setError("No match found");
                        }
                    }else{
                        txtRoute.setTextColor(getResources().getColor(R.color.txt_color));
                    }
                }
			}
		});

	}

	public boolean validateInputs() {
		boolean valid = true;
		if (edtClub.getText().toString().trim().length() == 0) {

			edtClub.setError(getResources().getString(R.string.validation_value_required));
			valid = false;
		}
		return valid;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == GET_ADDRESS_FROM_MAP) {
				HashMap<String, String> resultData = (HashMap<String, String>) data.getSerializableExtra("MAP_ADDRESSS_DATA");
				fromLatitude = resultData.get("latitude");
				fromLongitude = resultData.get("longitude");
				edtLocation.setText(resultData.get("address"));
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}

	}

	public void getIntentData() {
		if (getSmartApplication().readSharedPreferences().getString(SP_DEFAULT_LANDING_SCREEN, "").length() > 0) {
			try {
				JSONObject intentData = new JSONObject(getSmartApplication().readSharedPreferences().getString(SP_LAST_ACTIVITY_INTENT, "")).getJSONObject("itemdata");
				IN_PARENT_ID = intentData.getString("categoryID");
				IN_SECTION_ID = intentData.getString("sectionID");
				IN_FEATUREDFIRST = intentData.getString("featuredFirst");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class autoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

		public autoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return autoCompleteResultList.size();
		}

		@Override
		public String getItem(int position) {
			return autoCompleteResultList.get(position).get("title");
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(final CharSequence constraint) {
					final FilterResults filterResults = new FilterResults();
					autoCompleteResultList = new ArrayList<HashMap<String, String>>();
					if (constraint != null) {
						for (HashMap<String, String> club : clubs) {
							if (club.get("title").toLowerCase().contains(constraint.toString().toLowerCase())) {
								autoCompleteResultList.add(club);
							}
						}
						filterResults.values = autoCompleteResultList;
						filterResults.count = autoCompleteResultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}
}
