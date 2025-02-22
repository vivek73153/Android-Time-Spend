package com.ijoomer.components.intafy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ijoomer.common.classes.IjoomerIntafyMaster;
import com.ijoomer.common.classes.IjoomerUtilities;
import com.ijoomer.common.classes.ViewHolder;
import com.ijoomer.common.configuration.IjoomerApplicationConfiguration;
import com.ijoomer.custom.interfaces.IjoomerSharedPreferences;
import com.ijoomer.customviews.IjoomerAudioPlayer;
import com.ijoomer.customviews.IjoomerTextView;
import com.ijoomer.customviews.IjoomerVoiceButton;
import com.ijoomer.intafy.src.R;
import com.ijoomer.library.intafy.IntafyMessageDataProvider;
import com.ijoomer.weservice.WebCallListenerWithCacheInfo;
import com.smart.framework.CustomAlertNeutral;
import com.smart.framework.ItemView;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartFragment;
import com.smart.framework.SmartListAdapterWithHolder;
import com.smart.framework.SmartListItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Fragment Contains All Method Related To JomAlbumAllFragment.
 * 
 * @author tasol
 * 
 */
@SuppressLint("ValidFragment")
public class IjoomerIntafyMessageDetailListFragment extends SmartFragment implements IntafyTagHolder,IjoomerSharedPreferences {

    private ListView lstArticle;
    private LinearLayout listFooter;

    private ArrayList<SmartListItem> listData = new ArrayList<SmartListItem>();
    private SmartListAdapterWithHolder listAdapterWithHolder;
    private IntafyMessageDataProvider messageDataProvider;
    private SeekBar seekBar;
    private String IN_MESSAGE_ID;
    private String IN_USER_ID;
    private String IN_USERNAME;

    private AQuery aQuery;
    private int getDataCount=0;
    public IjoomerIntafyMessageDetailListFragment(String IN_MESSAGE_ID,String IN_USER_ID,String IN_USERNAME) {
        this.IN_MESSAGE_ID = IN_MESSAGE_ID;
        this.IN_USER_ID = IN_USER_ID;
        this.IN_USERNAME = IN_USERNAME;
    }

    /**
     * Overrides method
     */

    @Override
    public int setLayoutId() {
        return R.layout.ijoomer_intafy_article_list_fragment;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {
        listFooter = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.ijoomer_list_footer, null);
        lstArticle = (ListView) currentView.findViewById(R.id.lstArticle);
        lstArticle.addFooterView(listFooter, null, false);
        lstArticle.setAdapter(null);
        messageDataProvider = new IntafyMessageDataProvider(getActivity());
        aQuery = new AQuery(getActivity());
    }

    @Override
    public void prepareViews(View currentView) {

        getArticlesList(true);
    }

    @Override
    public void setActionListeners(View currentView) {
        lstArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> row = (HashMap<String, String>) listData.get(position).getValues().get(0);
                if(row.get(BODY).contains("{image}")){
                    try{
                        ((SmartActivity) getActivity()).loadNew(IjoomerIntafyImageViewActivity.class,getActivity(),false,"IN_IMAGE",((IjoomerIntafyMaster)getActivity()).getImage(row.get(BODY)));
                    }catch (Throwable e){
                        e.printStackTrace();
                    }
                }
            }
        });
        lstArticle.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(getDataCount>1) {
                    if ((firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount > 1) {
                        if (!messageDataProvider.isCalling() && messageDataProvider.hasNextPage()) {
                            listFooterVisible();
                            String connectedUserId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_CONNECTEDUSERID, "0");
                            String networkId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_NETWORKID, "0");
                            getDataCount = 0;
                            messageDataProvider.getMessageDetailsList(IN_MESSAGE_ID, IN_USER_ID, networkId, connectedUserId, new WebCallListenerWithCacheInfo() {

                                @Override
                                public void onProgressUpdate(int progressCount) {
                                }

                                @Override
                                public void onCallComplete(int responseCode, String errorMessage, ArrayList<HashMap<String, String>> data1, Object data2, int pageNo, int pageLimit,
                                                           boolean fromCache) {
                                    try {
                                        listFooterInvisible();
                                        if (responseCode == 200) {
                                            prepareList(data1, true, fromCache, pageNo, pageLimit);
                                            if(fromCache){
                                                getDataCount++;
                                            }else{
                                                getDataCount+=2;
                                            }
                                        } else {
                                            responseErrorMessageHandler(responseCode);
                                        }
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * Class methods
     */

    /**
     * This method used to update fragment.
     */
    public void update() {
        getArticlesList(false);
    }

    /**
     * This method used to visible list footer
     */
    public void listFooterVisible() {
        listFooter.setVisibility(View.VISIBLE);
    }

    /**
     * This method used to gone list footer
     */
    public void listFooterInvisible() {
        listFooter.setVisibility(View.GONE);
    }


    /**
     * This method used to get all photos.
     * @param isShowProgress represented progress shown
     */
    private void getArticlesList(boolean isShowProgress){
        getDataCount=0;
        messageDataProvider.restorePagingSettings();
        if(isShowProgress){
            seekBar =  IjoomerUtilities.getLoadingDialog(getString(R.string.intafy_loading_sending_request));
        }
        String connectedUserId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_CONNECTEDUSERID,"0");
        String networkId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_NETWORKID,"0");
        messageDataProvider.getMessageDetailsList(IN_MESSAGE_ID,IN_USER_ID,networkId, connectedUserId, new WebCallListenerWithCacheInfo() {
            @Override
            public void onCallComplete(int responseCode, String errorMessage, ArrayList<HashMap<String, String>> data1, Object data2, int pageNo, int pageLimit, boolean fromCache) {
                try {
                    if (responseCode == 200) {
                        if (data1 != null) {
                            prepareList(data1, false, fromCache, pageNo, pageLimit);
                            listAdapterWithHolder = getListAdapter();
                            lstArticle.setAdapter(listAdapterWithHolder);
                            lstArticle.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            lstArticle.setStackFromBottom(true);
                            if(fromCache){
                                getDataCount++;
                            }else{
                                getDataCount+=2;
                            }
                        }
                    } else {
                        responseErrorMessageHandler(responseCode);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgressUpdate(int progressCount) {
                seekBar.setProgress(progressCount);
            }
        });
    }


    public void prepareList(ArrayList<HashMap<String, String>> data, boolean append, boolean fromCache, int pageno, int pagelimit) {
        if (data != null) {
            if (!append) {
                listData.clear();
            } else {
                if (!fromCache) {
                    int startIndex = ((pageno - 1) * pagelimit);
                    int endIndex = listAdapterWithHolder.getCount();
                    if (startIndex > 0) {
                        for (int i = endIndex; i > startIndex; i--) {
                            try {
                                listAdapterWithHolder.remove(listAdapterWithHolder.getItem(0));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }

            for (int i=0;i<data.size();i++){
                HashMap<String, String> hashMap = data.get(i);
                SmartListItem item = new SmartListItem();
                item.setItemLayout(R.layout.ijoomer_intafy_message_detail_list_item);
                ArrayList<Object> obj = new ArrayList<Object>();
                obj.add(hashMap);
                item.setValues(obj);
                if (append) {
                    listAdapterWithHolder.insert(item,0);
                } else {
                    listData.add(item);
                }
            }
        }
    }

    /**
     * This method used to shown response message.
     * @param responseCode represented response code
     */
    private void responseErrorMessageHandler(final int responseCode) {
        IjoomerUtilities.getCustomOkDialog(String.format(getString(R.string.intafy_messages_thread),IN_USERNAME), getString(getResources().getIdentifier("code" + responseCode, "string", getActivity().getPackageName())), getString(R.string.intafy_ok), R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

            @Override
            public void NeutralMethod() {

            }
        });
    }

    /**
     * List adapter for all album.
     */
    private SmartListAdapterWithHolder getListAdapter() {

        SmartListAdapterWithHolder adapterNetwork = new SmartListAdapterWithHolder(getActivity(), R.layout.ijoomer_intafy_message_detail_list_item, listData, new ItemView() {

            @SuppressWarnings("unchecked")
            @Override
            public View setItemView(int position, View v, SmartListItem item,final ViewHolder holder) {

                holder.lnrLeftMessage = (LinearLayout) v.findViewById(R.id.lnrLeftMessage);
                holder.txtLeftMessage = (IjoomerTextView) v.findViewById(R.id.txtLeftMessage);
                holder.txtLeftMessageDate = (IjoomerTextView) v.findViewById(R.id.txtLeftMessageDate);
                holder.relLeftImage = (RelativeLayout) v.findViewById(R.id.relLeftImage);
                holder.pbrLeftImageLoad = (ProgressBar) v.findViewById(R.id.pbrLeftImageLoad);
                holder.imgLeftMessage = (ImageView) v.findViewById(R.id.imgLeftMessage);
                holder.btnLeftSentMessagePlayVoice = (IjoomerVoiceButton) v.findViewById(R.id.btnLeftSentMessagePlayVoice);

                holder.lnrRightMessage = (LinearLayout) v.findViewById(R.id.lnrRightMessage);
                holder.txtRightMessage = (IjoomerTextView) v.findViewById(R.id.txtRightMessage);
                holder.txtRightMessageDate = (IjoomerTextView) v.findViewById(R.id.txtRightMessageDate);
                holder.relRightImage = (RelativeLayout) v.findViewById(R.id.relRightImage);
                holder.pbrRightImageLoad = (ProgressBar) v.findViewById(R.id.pbrRightImageLoad);
                holder.imgRightMessage = (ImageView) v.findViewById(R.id.imgRightMessage);
                holder.btnRightSentMessagePlayVoice = (IjoomerVoiceButton) v.findViewById(R.id.btnRightSentMessagePlayVoice);


                final HashMap<String, String> row = (HashMap<String, String>) item.getValues().get(0);

                if(row.get(OUTGOING).equals("0")){
                    holder.lnrLeftMessage.setVisibility(View.VISIBLE);
                    holder.lnrRightMessage.setVisibility(View.GONE);
                    if(row.get(BODY).contains("{image}")){
                        holder.txtLeftMessage.setVisibility(View.GONE);
                        holder.relLeftImage.setVisibility(View.VISIBLE);
                        holder.btnLeftSentMessagePlayVoice.setVisibility(View.GONE);
                        aQuery.id(holder.imgLeftMessage).progress(holder.pbrLeftImageLoad).image(((IjoomerIntafyMaster)getActivity()).getImage(row.get(BODY)),true,true,((SmartActivity)getActivity()).getDeviceWidth(),0);
                    }else if(((IjoomerIntafyMaster)getActivity()).getPlainText(row.get(BODY)).trim().length()>0){
                        holder.relLeftImage.setVisibility(View.GONE);
                        holder.txtLeftMessage.setText(((IjoomerIntafyMaster)getActivity()).getPlainText(row.get(BODY)));
                        holder.btnLeftSentMessagePlayVoice.setVisibility(View.GONE);
                        holder.txtLeftMessage.setVisibility(View.VISIBLE);
                    }else if (((IjoomerIntafyMaster)getActivity()).getAudio(row.get(BODY)) != null) {
                        holder.relLeftImage.setVisibility(View.GONE);
                        holder.txtLeftMessage.setVisibility(View.GONE);
                        holder.btnLeftSentMessagePlayVoice.setVisibility(View.VISIBLE);
                        holder.btnLeftSentMessagePlayVoice.setAudioPath(((IjoomerIntafyMaster)getActivity()).getAudio(row.get(BODY)), false);
                        holder.btnLeftSentMessagePlayVoice.setAudioListener(new IjoomerAudioPlayer.AudioListener() {

                            @Override
                            public void onReportClicked() {
                            }

                            @Override
                            public void onPrepared() {
                            }

                            @Override
                            public void onPlayClicked(boolean isplaying) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });

                    }

                    holder.txtLeftMessageDate.setText(IjoomerUtilities.convertDateStringFormat(row.get(DATE), "yyyy-MM-dd kk:mm", IjoomerApplicationConfiguration.dateTimeFormat));
                }else{
                    holder.lnrRightMessage.setVisibility(View.VISIBLE);
                    holder.lnrLeftMessage.setVisibility(View.GONE);
                    if(row.get(BODY).contains("{image}")){
                        holder.txtRightMessage.setVisibility(View.GONE);
                        holder.relRightImage.setVisibility(View.VISIBLE);
                        holder.btnRightSentMessagePlayVoice.setVisibility(View.GONE);
                        aQuery.id(holder.imgRightMessage).progress(holder.pbrRightImageLoad).image(((IjoomerIntafyMaster)getActivity()).getImage(row.get(BODY)),true,true,((SmartActivity)getActivity()).getDeviceWidth(),0);
                    }else if(((IjoomerIntafyMaster)getActivity()).getPlainText(row.get(BODY)).trim().length()>0){
                        holder.relRightImage.setVisibility(View.GONE);
                        holder.txtRightMessage.setVisibility(View.VISIBLE);
                        holder.btnRightSentMessagePlayVoice.setVisibility(View.GONE);;
                        holder.txtRightMessage.setText(((IjoomerIntafyMaster)getActivity()).getPlainText(row.get(BODY)));
                    }else if (((IjoomerIntafyMaster)getActivity()).getAudio(row.get(BODY)) != null) {
                        holder.relRightImage.setVisibility(View.GONE);
                        holder.txtRightMessage.setVisibility(View.GONE);
                        holder.btnRightSentMessagePlayVoice.setVisibility(View.VISIBLE);
                        holder.btnRightSentMessagePlayVoice.setAudioPath(((IjoomerIntafyMaster)getActivity()).getAudio(row.get(BODY)), false);
                        holder.btnRightSentMessagePlayVoice.setAudioListener(new IjoomerAudioPlayer.AudioListener() {

                            @Override
                            public void onReportClicked() {
                            }

                            @Override
                            public void onPrepared() {
                            }

                            @Override
                            public void onPlayClicked(boolean isplaying) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });

                    }
                    holder.txtRightMessageDate.setText(IjoomerUtilities.convertDateStringFormat(row.get(DATE), "yyyy-MM-dd kk:mm", IjoomerApplicationConfiguration.dateTimeFormat));
                }

                return v;
            }

            @Override
            public View setItemView(int position, View v, SmartListItem item) {
                return null;
            }
        });
        return adapterNetwork;
    }

}
