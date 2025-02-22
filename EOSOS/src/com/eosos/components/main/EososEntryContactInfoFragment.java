package com.eosos.components.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.eosos.caching.IjoomerCaching;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.library.EososDataProvider;
import com.eosos.page.indicator.CirclePageIndicator;
import com.eosos.src.R;
import com.eosos.weservice.WebCallListener;
import com.smart.framework.CustomAlertNeutral;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartFragment;

import org.json.JSONArray;

/**
 * This Fragment Contains All Method Related To IcmsArchivedArticlesFragment.
 * 
 * @author tasol
 * 
 */
public class EososEntryContactInfoFragment extends SmartFragment implements EososTagHolder {

	private EososDataProvider dataProvider;
	private ViewPager viewPager;
    private CirclePageIndicator indicator;
	private ImageFragmentAdapter imageFragmentAdapter;
	private ImageView imgFav;
	private ArrayList<HashMap<String, String>> data;
	private ArrayList<String> images;
	private IjoomerTextView txtTitle, txtCity, txtAddress, txtEmail, txtPhone, txtWeb;
	private SeekBar seekEmail, seekPhone, seekWeb, seekAddress;
	private String FAVOURITE = "Favourite";
	private HashMap<String, String> dataFavourite;
	int progress;

	public EososEntryContactInfoFragment(ArrayList<HashMap<String, String>> data) {
		this.data = data;
	}

	/**
	 * Overrides methods
	 */
	@Override
	public int setLayoutId() {
		return R.layout.eosos_entry_contact_fragment;
	}

	@Override
	public void initComponents(View currentView) {
		dataProvider = new EososDataProvider(getActivity());
		txtTitle = (IjoomerTextView) currentView.findViewById(R.id.txtTitle);
		txtCity = (IjoomerTextView) currentView.findViewById(R.id.txtCity);
		txtEmail = (IjoomerTextView) currentView.findViewById(R.id.txtEmail);
		txtPhone = (IjoomerTextView) currentView.findViewById(R.id.txtPhone);
		txtWeb = (IjoomerTextView) currentView.findViewById(R.id.txtWeb);
		txtAddress = (IjoomerTextView) currentView.findViewById(R.id.txtAddress);
		seekWeb = (SeekBar) currentView.findViewById(R.id.seekWeb);
		seekEmail = (SeekBar) currentView.findViewById(R.id.seekEmail);
		seekPhone = (SeekBar) currentView.findViewById(R.id.seekPhone);
		seekAddress = (SeekBar) currentView.findViewById(R.id.seekAddress);
		viewPager = (ViewPager) currentView.findViewById(R.id.viewpager);
        indicator = (CirclePageIndicator) currentView.findViewById(R.id.indicator);
		imgFav = (ImageView) currentView.findViewById(R.id.imgFav);

		images = new ArrayList<String>();
		dataFavourite = new HashMap<String, String>();

	}

	@Override
	public void onResume() {
		super.onResume();
		seekAddress.setProgress(0);
		seekEmail.setProgress(0);
		seekPhone.setProgress(0);
		seekWeb.setProgress(0);
	}

	@Override
	public void prepareViews(View currentView) {


		try {
			if (data.get(0).get("isFavorite").equals("0")){
                imgFav.setImageResource(R.drawable.eosos_favourite);
            }
            else{
                imgFav.setImageResource(R.drawable.favourties);
            }

			if (data.get(0).get("image1").trim().length() > 0)
				images.add(data.get(0).get("image1"));
			if (data.get(0).get("image2").trim().length() > 0)
				images.add(data.get(0).get("image2"));
			if (data.get(0).get("image3").trim().length() > 0)
				images.add(data.get(0).get("image3"));
			imageFragmentAdapter = new ImageFragmentAdapter(getFragmentManager(), images);
			viewPager.setAdapter(imageFragmentAdapter);
			viewPager.setCurrentItem(0);
            indicator.setPageColor(Color.parseColor(getString(R.color.white)));
            indicator.setStrokeColor(Color.parseColor(getString(R.color.white)));
            indicator.setStrokeWidth(((SmartActivity)getActivity()).convertSizeToDeviceDependent(1));
            indicator.setRadius(((SmartActivity)getActivity()).convertSizeToDeviceDependent(5));
            indicator.setFillColor(Color.parseColor(getString(R.color.txt_header)));
            indicator.setViewPager(viewPager, 0);
            indicator.setSnap(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try {
			txtTitle.setText(data.get(0).get("title"));
			txtCity.setText(data.get(0).get("city"));
			String email = data.get(0).get("email");
			if (email.startsWith("mailto:"))
				email = email.replace("mailto:", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void updateData(String id){
        ArrayList<HashMap<String, String>> data1 = dataProvider.getEntryDetailsFromDb(id);
        if (data1 != null && data1.size() > 0) {
            data = data1;
            HashMap<String, String> hashMap = data.get(0);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(hashMap.get("field"));
                for (int j = 0; j < jsonArray.length(); j++) {
                    if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_image1")) {
                        hashMap.put("image1", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_image2")) {
                        hashMap.put("image2", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_image3")) {
                        hashMap.put("image3", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_description")) {
                        hashMap.put("description", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_email")) {
                        hashMap.put("email", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_phone")) {
                        hashMap.put("phone", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_website")) {
                        hashMap.put("website", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_website")) {
                        hashMap.put("website", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_address")) {
                        hashMap.put("address", jsonArray.getJSONObject(j).get("value").toString());
                    } else if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_city")) {
                        hashMap.put("city", jsonArray.getJSONObject(j).get("value").toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public void setActionListeners(View currentView) {

		imgFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (data != null && data.size() > 0) {
						if (data.get(0).get("isFavorite").equals("0")) {
                            final SeekBar seekBar = IjoomerUtilities.getLoadingDialog(getString(R.string.dialog_loading_sending_request));
                            dataProvider.addFavorite(data.get(0).get("id"),((SmartActivity)getActivity()).getDeviceUDID(),new WebCallListener() {
                                @Override
                                public void onCallComplete(int responseCode, String errorMessage, final ArrayList<HashMap<String, String>> data1, Object data2) {
                                    if(responseCode==200){
                                        IjoomerUtilities.getCustomOkDialog(getActivity().getString(R.string.eosos_entry_details), getActivity().getString(R.string.eosos_added_as_favourite),
                                                getActivity().getString(R.string.ok), R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

                                                    @Override
                                                    public void NeutralMethod() {
                                                        imgFav.setImageResource(R.drawable.favourties);
                                                        updateData(data.get(0).get("id"));
                                                    }
                                                }
                                        );

                                    }else{
                                        if(responseCode==599){
                                            imgFav.setImageResource(R.drawable.favourties);
                                            updateData(data.get(0).get("id"));
                                        }else{
                                            responseMessageHandler(responseCode);
                                        }
                                    }
                                }

                                @Override
                                public void onProgressUpdate(int progressCount) {
                                    seekBar.setProgress(progressCount);
                                }
                            });
						} else {
                            final SeekBar seekBar = IjoomerUtilities.getLoadingDialog(getString(R.string.dialog_loading_sending_request));
                            dataProvider.removeFavorite(data.get(0).get("id"),((SmartActivity)getActivity()).getDeviceUDID(),new WebCallListener() {
                                @Override
                                public void onCallComplete(int responseCode, String errorMessage, ArrayList<HashMap<String, String>> data1, Object data2) {
                                    if(responseCode==200){
                                        IjoomerUtilities.getCustomOkDialog(getActivity().getString(R.string.eosos_entry_details), getActivity().getString(R.string.eosos_removed_from_favourite),
                                                getActivity().getString(R.string.ok), R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

                                                    @Override
                                                    public void NeutralMethod() {
                                                        imgFav.setImageResource(R.drawable.eosos_favourite);
                                                        updateData(data.get(0).get("id"));
                                                    }
                                                }
                                        );

                                    }else{
                                        if(responseCode==599){
                                            imgFav.setImageResource(R.drawable.eosos_favourite);
                                            updateData(data.get(0).get("id"));
                                        }else{
                                            responseMessageHandler(responseCode);
                                        }
                                    }
                                }

                                @Override
                                public void onProgressUpdate(int progressCount) {
                                    seekBar.setProgress(progressCount);
                                }
                            });
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		seekAddress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				setProgreeHandler(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 100) {
					try {
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW ,
						        Uri.parse("geo:0,0?q= (" + data.get(0).get("address")+" "+txtCity.getText()+ ")"));
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		seekPhone.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setProgreeHandler(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 100) {
					try {
						((EososEntryDetailActivityNew) getActivity()).call(data.get(0).get("phone"));
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
		seekEmail.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setProgreeHandler(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 100) {
					try {
						((EososEntryDetailActivityNew) getActivity()).onEmail(data.get(0).get("email").replace("mailto:", "").trim(), "");
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
		seekWeb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setProgreeHandler(seekBar);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 100) {
                    Intent web = new Intent(Intent.ACTION_VIEW);
                    web.setData(Uri.parse(data.get(0).get("website")));
                    startActivity(web);
				}
			}
		});

	}

    private void responseMessageHandler(final int responseCode) {
        IjoomerUtilities.getCustomOkDialog(getActivity().getString(R.string.eosos_entry_details), getActivity().getString(getResources().getIdentifier("code" + responseCode, "string", getActivity().getPackageName())),
                getActivity().getString(R.string.ok), R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

                    @Override
                    public void NeutralMethod() {
                    }
                }
        );
    }
	@Override
	public View setLayoutView() {
		return null;
	}

	/**
	 * Custom class Adapter
	 */
	private class ImageFragmentAdapter extends FragmentStatePagerAdapter {

		ArrayList<String> arrayImgs;

		public ImageFragmentAdapter(FragmentManager fm, ArrayList<String> arrayImgs) {
			super(fm);
			this.arrayImgs = arrayImgs;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int pos) {
			System.gc();

			return new EososImageFragment(arrayImgs.get(pos));

		}

		@Override
		public int getCount() {
			return images.size();
		}
	}

	public void setProgreeHandler(final SeekBar seekBar) {

		progress = seekBar.getProgress();
		if (progress > 50) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					while (progress < 100) {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								progress += 5;
								seekBar.setProgress(progress);
							}
						});
					}

				}
			}).start();

		} else {
			seekBar.setProgress(0);
		}

	}

}