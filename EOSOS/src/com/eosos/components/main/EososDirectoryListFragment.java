package com.eosos.components.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.UserDictionary;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.eosos.common.classes.IjoomerSuperMaster;
import com.eosos.common.classes.ViewHolder;
import com.eosos.custom.interfaces.IjoomerSharedPreferences;
import com.eosos.customviews.IjoomerAutoCompleteTextView;
import com.eosos.customviews.IjoomerIndexableListView;
import com.eosos.customviews.IjoomerRatingBar;
import com.eosos.customviews.IjoomerTextView;
import com.eosos.library.EososDataProvider;
import com.eosos.src.R;
import com.smart.framework.ItemView;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartFragment;
import com.smart.framework.SmartListAdapterIndexScrolling;
import com.smart.framework.SmartListItem;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class EososDirectoryListFragment extends SmartFragment implements EososTagHolder, IjoomerSharedPreferences {

    private IjoomerIndexableListView listDirectory;
    private ArrayList<SmartListItem> listData = new ArrayList<SmartListItem>();
    private SmartListAdapterIndexScrolling listAdapterWithHolder;
    private ArrayList<HashMap<String, String>> filteredList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> autoCompleteResultList;

    private AQuery aQuery;
    private String[] sections;
    private IjoomerAutoCompleteTextView edtClub;
    private LinearLayout lnrSearch;
    private IjoomerTextView txtSearchCancle;
    private HashMap<String, Integer> alphaIndexer;
    private ArrayList<String> sectionArray;
    private ArrayList<HashMap<String, String>> clubs;
    private EososDataProvider dataProvider;
    private HashMap<String, Integer> noOfCityMap = new HashMap<String, Integer>();

    public EososDirectoryListFragment() {
    }

    @Override
    public int setLayoutId() {
        return R.layout.eosos_entry_fragment;
    }

    @Override
    public void initComponents(View currentView) {
        listDirectory = (IjoomerIndexableListView) currentView.findViewById(R.id.directoryList);
        listDirectory.setSelector(R.drawable.list_item_selector);
        aQuery = new AQuery(getActivity());
        edtClub = (IjoomerAutoCompleteTextView) currentView.findViewById(R.id.edtClub);
        lnrSearch = (LinearLayout) currentView.findViewById(R.id.lnrSearch);
        txtSearchCancle = (IjoomerTextView) currentView.findViewById(R.id.txtSearchCancle);
        alphaIndexer = new HashMap<String, Integer>();
        sectionArray = new ArrayList<String>();
        dataProvider = new EososDataProvider(getActivity());
        listDirectory.setFastScrollEnabled(true);
        ((ImageView) ((SmartActivity) getActivity()).getHeaderView().findViewById(R.id.imgDirection)).setVisibility(View.GONE);
    }

    @Override
    public void prepareViews(View currentView) {
        getIntentData();
        clubs = dataProvider.getClubList();
        listDirectory.setDivider(null);
        prepareList(clubs);
        listAdapterWithHolder = getListAdapter(listData);
        listDirectory.setAdapter(listAdapterWithHolder);

        edtClub.setAdapter(new autoCompleteAdapter(getActivity(), R.layout.ijoomer_spinner_dropdown_item));
    }


    @Override
    public void setActionListeners(View currentView) {
        edtClub.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                ((IjoomerSuperMaster) getActivity()).hideSoftKeyboard();
                edtClub.clearFocus();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        if (autoCompleteResultList != null && autoCompleteResultList.get(0) != null) {
                            applySearch();
                        }
                    } catch (Exception e) {
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
                    if (autoCompleteResultList != null && (HashMap<String, String>) autoCompleteResultList.get(pos) != null) {
                        ((SmartActivity) getActivity()).loadNew(EososEntryDetailActivityNew.class, getActivity(), false, "IN_ID",
                                ((HashMap<String, String>) autoCompleteResultList.get(pos)).get("id"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        listDirectory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    HashMap<String, String> row = (HashMap<String, String>) listData.get(arg2).getValues().get(0);
                    ((SmartActivity) getActivity()).loadNew(EososEntryDetailActivityNew.class, getActivity(), false, "IN_ID", row.get("id"));
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

    public void getIntentData() {
        if (((SmartActivity) getActivity()).getSmartApplication().readSharedPreferences().getString(SP_DEFAULT_LANDING_SCREEN, "").length() > 0) {
            try {
                JSONObject intentData = new JSONObject(((SmartActivity) getActivity()).getSmartApplication().readSharedPreferences().getString(SP_LAST_ACTIVITY_INTENT, ""))
                        .getJSONObject("itemdata");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void prepareList(ArrayList<HashMap<String, String>> data) {
        sectionArray.clear();
        if (data != null) {
            alphaIndexer = new HashMap<String, Integer>();
            sectionArray = new ArrayList<String>();
            listData.clear();
            noOfCityMap.clear();
            for (int i = 0; i < data.size(); i++) {
                HashMap<String, String> hashMap = data.get(i);

                SmartListItem item = new SmartListItem();
                item.setItemLayout(R.layout.eosos_directory_list_item);
                ArrayList<Object> obj = new ArrayList<Object>();
                obj.add(hashMap);
                if (!alphaIndexer.containsKey(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase())) {
                    alphaIndexer.put(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase(), i);
                    sectionArray.add(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase());
                    obj.add(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase());
                    noOfCityMap.put(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase(), 1);
                } else {
                    int total = noOfCityMap.get(Character.toUpperCase(hashMap.get("city").charAt(0)) +hashMap.get("city").substring(1).toLowerCase()) + 1;
                    noOfCityMap.put(hashMap.get("city"), total);
                }
                item.setValues(obj);
                listData.add(item);
            }
            setSections();
        }

    }

    public void setSections() {

        sections = new String[sectionArray.size()];
        for (int i = 0; i < sectionArray.size(); i++) {
            String ch = sectionArray.get(i).substring(0, 1);
            sections[i] = ch;
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

    public SmartListAdapterIndexScrolling getListAdapter(final ArrayList<SmartListItem> listData) {

        SmartListAdapterIndexScrolling adapterWithHolder = new SmartListAdapterIndexScrolling(getActivity(), R.layout.eosos_directory_list_item, listData, sections, alphaIndexer,
                sectionArray, new ItemView() {

            @Override
            public View setItemView(int position, View v, SmartListItem item, final ViewHolder holder) {

                holder.eososTxtTitle = (IjoomerTextView) v.findViewById(R.id.eososTxtTitle);
                holder.eososTxtLocation = (IjoomerTextView) v.findViewById(R.id.eososTxtLocation);
                holder.eososTxtDistance = (IjoomerTextView) v.findViewById(R.id.eososTxtDistance);
                holder.eososImgClub = (ImageView) v.findViewById(R.id.eososImgClub);
                holder.eososTxtCity = (IjoomerTextView) v.findViewById(R.id.eososTxtCity);
                holder.rtbRating = (IjoomerRatingBar) v.findViewById(R.id.rtbRating);
                holder.eososProgress = (ProgressBar) v.findViewById(R.id.progress);
                holder.divider = (View) v.findViewById(R.id.divider);
                holder.divider.setVisibility(View.VISIBLE);
                holder.rtbRating.setVisibility(View.GONE);
                holder.eososTxtDistance.setVisibility(View.GONE);
                holder.eososTxtCity.setVisibility(View.GONE);
                holder.eososProgress.setVisibility(View.GONE);

                if (item.getValues().size() > 1) {
                    holder.eososTxtCity.setText(item.getValues().get(1).toString());
                    holder.eososTxtCity.setVisibility(View.VISIBLE);
                    if (noOfCityMap.containsKey(item.getValues().get(1).toString()) && noOfCityMap.get(item.getValues().get(1).toString()) == 1) {
                        holder.divider.setVisibility(View.GONE);
                    }
                } else if ((position + 1) <= listData.size() - 1 && listData.get(position + 1).getValues().size() > 1) {
                    holder.divider.setVisibility(View.GONE);
                }

                if (position == listData.size() - 1) {
                    holder.divider.setVisibility(View.VISIBLE);
                }
                @SuppressWarnings("unchecked")
                HashMap<String, String> value = (HashMap<String, String>) item.getValues().get(0);

                holder.eososTxtLocation.setText(Html.fromHtml(value.get("city")).toString().trim());
                holder.eososTxtTitle.setText(value.get("title").trim());

                try {
                    holder.rtbRating.setStarRating(Float.parseFloat(value.get(AVERAGERATING)) / 2);
                    holder.rtbRating.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(value.get("image_thumb").trim().length()>0){
                    holder.eososProgress.setVisibility(View.GONE);
                    holder.eososImgClub.setImageBitmap(BitmapFactory.decodeFile(value.get("image_thumb")));
                }else{
                    holder.eososProgress.setVisibility(View.VISIBLE);
                    aQuery.ajax(value.get("image1"), Bitmap.class, 0, new AjaxCallback<Bitmap>() {
                        @Override
                        public void callback(String url, Bitmap object, AjaxStatus status) {
                            super.callback(url, object, status);
                            try{
                                String thumbPath = AQUtility.getCacheDir(getActivity()) + "/" + System.currentTimeMillis() + ".png";
                                final int THUMBNAIL_SIZE = 100;
                                object = Bitmap.createScaledBitmap(object, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                                FileOutputStream outputStream = new FileOutputStream(thumbPath);
                                object.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                dataProvider.createThumb(url, thumbPath);
                            }catch(Exception ex) {
                                ex.printStackTrace();
                            }
                            if (object != null)
                                object.recycle();
                            holder.eososProgress.setVisibility(View.GONE);
                        }
                });
            }
            //aQuery.id(holder.eososImgClub).image(aQuery.getCachedImage(value.get("image_thumb")));
//                try {
//                    if(aQuery.getCachedImage(value.get("image_thumb"))==null){
//                        holder.eososProgress.setVisibility(View.VISIBLE);
//                        aQuery.id(holder.eososImgClub).progress(holder.eososProgress).image(value.get("image1"),true,true);
//                    }else{
//                        holder.eososProgress.setVisibility(View.GONE);
//                        aQuery.id(holder.eososImgClub).image(aQuery.getCachedImage(value.get("image1")));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            return v;
        }

        @Override
        public View setItemView(int position, View v, SmartListItem item) {
            return null;
        }
    });
    return adapterWithHolder;
}

    @Override
    public View setLayoutView() {
        return null;
    }

    public ArrayList<HashMap<String, String>> getClubs() {
        return clubs;
    }

    public void hideSearchBar() {
        lnrSearch.setVisibility(View.GONE);
    }

    public void showSearchBar() {

        if (lnrSearch.getVisibility() == View.GONE) {
            lnrSearch.setVisibility(View.VISIBLE);
            edtClub.requestFocus();
            ((IjoomerSuperMaster) getActivity()).showSoftKeyboard();
        } else {
            ((IjoomerSuperMaster) getActivity()).hideSoftKeyboard();
            lnrSearch.setVisibility(View.GONE);
            edtClub.setText("");
            prepareList(clubs);
            listAdapterWithHolder = getListAdapter(listData);
            listDirectory.setAdapter(listAdapterWithHolder);
        }
    }

    private void applySearch() {

        for (HashMap<String, String> row : clubs) {

            if (row.get("title").toLowerCase().contains(edtClub.getText().toString().trim().toLowerCase())) {
                filteredList.add(row);
            }

        }
        if (filteredList.size() <= 0) {
            // show message
        } else {
            prepareList(clubs);
            listAdapterWithHolder = getListAdapter(listData);
            listDirectory.setAdapter(listAdapterWithHolder);
        }
    }
}
