package com.eosos.components.main;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.common.flipanimation.AnimationFactory;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.src.R;
import com.smart.framework.CustomAlertNeutral;

public class EososEntryListActivity extends EososMasterActivity {
	private IjoomerTextView txtDirectoriesList, txtDirectoriesMap;
	private IjoomerTextView txtFavouritesList, txtFavouritesMap;

	public ImageView imgSearch, imgFav, imgDirectory;
	public ImageView imgDirectoriesMapType;
	public ImageView imgFavouritesMapType;
	public ImageView imgFavouritesUnit;

	public EososMapFragment mapFragment;
	public EososDirectoryListFragment lstFragment;
	public EososFavouriteEntryListFragment favouriteEntryListFragment;
	public EososMapFragment directoriesMapFragment;
	public EososMapFragment favouritesMapFragment;
	private AQuery aQuery;
	private ViewFlipper viewFlipper;
	private boolean isUnitPressed;
	private boolean isDirectoriesList = true;
	private ViewGroup main;

	@Override
	public int setLayoutId() {
		return R.layout.eosos_entry;
	}

	@Override
	public void initComponents() {
		main = (ViewGroup) findViewById(R.id.main);
		txtDirectoriesList = (IjoomerTextView) findViewById(R.id.txtDirectoriesList);
		txtDirectoriesMap = (IjoomerTextView) findViewById(R.id.txtDirectoriesMap);
		imgDirectoriesMapType = (ImageView) findViewById(R.id.imgDirectoriesMapType);
		txtFavouritesList = (IjoomerTextView) findViewById(R.id.txtFavouritesList);
		txtFavouritesMap = (IjoomerTextView) findViewById(R.id.txtFavouritesMap);
		imgFavouritesMapType = (ImageView) findViewById(R.id.imgFavouritesMapType);
		imgFavouritesUnit = (ImageView) findViewById(R.id.imgFavouritesUnit);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		aQuery = new AQuery(this);
	}

	@Override
	public void prepareViews() {

		isDirectoriesList = true;
		imgSearch = (ImageView) getHeaderView().findViewById(R.id.imgSearch);
		imgSearch.setVisibility(View.VISIBLE);
		imgFavouritesUnit.setVisibility(View.INVISIBLE);
		imgFav = (ImageView) getHeaderView().findViewById(R.id.imgFav);
		imgDirectory = (ImageView) getHeaderView().findViewById(R.id.imgDirectory);
		txtDirectoriesList.setTextColor(getResources().getColor(R.color.red));
		((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.directory));
		imgFav.setVisibility(View.VISIBLE);
		lstFragment = new EososDirectoryListFragment();
		favouriteEntryListFragment = new EososFavouriteEntryListFragment(false);
		addFragment(R.id.lnrDirectoriesFragment, lstFragment);
		addFragment(R.id.lnrFavouritesFragment, favouriteEntryListFragment);

	}

	@Override
	public void setActionListeners() {

		txtDirectoriesMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(!IjoomerUtilities.isNetwokReachable()){
                    IjoomerUtilities.getCustomOkDialog(getString(R.string.directory), getString(R.string.code599), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
                            new CustomAlertNeutral() {

                                @Override
                                public void NeutralMethod() {
                                }
                            });
                }else {
                    if (imgFav.getVisibility() != View.GONE) {
                        txtDirectoriesMap.setTextColor(getResources().getColor(R.color.red));
                        txtDirectoriesList.setTextColor(getResources().getColor(R.color.txt_color));
                        imgDirectoriesMapType.setVisibility(View.VISIBLE);
                        imgFav.setVisibility(View.GONE);
                        directoriesMapFragment = new EososMapFragment(lstFragment.getClubs(), "", "directories");
                        ((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.location));
                        addFragment(R.id.lnrDirectoriesFragment, directoriesMapFragment);
                    }
                }
			}
		});

		txtDirectoriesList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgFav.getVisibility() != View.VISIBLE) {
					txtDirectoriesList.setTextColor(getResources().getColor(R.color.red));
					txtDirectoriesMap.setTextColor(getResources().getColor(R.color.txt_color));
					imgFav.setVisibility(View.VISIBLE);
					imgDirectoriesMapType.setVisibility(View.INVISIBLE);
					((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.directory));
					lstFragment = new EososDirectoryListFragment();
					addFragment(R.id.lnrDirectoriesFragment, lstFragment);
				}
			}
		});

		txtFavouritesMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(!IjoomerUtilities.isNetwokReachable()){
                    IjoomerUtilities.getCustomOkDialog(getString(R.string.favourites), getString(R.string.code599), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
                            new CustomAlertNeutral() {

                                @Override
                                public void NeutralMethod() {
                                }
                            });
                }else {
                    if (imgFavouritesMapType.getVisibility() != View.VISIBLE) {
                        txtFavouritesMap.setTextColor(getResources().getColor(R.color.red));
                        txtFavouritesList.setTextColor(getResources().getColor(R.color.txt_color));
                        imgFavouritesMapType.setVisibility(View.VISIBLE);
                        imgFavouritesUnit.setVisibility(View.INVISIBLE);
                        favouritesMapFragment = new EososMapFragment(favouriteEntryListFragment.getClubs(), "", "favourites");
                        ((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.location));
                        addFragment(R.id.lnrFavouritesFragment, favouritesMapFragment);
                        imgDirectory.setVisibility(View.GONE);
                    }
                }
			}
		});

		txtFavouritesList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgFavouritesMapType.getVisibility() != View.INVISIBLE) {
					txtFavouritesList.setTextColor(getResources().getColor(R.color.red));
					txtFavouritesMap.setTextColor(getResources().getColor(R.color.txt_color));
					imgFavouritesMapType.setVisibility(View.INVISIBLE);
                    imgFavouritesUnit.setVisibility(View.INVISIBLE);
					((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.favourites));
					favouriteEntryListFragment = new EososFavouriteEntryListFragment(false);
					addFragment(R.id.lnrFavouritesFragment, favouriteEntryListFragment);
					imgDirectory.setVisibility(View.VISIBLE);
				}
			}
		});

		imgFavouritesUnit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isUnitPressed) {
					aQuery.id(imgFavouritesUnit).image(getResources().getDrawable(R.drawable.unit_btn_icon_normal));
					isUnitPressed = false;
				} else {
					aQuery.id(imgFavouritesUnit).image(getResources().getDrawable(R.drawable.unit_btn_icon_selected));
					isUnitPressed = true;
				}
				favouriteEntryListFragment.changeDistanceUnit(isUnitPressed);
			}
		});

		imgSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgFav.getVisibility() == View.VISIBLE) {
					lstFragment.showSearchBar();
				} else {
					directoriesMapFragment.showSearchBar();
				}
			}
		});
		imgFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imgFav.setVisibility(View.GONE);
				imgSearch.setVisibility(View.GONE);
				imgDirectory.setVisibility(View.VISIBLE);
                imgFavouritesUnit.setVisibility(View.INVISIBLE);
				isDirectoriesList = false;
				AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
				favouriteEntryListFragment.showNoContentDialog();
				((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.favourites));
				txtFavouritesList.setTextColor(getResources().getColor(R.color.red));
			}
		});
		imgDirectory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgFav.setVisibility(View.VISIBLE);
				imgSearch.setVisibility(View.VISIBLE);
				imgDirectory.setVisibility(View.GONE);
				isDirectoriesList = true;
				AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
				((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.directory));
			}
		});

		imgDirectoriesMapType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aQuery.id(imgDirectoriesMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_selected));
				directoriesMapFragment.showPopupMapType(v, new OnDismissListener() {

					@Override
					public void onDismiss() {
						aQuery.id(imgDirectoriesMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_normal));
					}
				});
			}
		});
		imgFavouritesMapType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aQuery.id(imgFavouritesMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_selected));
				favouritesMapFragment.showPopupMapType(v, new OnDismissListener() {

					@Override
					public void onDismiss() {
						aQuery.id(imgFavouritesMapType).image(getResources().getDrawable(R.drawable.map_btn_icon_normal));
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (imgFav.getVisibility() == View.VISIBLE) {
			lstFragment.hideSearchBar();
		} else {
			if (directoriesMapFragment != null) {
				directoriesMapFragment.showSearchBar();
			}
		}
	}
}
