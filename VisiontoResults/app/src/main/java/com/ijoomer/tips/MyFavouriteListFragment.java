package com.ijoomer.tips;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ijoomer.tips.customviews.CustomBoldTextView;
import com.ijoomer.tips.customviews.CustomTextView;
import com.ijoomer.tips.database.DataBaseProvider;
import com.ijoomer.tips.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;


public class MyFavouriteListFragment extends ListFragment {

    private View view;
    private DataBaseProvider dataBaseProvider;
    private FavouriteAdapter adapter;
    private ArrayList<HashMap<String,String>> favouriteList;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myfavourite_fragment,null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));

        dataBaseProvider = new DataBaseProvider(getActivity());
        getFavourites();
        return view;
    }


    private void getFavourites(){
        ((MyFavouriteActivity)getActivity()).showProgressBar();
        new AsyncTask<Void, Void, ArrayList<HashMap<String,String>>>() {
            @Override
            protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
                ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
                dataBaseProvider.open();
                try {
                    data = dataBaseProvider.getFavourites();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(ArrayList<HashMap<String,String>> response) {
                super.onPostExecute(response);
                favouriteList = response;
                dataBaseProvider.close();
                ((MyFavouriteActivity) getActivity()).hideProgressBar();
                if(favouriteList!=null && favouriteList.size()>0){
                    adapter = new FavouriteAdapter(getActivity(),favouriteList);
                    getListView().setAdapter(adapter);
                    ((MyFavouriteActivity)getActivity()).hideProgressBar();

                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((MyFavouriteActivity)getActivity()).gotoTipDetail(Integer.parseInt(favouriteList.get(position).get(Utility.SUITEID)),Integer.parseInt(favouriteList.get(position).get(Utility.DRIVERID)), favouriteList.get(position).get(Utility.SUITENAME), favouriteList.get(position).get(Utility.DRIVERNAME), Integer.parseInt(favouriteList.get(position).get(Utility.TIPID)), favouriteList.get(position).get(Utility.TIPNAME), favouriteList.get(position).get(Utility.TIPNOTE),Integer.parseInt(favouriteList.get(position).get(Utility.TIPFAVOURITE)),favouriteList.get(position).get(Utility.TIPQUESTIONONE),favouriteList.get(position).get(Utility.TIPQUESTIONTWO),favouriteList.get(position).get(Utility.TIPQUESTIONTHREE));
                        }
                    });
                }else{
                    ((MyFavouriteActivity) getActivity()).successPopupWindow.showAtLocation(((MyFavouriteActivity) getActivity()).container, Gravity.NO_GRAVITY, 0, 0);
                }
            }
        }.execute();
    }

    class FavouriteAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String,String>> favouriteList;
        public FavouriteAdapter(Context context,ArrayList<HashMap<String,String>> favouriteList){
            this.context=context;
            this.favouriteList =favouriteList;
        }

        @Override
        public int getCount() {
            return favouriteList.size();
        }

        @Override
        public Object getItem(int position) {
            return favouriteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.myfavourite_list_item,null);
                holder.txtTipName = (CustomBoldTextView) convertView.findViewById(R.id.txtTipName);
                holder.txtSuiteDriverName = (CustomTextView) convertView.findViewById(R.id.txtSuiteDriverName);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtTipName.setText(favouriteList.get(position).get(Utility.TIPNAME));
            if(favouriteList.get(position).get(Utility.SUITEID).equals("1")){
                holder.txtSuiteDriverName.setText(Html.fromHtml("<font color='#ee2b7b'>" + favouriteList.get(position).get(Utility.SUITENAME) + " / </font>" + "<font color='#60605b'>" + favouriteList.get(position).get(Utility.DRIVERNAME) + "</font>"));
            }else if(favouriteList.get(position).get(Utility.SUITEID).equals("2")){
                holder.txtSuiteDriverName.setText(Html.fromHtml("<font color='#009bdf'>" + favouriteList.get(position).get(Utility.SUITENAME) + " / </font>" + "<font color='#60605b'>" + favouriteList.get(position).get(Utility.DRIVERNAME) + "</font>"));
            }else if(favouriteList.get(position).get(Utility.SUITEID).equals("3")){
                holder.txtSuiteDriverName.setText(Html.fromHtml("<font color='#3daf2c'>" + favouriteList.get(position).get(Utility.SUITENAME) + " / </font>" + "<font color='#60605b'>" + favouriteList.get(position).get(Utility.DRIVERNAME) + "</font>"));
            }else{
                holder.txtSuiteDriverName.setText(Html.fromHtml("<font color='#f7931e'>" + favouriteList.get(position).get(Utility.SUITENAME) + " / </font>" + "<font color='#60605b'>" + favouriteList.get(position).get(Utility.DRIVERNAME) + "</font>"));
            }

            return convertView;
        }

        class ViewHolder{
            CustomBoldTextView txtTipName;
            CustomTextView txtSuiteDriverName;
        }
    }
}
