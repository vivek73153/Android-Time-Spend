package com.eosos.library;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.eosos.caching.IjoomerCaching;
import com.eosos.caching.IjoomerCachingInsertListener;
import com.eosos.common.classes.IjoomerPagingProvider;
import com.eosos.common.classes.IjoomerUtilities;
import com.eosos.common.configuration.IjoomerApplicationConfiguration;
import com.eosos.components.main.EososTagHolder;
import com.eosos.weservice.IjoomerWebService;
import com.eosos.weservice.ProgressListener;
import com.eosos.weservice.WebCallListener;
import com.eosos.weservice.WebCallListenerWithCacheInfo;

public class EososDataProvider extends IjoomerPagingProvider implements EososTagHolder {

    private Context mContext;
    private boolean isCalling = false;
    private final String ISOBIPRO = "isobipro";
    private final String SECTON_CATEGORIES = "sectionCategories";
    private final String SECTION_ID = "sectionID";
    private final String CAT_ID = "categoryID";
    private final String FEATUREDFIRST = "featuredFirst";
    private final String ISINTIALREQUEST = "isIntialRequest";
    private final String LASTREQUESTTIME = "lastRequestTime";
    private final String ENTRYID = "entryID";
    private final String DEVICEID = "deviceID";
    private final String CLUBS = "Clubs";
    private final String CLUBFIELDS = "ClubFields";
    private AQuery aQuery;
    int count = 0;
    boolean imageLoadingProblem = false;

    public interface ImageDownloadListener {
        void onImgeDownload(int total, int countProgress);

        void onTaskComplete();
    }

    public EososDataProvider(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        aQuery = new AQuery(mContext);
    }

    public boolean isFavourite(String id) {
        try {
            ArrayList<HashMap<String, String>> data = new IjoomerCaching(mContext).getDataFromCache(FAVOURITE, "select * from '" + FAVOURITE + "' where id='" + id
                    + "' order by rowid");
            if (data != null && data.size() > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public ArrayList<HashMap<String, String>> getFavouritesList() {

        return new IjoomerCaching(mContext).getDataFromCache(CLUBS, "select * from '" + CLUBS + "' where isFavorite='1' order by lower(city),lower(title)");

    }

    public ArrayList<HashMap<String, String>> getClubList() {
        return new IjoomerCaching(mContext).getDataFromCache(CLUBS, "SELECT * from '" + CLUBS + "' order by lower(city),lower(title)");
    }



    public ArrayList<HashMap<String, String>> getEntryDetailsFromDb(String id) {
        return new IjoomerCaching(mContext).getDataFromCache(CLUBS, "select * from Clubs where id='" + id + "' order by rowid");
    }

    public void addFavorite(final String entryID, final String deviceID, final WebCallListener target) {
        final JSONObject taskData = new JSONObject();
        new AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>() {

            @Override
            protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
                IjoomerWebService iw = new IjoomerWebService();
                iw.reset();
                iw.addWSParam(EXTNAME, SOBIPRO);
                iw.addWSParam(EXTVIEW, "isobipro");
                iw.addWSParam(EXTTASK, "addFavorite");

                try {
                    taskData.put(ENTRYID, entryID);
                    taskData.put(DEVICEID, deviceID);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                iw.addWSParam(TASKDATA, taskData);

                iw.WSCall(new ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        if (num >= 100) {
                            target.onProgressUpdate(95);
                        } else {
                            target.onProgressUpdate((int) num);
                        }
                    }
                });

                if (validateResponse(iw.getJsonObject())) {
                    return new IjoomerCaching(mContext).parseData(iw.getJsonObject());
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
                super.onPostExecute(result);
                target.onProgressUpdate(100);
                if (getResponseCode() == 200 || getResponseCode() == 599)
                    addToFavorite(entryID);
                target.onCallComplete(getResponseCode(), getErrorMessage(), result, null);
            }
        }.execute();

    }

    public void removeFavorite(final String entryID, final String deviceID, final WebCallListener target) {
        final JSONObject taskData = new JSONObject();
        new AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>() {

            @Override
            protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
                IjoomerWebService iw = new IjoomerWebService();
                iw.reset();
                iw.addWSParam(EXTNAME, SOBIPRO);
                iw.addWSParam(EXTVIEW, "isobipro");
                iw.addWSParam(EXTTASK, "removeFavorite");

                try {
                    taskData.put(ENTRYID, entryID);
                    taskData.put(DEVICEID, deviceID);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                iw.addWSParam(TASKDATA, taskData);

                iw.WSCall(new ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        if (num >= 100) {
                            target.onProgressUpdate(95);
                        } else {
                            target.onProgressUpdate((int) num);
                        }
                    }
                });

                if (validateResponse(iw.getJsonObject())) {
                    return new IjoomerCaching(mContext).parseData(iw.getJsonObject());
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
                super.onPostExecute(result);
                target.onProgressUpdate(100);
                if (getResponseCode() == 200 || getResponseCode() == 599)
                    removeFromFavorite(entryID);
                target.onCallComplete(getResponseCode(), getErrorMessage(), result, null);
            }
        }.execute();

    }


    public void addToFavorite(String entryId){
        try{
            new IjoomerCaching(mContext).updateTable("update "+CLUBS+" set isFavorite='1' where id='"+entryId+"'");
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    public void removeFromFavorite(String entryId){
        new IjoomerCaching(mContext).updateTable("update "+CLUBS+" set isFavorite='0' where id='"+entryId+"'");
    }

    public void getEntryDetail(final String deviceId,final String Sectionid, final String id, final WebCallListenerWithCacheInfo target) {
        new AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>() {
            IjoomerWebService iw = new IjoomerWebService();

            protected void onPreExecute() {
            }

            @Override
            protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

                iw.reset();
                iw.addWSParam(EXTNAME, SOBIPRO);
                iw.addWSParam(EXTVIEW, ISOBIPRO);
                iw.addWSParam(EXTTASK, "getDetailInfo");

                JSONObject taskData = new JSONObject();
                try {
                    taskData.put(DEVICEID, deviceId);
                    taskData.put("id", id);
                    taskData.put(SECTION_ID, Sectionid);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                iw.addWSParam(TASKDATA, taskData);
                iw.WSCall(new ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        if (num >= 100) {
                            target.onProgressUpdate(95);
                        } else {
                            target.onProgressUpdate((int) num);
                        }
                    }
                });

                if (validateResponse(iw.getJsonObject())) {
                    try {
                        if (iw.getJsonObject().getJSONArray("entries") != null) {
                            IjoomerCaching caching = new IjoomerCaching(mContext);
                            caching.addExtraColumn("image1","");
                            caching.addExtraColumn("image_thumb","");
                            caching.setCachingInsertListener(new IjoomerCachingInsertListener() {
                                @Override
                                public void onBeforeInsert(ContentValues dataToInsert) {
                                    if(dataToInsert.containsKey("field")){
                                        try{
                                            JSONArray jsonArray = new JSONArray(dataToInsert.getAsString("field"));
                                            for (int j = 0; j < jsonArray.length(); j++) {
                                                if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_image1")) {
                                                    dataToInsert.put("image1", jsonArray.getJSONObject(j).get("value").toString());
                                                    break;
                                                }

                                            }
                                        }catch (Throwable e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            return caching.cacheData(iw.getJsonObject().getJSONArray("entries"), false, CLUBS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
                super.onPostExecute(result);
                target.onProgressUpdate(100);
                target.onCallComplete(getResponseCode(), getErrorMessage(), result, null, getPageNo() - 1, getPageLimit(), false);
            }
        }.execute();

    }


    public ArrayList<HashMap<String, String>> getPlannerList(final String city, final String night) {
        return new IjoomerCaching(mContext).getDataFromCache(CLUBS,
                "select * from Clubs where id in (select id from ClubFields where id in (select  id from ClubFields where  labelid='" + night.toLowerCase()
                        + "' and lower(value)='yes') and lower(value) ='" + city.toLowerCase() + "')");
    }

    public boolean isClubOpen(String id, String today) {
        try {
            ArrayList<HashMap<String, String>> array = new IjoomerCaching(mContext).getDataFromCache(CLUBFIELDS, "select value from ClubFields where id='" + id
                    + "' and labelid='field_" + today + "'");
            if (array.get(0).get("value").equalsIgnoreCase("yes"))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> getCityList() {
        return new IjoomerCaching(mContext).getDataFromCache(CLUBFIELDS, "SELECT distinct(value) FROM ClubFields where labelid='field_city'");
    }




    /**
     * This method used to check provider execute any request call.
     *
     * @return {@link boolean}
     */
    public boolean isCalling() {
        return isCalling;
    }

    public void getClubs(final String deviceId,final String sectionID, final String catID, final String featuredFirst, final String isIntialRequest,
                         final String lastRequestTime, final WebCallListener target) {
        if (hasNextPage()) {
            isCalling = true;
            new AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>() {

                @Override
                protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
                    IjoomerWebService iw = new IjoomerWebService();
                    iw.reset();
                    iw.addWSParam(EXTNAME, SOBIPRO);
                    iw.addWSParam(EXTVIEW, ISOBIPRO);
                    iw.addWSParam(EXTTASK, SECTON_CATEGORIES);

                    JSONObject taskData = new JSONObject();

                    try {
                        taskData.put(DEVICEID,deviceId);
                        taskData.put(SECTION_ID, sectionID);
                        taskData.put(CAT_ID, catID);
                        taskData.put(PAGENO, getPageNo());
                        taskData.put(FEATUREDFIRST, featuredFirst);
                        taskData.put(ISINTIALREQUEST, isIntialRequest);
                        taskData.put(LASTREQUESTTIME, lastRequestTime);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    iw.addWSParam(TASKDATA, taskData);
                    iw.WSCall(new ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            if (num >= 100) {
                                target.onProgressUpdate(70);
                            } else {
                                target.onProgressUpdate((int) num);
                            }
                        }
                    });

                    target.onProgressUpdate(80);
                    if (validateResponse(iw.getJsonObject())) {
                        try {
                            setPagingParams(Integer.parseInt(iw.getJsonObject().getString(PAGELIMIT)), Integer.parseInt(iw.getJsonObject().getString(TOTAL)));
                            iw.getJsonObject().remove(TOTAL);
                            target.onProgressUpdate(80);
                            try {
                                if (iw.getJsonObject().getJSONArray("entries")!=null) {
                                    target.onProgressUpdate(90);
                                    IjoomerCaching caching = new IjoomerCaching(mContext);
                                    caching.addExtraColumn("image1","");
                                    caching.addExtraColumn("image_thumb","");
                                    caching.setCachingInsertListener(new IjoomerCachingInsertListener() {
                                        @Override
                                        public void onBeforeInsert(ContentValues dataToInsert) {
                                            if(dataToInsert.containsKey("field")){
                                                try{
                                                    JSONArray jsonArray = new JSONArray(dataToInsert.getAsString("field"));
                                                    for (int j = 0; j < jsonArray.length(); j++) {
                                                        if (jsonArray.getJSONObject(j).get("labelid").toString().equalsIgnoreCase("field_image1")) {
                                                            dataToInsert.put("image1", jsonArray.getJSONObject(j).get("value").toString());
                                                            break;
                                                        }

                                                    }
                                                }catch (Throwable e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                    caching.cacheData(iw.getJsonObject().getJSONArray("entries"), false, CLUBS);
                                    if (iw.getJsonObject().has("deleted_id")) {
                                        removeDeletedEntries(iw.getJsonObject().getString("deleted_id"));
                                    }
                                    target.onProgressUpdate(90);
                                    return new IjoomerCaching(mContext).getDataFromCache(CLUBS, "select * from '" + CLUBS + "' order by rowid");
                                }
                            } catch (Throwable e) {
                                if (iw.getJsonObject().has("deleted_id")) {
                                    removeDeletedEntries(iw.getJsonObject().getString("deleted_id"));
                                }
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
                    super.onPostExecute(result);
                    target.onProgressUpdate(100);
                    target.onCallComplete(getResponseCode(), getErrorMessage(), result, null);
                    isCalling = false;
                }

            }.execute();
        } else {
            isCalling = false;
            target.onCallComplete(getResponseCode(), getErrorMessage(), null, null);
            target.onProgressUpdate(100);
        }
    }

    private void removeDeletedEntries(String deletedId) {
        IjoomerCaching caching = new IjoomerCaching(mContext);
        String[] deletedArray = deletedId.split(",");
        String deleteQuery = "";
        for (int i = 0; i < deletedArray.length; i++) {
            deleteQuery += " id='" + deletedArray[i] + "' or";
        }
        deleteQuery = deleteQuery.substring(0, deleteQuery.length() - 3);
        caching.deleteDataFromCache("delete from " + CLUBS + " where " + deleteQuery);
    }


    public ArrayList<HashMap<String,String>> getNearBy(String latitude,String longitude,double nearMiles){
       IjoomerCaching caching = new IjoomerCaching(mContext);
        ArrayList<HashMap<String,String>> clubs = caching.getDataFromCache(CLUBS);
        ArrayList<HashMap<String,String>> nears = new ArrayList<HashMap<String, String>>();
        for (HashMap<String,String> club : clubs){
            double miles;
            double km;
            float[] dist = new float[1];
            Location.distanceBetween(Double.parseDouble(latitude),Double.parseDouble(longitude),Double.parseDouble(club.get(LATITUDE)),Double.parseDouble(club.get(LONGITUDE)), dist);
            miles = (dist[0] * 0.00062137119);
            km = (dist[0] * 0.001);

            club.put("distanceMiles",String.format("%.1f", miles));
            club.put("distanceKm",String.format("%.1f", km));

            if(Math.round(miles) <= Math.round(nearMiles)){
                nears.add(club);
            }
        }
        Collections.sort(nears, new MapComparator("distanceMiles"));
        return nears;

    }

    class MapComparator implements Comparator<Map<String, String>> {
        private final String key;

        public MapComparator(String key){
            this.key = key;
        }

        public int compare(Map<String, String> first,
                           Map<String, String> second){
            double firstValue = Double.parseDouble(first.get(key));
            double secondValue = Double.parseDouble(second.get(key));
            return firstValue < secondValue ? -1 : firstValue > secondValue ? 1 : 0;
        }
    }

    public void startDownloadEntryImages(final ImageDownloadListener listener) {

        if (IjoomerUtilities.isNetwokReachable()) {

            IjoomerCaching caching = new IjoomerCaching(mContext);
            ArrayList<HashMap<String, String>> entries = caching.getDataFromCache(CLUBFIELDS,
                    "SELECT value FROM ClubFields where labelid like 'field_image1' or labelid like 'field_image2' or labelid like 'field_image3'");
            final List<String> imagesList = new LinkedList<String>();

            for (HashMap<String, String> row : entries) {

                if (row.get("value").trim().length() > 0) {
                    imagesList.add(row.get("value").trim());
                }
            }

            for (String url : imagesList) {

                aQuery.ajax(url.trim(), Bitmap.class, 0, new AjaxCallback<Bitmap>() {
                    @Override
                    public void callback(String url, Bitmap object, AjaxStatus status) {
                        super.callback(url, object, status);

                        if (status.getCode() != 200 && (!imageLoadingProblem)) {
                            imageLoadingProblem = true;
                            listener.onImgeDownload(imagesList.size(), -1);
                        } else {
                            try
                            {
                                if(!isThumbCreate(url)) {
                                    String thumbPath = AQUtility.getCacheDir(mContext) + "/" + System.currentTimeMillis() + ".png";
                                    final int THUMBNAIL_SIZE = 100;
                                    object = Bitmap.createScaledBitmap(object, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                                    FileOutputStream outputStream = new FileOutputStream(thumbPath);
                                    object.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                    createThumb(url, thumbPath);
                                }
                            }
                            catch(Exception ex) {
                                ex.printStackTrace();
                            }
                            count++;
                            System.out.println("ImageDownload : " + count);
                            listener.onImgeDownload(imagesList.size(), count);
                            if (count == imagesList.size()) {
                                listener.onTaskComplete();
                            }
                            if (object != null)
                                object.recycle();
                        }

                    }
                });
            }

        } else {
            listener.onImgeDownload(0, -1);
        }
    }

    public void createThumb(String url,String imageThumbPath){
        try{
            new IjoomerCaching(mContext).updateTable("update "+CLUBS+" set image_thumb='"+imageThumbPath+"' where image1='"+url+"'");
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    private boolean isThumbCreate(String url){
        try{
            IjoomerCaching caching = new IjoomerCaching(mContext);
            HashMap<String,String> data = caching.getDataFromCache(CLUBS,"select image_thumb from '"+CLUBS+"' where image1='"+url+"'").get(0);
            if(data.get("image_thumb").trim().length()>0){
                return true;
            }else{
                return false;
            }
        }catch (Throwable e){
            e.printStackTrace();
            return false;
        }
    }

}
