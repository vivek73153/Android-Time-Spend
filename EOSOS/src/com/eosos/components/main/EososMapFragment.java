package com.eosos.components.main;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.mg6.android.maps.extensions.ClusteringSettings;
import pl.mg6.android.maps.extensions.GoogleMap;
import pl.mg6.android.maps.extensions.GoogleMap.InfoWindowAdapter;
import pl.mg6.android.maps.extensions.GoogleMap.OnInfoWindowClickListener;
import pl.mg6.android.maps.extensions.Marker;
import pl.mg6.android.maps.extensions.SupportMapFragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.eosos.common.classes.IjoomerMapClusterIconProvider;
import com.eosos.common.classes.IjoomerSuperMaster;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.customviews.IjoomerAutoCompleteTextView;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.library.EososDataProvider;
import com.eosos.src.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartFragment;

/**
 * This Class Contains All Method Related To JomMapActivity.
 * 
 * @author tasol
 * 
 */
public class EososMapFragment extends SmartFragment implements OnInfoWindowClickListener, EososTagHolder {

	private GoogleMap googleMap;
	private LinearLayout lnrMap;
	private SupportMapFragment mapFragment;
	private Timer t = null;
	private String today;
	private IjoomerAutoCompleteTextView edtClub;
	private LinearLayout lnrSearch;
	private IjoomerTextView txtSearchCancle;
	private ArrayList<HashMap<String, String>> autoCompleteResultList;
	private ImageView imgDirection;
	private BitmapDescriptor bitmapDescriptorActive;
	private BitmapDescriptor bitmapDescriptor;
	private ArrayList<HashMap<String, String>> IN_MAPLIST;
	private HashMap<Marker, HashMap<String, String>> markerHashMap;
	private EososDataProvider dataProvider;
	private String fromList;
	private String locationSearch;

	LatLngBounds.Builder b = new LatLngBounds.Builder();

	private final double[] CLUSTER_SIZES = new double[] { 180, 160, 144, 120, 96 };

	private MutableData[] dataArray = { new MutableData(6, new LatLng(-50, 0)), new MutableData(28, new LatLng(-52, 1)), new MutableData(496, new LatLng(-51, -2)), };
	private Handler handler = new Handler();
	private Runnable dataUpdater = new Runnable() {

		@Override
		public void run() {
			for (MutableData data : dataArray) {
				data.value = 7 + 3 * data.value;
			}
			onDataUpdate();
			handler.postDelayed(this, 1000);
		}
	};

	public EososMapFragment(ArrayList<HashMap<String, String>> data, String locationSearch, String fromList) {
		IN_MAPLIST = data;
		dataProvider = new EososDataProvider(getActivity());
		this.locationSearch = locationSearch;
		this.fromList = fromList;
	}

	public void update(ArrayList<HashMap<String, String>> data) {
		IN_MAPLIST = data;
		if (IN_MAPLIST != null && IN_MAPLIST.size() > 0) {

			try {
				b = new LatLngBounds.Builder();
				googleMap.setClustering(new ClusteringSettings().iconDataProvider(new IjoomerMapClusterIconProvider(getResources())).addMarkersDynamically(true));
				for (HashMap<String, String> mapItem : IN_MAPLIST) {
					if (mapItem.get(LATITUDE) != null && mapItem.get(LATITUDE).toString().trim().length() > 0 && mapItem.get(LONGITUDE) != null
							&& mapItem.get(LONGITUDE).toString().trim().length() > 0) {
						placeMarker(mapItem);
					}
				}
				if (fromList.equals("favourites")) {
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(Double.parseDouble(IN_MAPLIST.get(IN_MAPLIST.size() - 1).get(LATITUDE)), Double.parseDouble(IN_MAPLIST.get(IN_MAPLIST.size() - 1).get(
									LONGITUDE))), 12));
				} else if (fromList.equals("planner")) {
					getLocation(locationSearch);
				} else if (fromList.equals("directories") || fromList.equals("nearby")) {
					getLocationEurope("Europe");
				} else {
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(50.5333, 4.7667)));
				}
				googleMap.setInfoWindowAdapter(new InfoAdapter());
				googleMap.setOnInfoWindowClickListener(EososMapFragment.this);
				setUpClusteringViews();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				LatLng latLng = new LatLng(Double.parseDouble(((SmartActivity) getActivity()).getLatitude()), Double.parseDouble(((SmartActivity) getActivity()).getLongitude()));
				b.include(latLng);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Overrides methods
	 */
	@Override
	public void initComponents(View currentView) {

		lnrMap = (LinearLayout) currentView.findViewById(R.id.lnrMap);
		markerHashMap = new HashMap<Marker, HashMap<String, String>>();
		imgDirection = (ImageView) ((SmartActivity) getActivity()).getHeaderView().findViewById(R.id.imgDirection);
		imgDirection.setVisibility(View.VISIBLE);
		edtClub = (IjoomerAutoCompleteTextView) currentView.findViewById(R.id.edtClub);
		lnrSearch = (LinearLayout) currentView.findViewById(R.id.lnrSearch);
		txtSearchCancle = (IjoomerTextView) currentView.findViewById(R.id.txtSearchCancle);
	}

	@Override
	public void prepareViews(View currentView) {

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		Date d = new Date();
		today = sdf.format(d).toLowerCase();
		Log.e("today", today);
		lnrMap.setVisibility(View.VISIBLE);
		mapFragment = new SupportMapFragment();
		edtClub.setAdapter(new autoCompleteAdapter(getActivity(), R.layout.ijoomer_spinner_dropdown_item));
		addFragment(lnrMap.getId(), mapFragment);
		populateMap();

	}

	@Override
	public void setActionListeners(View currentView) {

		edtClub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edtClub.setText("");
				edtClub.setFocusable(true);
				edtClub.setFocusableInTouchMode(true);

			}
		});

		imgDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (fromList.equals("planner")) {
						getLocation(locationSearch);
					} else if (fromList.equals("favourites")) {
						googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(Double.parseDouble(IN_MAPLIST.get(IN_MAPLIST.size() - 1).get(LATITUDE)), Double.parseDouble(IN_MAPLIST.get(IN_MAPLIST.size() - 1).get(
										LONGITUDE))), 12));

					} else if (fromList.equals("directories") || fromList.equals("nearby")) {
						getLocationEurope("Europe");
					} else {
						getMyLocation();
					}
				} catch (Exception e) {
					getLocation("Europe");
				}

			}
		});
		edtClub.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH && (autoCompleteResultList == null || autoCompleteResultList.size() == 0)) {
					edtClub.setError("No match found");
					return true;
				}
				return false;
			}
		});
		edtClub.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (autoCompleteResultList != null && autoCompleteResultList.get(0) != null) {
						HashMap<String, String> entry = autoCompleteResultList.get(0);
						googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(entry.get(LATITUDE)), Double.parseDouble(entry.get(LONGITUDE))), 14));
						((IjoomerSuperMaster) getActivity()).hideSoftKeyboard();
						lnrSearch.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
		edtClub.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos, long arg3) {

				try {
					if (autoCompleteResultList != null && autoCompleteResultList.get(pos) != null) {
						HashMap<String, String> entry = autoCompleteResultList.get(pos);
						googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(entry.get(LATITUDE)), Double.parseDouble(entry.get(LONGITUDE))), 14));
						((IjoomerSuperMaster) getActivity()).hideSoftKeyboard();
						lnrSearch.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		txtSearchCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showSearchBar();
			}
		});
	}

	@Override
	public int setLayoutId() {
		return R.layout.eosos_map_fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		handler.post(dataUpdater);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(dataUpdater);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if (marker.isCluster()) {
			List<Marker> markers = marker.getMarkers();
			Builder builder = LatLngBounds.builder();
			for (Marker m : markers) {
				builder.include(m.getPosition());
			}
			LatLngBounds bounds = builder.build();
			googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.padding)));
		} else {
			final HashMap<String, String> data = markerHashMap.get(marker);
			if (data.containsKey("id")) {
				try {
					((SmartActivity) getActivity()).loadNew(EososEntryDetailActivityNew.class, getActivity(), false, "IN_ID", data.get("id"));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			marker.hideInfoWindow();
		}

	}

	/**
	 * Class methods
	 */

	public void hideSearchBar() {
		lnrSearch.setVisibility(View.GONE);
	}

	public void showSearchBar() {

		if (lnrSearch.getVisibility() == View.GONE)
			lnrSearch.setVisibility(View.VISIBLE);
		else
			lnrSearch.setVisibility(View.GONE);
	}

	/**
	 * This method used to set clustering view.
	 */
	private void setUpClusteringViews() {
		ClusteringSettings clusteringSettings = new ClusteringSettings();
		clusteringSettings.addMarkersDynamically(true);

		clusteringSettings.iconDataProvider(new IjoomerMapClusterIconProvider(getResources()));

		double clusterSize = CLUSTER_SIZES[0];
		clusteringSettings.clusterSize(clusterSize);

		googleMap.setClustering(clusteringSettings);

	}

	private void getMyLocation() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					LatLng latLng = new LatLng(Double.parseDouble(((SmartActivity) getActivity()).getLatitude()),
							Double.parseDouble(((SmartActivity) getActivity()).getLongitude()));
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getLocation(final String locationSearch) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Address address = IjoomerUtilities.getLatLongFromAddress(locationSearch);
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12));
			}
		});

	}

	private void getLocationEurope(final String locationSearch) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				LatLngBounds bounds = b.build();

				// Change the padding as per needed
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, ((SmartActivity) getActivity()).getDeviceWidth(), ((SmartActivity) getActivity()).getDeviceHeight(),
						5);
				googleMap.animateCamera(cu);
			}
		});

	}

	/**
	 * This method used to populate map.
	 */
	private void populateMap() {
		t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {

							googleMap = mapFragment.getExtendedMap();

							if (googleMap != null) {
								bitmapDescriptorActive = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.location_img1));
								bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.location_img2));
								t.cancel();
							}
							googleMap.clear();
							try {
								googleMap.setMyLocationEnabled(true);
								googleMap.getUiSettings().setMyLocationButtonEnabled(false);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (IN_MAPLIST != null && IN_MAPLIST.size() > 0) {
								b = new LatLngBounds.Builder();
								try {
									googleMap.setClustering(new ClusteringSettings().iconDataProvider(new IjoomerMapClusterIconProvider(getResources()))
											.addMarkersDynamically(true));
									for (HashMap<String, String> mapItem : IN_MAPLIST) {
										if (mapItem.get(LATITUDE) != null && mapItem.get(LATITUDE).trim().length() > 0 && mapItem.get(LONGITUDE) != null
												&& mapItem.get(LONGITUDE).trim().length() > 0) {
											placeMarker(mapItem);
										}
									}
									if (fromList.equals("favourites")) {
										googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
												new LatLng(Double.parseDouble(IN_MAPLIST.get(IN_MAPLIST.size() - 1).get(LATITUDE)), Double.parseDouble(IN_MAPLIST.get(
														IN_MAPLIST.size() - 1).get(LONGITUDE))), 12));
									} else if (fromList.equals("planner")) {
										getLocation(locationSearch);
									} else if (fromList.equals("directories") || fromList.equals("nearby")) {
										getLocationEurope("Europe");
									} else {
										googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(50.5333, 4.7667)));
									}

									googleMap.setInfoWindowAdapter(new InfoAdapter());
									googleMap.setOnInfoWindowClickListener(EososMapFragment.this);
									setUpClusteringViews();
								} catch (Exception e) {

									e.printStackTrace();
								}

							} else {
								LatLng latLng = new LatLng(Double.parseDouble(((SmartActivity) getActivity()).getLatitude()), Double.parseDouble(((SmartActivity) getActivity())
										.getLongitude()));
								b.include(latLng);
								getMyLocation();
							}

						} catch (Exception e) {
						}

					}
				});

			}
		}, 0, 500);

	}

	/**
	 * This method used to place marker on map.
	 * 
	 * @param markerData
	 *            represented markar data.
	 */
	private void placeMarker(final HashMap<String, String> markerData) {
		try {
			b.include(new LatLng(Double.parseDouble(markerData.get("latitude")), Double.parseDouble(markerData.get("longitude"))));
			if (today != null && dataProvider.isClubOpen(markerData.get("id"), today)) {
				markerHashMap.put(
						googleMap.addMarker(new MarkerOptions().title(markerData.get("title")).icon(bitmapDescriptorActive)
								.position(new LatLng(Double.parseDouble(markerData.get("latitude")), Double.parseDouble(markerData.get("longitude"))))), markerData);

			} else {
				markerHashMap.put(
						googleMap.addMarker(new MarkerOptions().title(markerData.get("title")).icon(bitmapDescriptor)
								.position(new LatLng(Double.parseDouble(markerData.get("latitude")), Double.parseDouble(markerData.get("longitude"))))), markerData);
			}
		} catch (Exception e) {
			markerHashMap.put(
					googleMap.addMarker(new MarkerOptions().title(markerData.get("title")).icon(bitmapDescriptorActive)
							.position(new LatLng(Double.parseDouble(markerData.get("latitude")), Double.parseDouble(markerData.get("longitude"))))), markerData);

		}
	}

	/**
	 * This method used to on map data update.
	 */
	private void onDataUpdate() {
		try {
			Marker m = googleMap.getMarkerShowingInfoWindow();
			if (m != null && !m.isCluster() && m.getData() instanceof MutableData) {
				m.showInfoWindow();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Custom marker info adapter.
	 * 
	 * @author tasol
	 * 
	 */
	class InfoAdapter implements InfoWindowAdapter {

		private TextView tv;
		{

			tv = new TextView(getActivity());
			tv.setTextColor(Color.BLACK);
		}

		private Collator collator = Collator.getInstance();
		private Comparator<Marker> comparator = new Comparator<Marker>() {
			public int compare(Marker lhs, Marker rhs) {
				String leftTitle = lhs.getTitle();
				String rightTitle = rhs.getTitle();
				if (leftTitle == null && rightTitle == null) {
					return 0;
				}
				if (leftTitle == null) {
					return 1;
				}
				if (rightTitle == null) {
					return -1;
				}
				return collator.compare(leftTitle, rightTitle);
			}
		};

		@Override
		public View getInfoContents(Marker marker) {
			if (marker.isCluster()) {
				List<Marker> markers = marker.getMarkers();
				int i = 0;
				String text = "";
				while (i < 3 && markers.size() > 0) {
					Marker m = Collections.min(markers, comparator);
					String title = m.getTitle();
					if (title == null) {
						break;
					}
					text += title + "\n";
					markers.remove(m);
					i++;
				}
				if (text.length() == 0) {
					text = "Markers with mutable data";
				} else if (markers.size() > 0) {
					text += "and " + markers.size() + " more...";
				} else {
					text = text.substring(0, text.length() - 1);
				}
				tv.setText(text);
				return tv;
			} else {
				Object data = marker.getData();
				if (data instanceof MutableData) {
					MutableData mutableData = (MutableData) data;
					tv.setText("Value: " + mutableData.value);
					return tv;
				} else {
					return null;
				}
			}
		}

		@Override
		public View getInfoWindow(Marker marker) {
			if (!marker.isCluster()) {
				final HashMap<String, String> data = markerHashMap.get(marker);

				View view = LayoutInflater.from(getActivity()).inflate(R.layout.eosos_map_bubble, null);

				IjoomerTextView txtTitle = (IjoomerTextView) view.findViewById(R.id.txtTitle);
				ImageView imgInfo = (ImageView) view.findViewById(R.id.imgInfo);
				imgInfo.setVisibility(View.VISIBLE);
				txtTitle.setText(data.get("title"));

				return view;
			} else {
				return null;
			}

		}

	}

	public static Rect locateView(View v) {
		int[] loc_int = new int[2];
		if (v == null)
			return null;
		try {

			v.getLocationOnScreen(loc_int);
		} catch (NullPointerException npe) {
			// Happens when the view doesn't exist on screen anymore.
			return null;
		}
		Rect location = new Rect();
		location.left = loc_int[0];
		location.top = loc_int[1];
		location.right = location.left + v.getWidth();
		location.bottom = location.top + v.getHeight();

		Log.e("Rleft", location.left + "");
		Log.e("Rtop", location.top + "");
		Log.e("Rright", location.right + "");
		Log.e("Rbottom", location.bottom + "");
		// Rect(247, 440,277, 470)
		return location;
	}

	public void showPopupMapType(View v, final OnDismissListener dismiss) {

		Rect rect = locateView(v);

		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.eosos_maptype_popup, null);
		final IjoomerTextView txtNormal = (IjoomerTextView) layout.findViewById(R.id.txtNormal);
		final IjoomerTextView txtHybrid = (IjoomerTextView) layout.findViewById(R.id.txtHybrid);
		final IjoomerTextView txtSatellite = (IjoomerTextView) layout.findViewById(R.id.txtSatellite);

		final PopupWindow popup = new PopupWindow(getActivity());
		popup.setAnimationStyle(R.style.animation);
		popup.setContentView(layout);
		popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		// popup.setWidth(((SmartActivity) getActivity()).getDeviceWidth() / 2);
		popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);
		popup.setBackgroundDrawable(new BitmapDrawable(getResources()));
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				dismiss.onDismiss();
			}
		});
		popup.showAtLocation(layout, Gravity.LEFT | Gravity.BOTTOM, (rect.left - v.getWidth() / 2), ((SmartActivity) getActivity()).getDeviceHeight() - rect.top);
		if (popup.isShowing()) {

			txtNormal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						popup.dismiss();
						if (googleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)
							googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			txtHybrid.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						popup.dismiss();
						if (googleMap.getMapType() != GoogleMap.MAP_TYPE_HYBRID)
							googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			txtSatellite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						popup.dismiss();
						if (googleMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
							googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}

	}

	/**
	 * Inner class
	 * 
	 * @author tasol
	 * 
	 */
	private class MutableData {

		private int value;
		@SuppressWarnings("unused")
		private LatLng position;

		public MutableData(int value, LatLng position) {
			this.value = value;
			this.position = position;
		}
	}

	@Override
	public View setLayoutView() {
		return null;
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
					if (constraint != null && constraint.toString().trim().length() > 1) {

						for (int i = 0; i < IN_MAPLIST.size(); i++) {
							if (IN_MAPLIST.get(i).get("title").toLowerCase().contains(constraint.toString().toLowerCase())) {
								autoCompleteResultList.add(IN_MAPLIST.get(i));
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
