package com.mycadiz.media.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.mycadiz.customviews.IjoomerTextView;

/**
 * <p>
 * Activity that will play a video from YouTube. A specific video or the latest
 * video in a YouTube playlist can be specified in the intent used to invoke
 * this activity. The data of the intent can be set to a specific video by using
 * an Intent data URL of the form:
 * </p>
 * <p/>
 * 
 * <pre>
 *     ytv://videoid
 * </pre>
 * <p/>
 * <p>
 * where
 * 
 * <pre>
 * videoid
 * </pre>
 * 
 * is the ID of the YouTube video to be played.
 * </p>
 * <p/>
 * <p>
 * If the user wishes to play the latest video in a YouTube playlist, the Intent
 * data URL should be of the form:
 * </p>
 * <p/>
 * 
 * <pre>
 *     ytpl://playlistid
 * </pre>
 * <p/>
 * <p>
 * where
 * 
 * <pre>
 * playlistid
 * </pre>
 * 
 * is the ID of the YouTube playlist from which the latest video is to be
 * played.
 * </p>
 * <p/>
 * <p>
 * Code used to invoke this intent should look something like the following:
 * </p>
 * <p/>
 * 
 * <pre>
 * Intent lVideoIntent = new Intent(null, Uri.parse(&quot;ytpl://&quot; + YOUTUBE_PLAYLIST_ID), this, OpenYouTubePlayerActivity.class);
 * startActivity(lVideoIntent);
 * </pre>
 * <p/>
 * <p>
 * There are several messages that are displayed to the user during various
 * phases of the video load process. If you wish to supply text other than the
 * default english messages (e.g., internationalization, etc.), you can pass the
 * text to be used via the Intent's extended data. The messages that can be
 * customized include:
 * <p/>
 * <ul>
 * <li>com.keyes.video.msg.init - activity is initializing.</li>
 * <li>com.keyes.video.msg.detect - detecting the bandwidth available to
 * download video.</li>
 * <li>com.keyes.video.msg.playlist - getting latest video from playlist.</li>
 * <li>com.keyes.video.msg.token - retrieving token from YouTube.</li>
 * <li>com.keyes.video.msg.loband - buffering low-bandwidth.</li>
 * <li>com.keyes.video.msg.hiband - buffering hi-bandwidth.</li>
 * <li>com.keyes.video.msg.error.title - dialog title displayed if anything goes
 * wrong.</li>
 * <li>com.keyes.video.msg.error.msg - message displayed if anything goes wrong.
 * </li>
 * </ul>
 * <p/>
 * <p>
 * For example:
 * </p>
 * <p/>
 * 
 * <pre>
 *      Intent lVideoIntent = new Intent(null, Uri.parse("ytpl://"+YOUTUBE_PLAYLIST_ID), this, OpenYouTubePlayerActivity.class);
 *      lVideoIntent.putExtra("com.keyes.video.msg.init", getString("str_video_intro"));
 *      lVideoIntent.putExtra("com.keyes.video.msg.detect", getString("str_video_detecting_bandwidth"));
 *      ...
 *      startActivity(lVideoIntent);
 * </pre>
 * 
 * @author David Keyes
 */
public class IjoomerMediaPlayer extends Activity {

	public static final String SCHEME_YOUTUBE_VIDEO = "ytv";
	public static final String SCHEME_YOUTUBE_PLAYLIST = "ytpl";
	public static final String SCHEME_FILE = "file";
	public static final String SCHEME_MP4_VIDEO = "mp4";

	static final String YOUTUBE_VIDEO_INFORMATION_URL = "http://www.youtube.com/get_video_info?&video_id=";
	static final String YOUTUBE_PLAYLIST_ATOM_FEED_URL = "http://gdata.youtube.com/feeds/api/playlists/";

	protected ProgressBar mProgressBar;
	protected IjoomerTextView mProgressMessage;
	protected VideoView mVideoView;

	public final static String MSG_INIT = "com.keyes.video.msg.init";
	protected String mMsgInit = "Initializing";

	public final static String MSG_DETECT = "com.keyes.video.msg.detect";
	protected String mMsgDetect = "Detecting Bandwidth";

	public final static String MSG_PLAYLIST = "com.keyes.video.msg.playlist";
	protected String mMsgPlaylist = "Determining Latest Video in YouTube Playlist";

	public final static String MSG_TOKEN = "com.keyes.video.msg.token";
	protected String mMsgToken = "Retrieving YouTube Video Token";

	public final static String MSG_LO_BAND = "com.keyes.video.msg.loband";
	protected String mMsgLowBand = "Buffering Low-bandwidth Video";

	public final static String MSG_HI_BAND = "com.keyes.video.msg.hiband";
	protected String mMsgHiBand = "Buffering High-bandwidth Video";

	public final static String MSG_ERROR_TITLE = "com.keyes.video.msg.error.title";
	protected String mMsgErrorTitle = "Communications Error";

	public final static String MSG_ERROR_MSG = "com.keyes.video.msg.error.msg";
	protected String mMsgError = "An error occurred during the retrieval of the video.  This could be due to network issues or YouTube protocols.  Please try again later.";

	/**
	 * Background task on which all of the interaction with YouTube is done
	 */
	protected QueryYouTubeTask mQueryYouTubeTask;

	protected String mVideoId = null;

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// create the layout of the view
		setupView();

		// determine the messages to be displayed as the view loads the video
		extractMessages();

		// set the flag to keep the screen ON so that the video can play without
		// the screen being turned off
		getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mProgressBar.bringToFront();
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressMessage.setText(mMsgInit);

		// extract the playlist or video id from the intent that started this
		// video

		Uri lVideoIdUri = this.getIntent().getData();

		if (lVideoIdUri == null) {
			Log.i(this.getClass().getSimpleName(), "No video ID was specified in the intent.  Closing video activity.");
			finish();
		}
		String lVideoSchemeStr = lVideoIdUri.getScheme();
		String lVideoIdStr = lVideoIdUri.getEncodedSchemeSpecificPart();
		if (lVideoIdStr == null) {
			Log.i(this.getClass().getSimpleName(), "No video ID was specified in the intent.  Closing video activity.");
			finish();
		}
		if (lVideoIdStr.startsWith("//")) {
			if (lVideoIdStr.length() > 2) {
				lVideoIdStr = lVideoIdStr.substring(2);
			} else {
				Log.i(this.getClass().getSimpleName(), "No video ID was specified in the intent.  Closing video activity.");
				finish();
			}
		}

		// /////////////////
		// extract either a video id or a playlist id, depending on the uri
		// scheme
		YouTubeId lYouTubeId = null;
		if (lVideoSchemeStr != null && lVideoSchemeStr.equalsIgnoreCase(SCHEME_YOUTUBE_PLAYLIST)) {
			lYouTubeId = new PlaylistId(lVideoIdStr);
		} else if (lVideoSchemeStr != null && lVideoSchemeStr.equalsIgnoreCase(SCHEME_YOUTUBE_VIDEO)) {
			lYouTubeId = new VideoId(lVideoIdStr);
		} else if (lVideoSchemeStr != null && lVideoSchemeStr.equalsIgnoreCase(SCHEME_FILE)) {
			lYouTubeId = new FileId(lVideoIdStr);
		} else if (lVideoSchemeStr != null && lVideoSchemeStr.equalsIgnoreCase(SCHEME_MP4_VIDEO)) {
			lYouTubeId = new FileId(lVideoIdStr);
		}

		if (lYouTubeId == null) {
			Log.i(this.getClass().getSimpleName(), "Unable to extract video ID from the intent.  Closing video activity.");
			finish();
		}

		mQueryYouTubeTask = (QueryYouTubeTask) new QueryYouTubeTask().execute(lYouTubeId);
	}

	/**
	 * Determine the messages to display during video load and initialization.
	 */
	private void extractMessages() {
		Intent lInvokingIntent = getIntent();
		String lMsgInit = lInvokingIntent.getStringExtra(MSG_INIT);
		if (lMsgInit != null) {
			mMsgInit = lMsgInit;
		}
		String lMsgDetect = lInvokingIntent.getStringExtra(MSG_DETECT);
		if (lMsgDetect != null) {
			mMsgDetect = lMsgDetect;
		}
		String lMsgPlaylist = lInvokingIntent.getStringExtra(MSG_PLAYLIST);
		if (lMsgPlaylist != null) {
			mMsgPlaylist = lMsgPlaylist;
		}
		String lMsgToken = lInvokingIntent.getStringExtra(MSG_TOKEN);
		if (lMsgToken != null) {
			mMsgToken = lMsgToken;
		}
		String lMsgLoBand = lInvokingIntent.getStringExtra(MSG_LO_BAND);
		if (lMsgLoBand != null) {
			mMsgLowBand = lMsgLoBand;
		}
		String lMsgHiBand = lInvokingIntent.getStringExtra(MSG_HI_BAND);
		if (lMsgHiBand != null) {
			mMsgHiBand = lMsgHiBand;
		}
		String lMsgErrTitle = lInvokingIntent.getStringExtra(MSG_ERROR_TITLE);
		if (lMsgErrTitle != null) {
			mMsgErrorTitle = lMsgErrTitle;
		}
		String lMsgErrMsg = lInvokingIntent.getStringExtra(MSG_ERROR_MSG);
		if (lMsgErrMsg != null) {
			mMsgError = lMsgErrMsg;
		}
	}

	/**
	 * Create the view in which the video will be rendered.
	 */
	private void setupView() {
		LinearLayout lLinLayout = new LinearLayout(this);
		lLinLayout.setId(1);
		lLinLayout.setOrientation(LinearLayout.VERTICAL);
		lLinLayout.setGravity(Gravity.CENTER);
		lLinLayout.setBackgroundColor(Color.BLACK);

		LayoutParams lLinLayoutParms = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		lLinLayout.setLayoutParams(lLinLayoutParms);

		this.setContentView(lLinLayout);

		RelativeLayout lRelLayout = new RelativeLayout(this);
		lRelLayout.setId(2);
		lRelLayout.setGravity(Gravity.CENTER);
		lRelLayout.setBackgroundColor(Color.BLACK);
		android.widget.RelativeLayout.LayoutParams lRelLayoutParms = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		lRelLayout.setLayoutParams(lRelLayoutParms);
		lLinLayout.addView(lRelLayout);

		mVideoView = new VideoView(this);
		mVideoView.setId(3);
		android.widget.RelativeLayout.LayoutParams lVidViewLayoutParams = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lVidViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mVideoView.setLayoutParams(lVidViewLayoutParams);
		lRelLayout.addView(mVideoView);

		mProgressBar = new ProgressBar(this);
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setEnabled(true);
		mProgressBar.setId(4);
		android.widget.RelativeLayout.LayoutParams lProgressBarLayoutParms = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lProgressBarLayoutParms.addRule(RelativeLayout.CENTER_IN_PARENT);
		mProgressBar.setLayoutParams(lProgressBarLayoutParms);
		lRelLayout.addView(mProgressBar);

		mProgressMessage = new IjoomerTextView(this);
		mProgressMessage.setId(5);
		android.widget.RelativeLayout.LayoutParams lProgressMsgLayoutParms = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lProgressMsgLayoutParms.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lProgressMsgLayoutParms.addRule(RelativeLayout.BELOW, 4);
		mProgressMessage.setLayoutParams(lProgressMsgLayoutParms);
		mProgressMessage.setTextColor(Color.LTGRAY);
		mProgressMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		mProgressMessage.setText("...");
		lRelLayout.addView(mProgressMessage);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		YouTubeUtility.markVideoAsViewed(this, mVideoId);

		if (mQueryYouTubeTask != null) {
			mQueryYouTubeTask.cancel(true);
		}

		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}

		// clear the flag that keeps the screen ON
		getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.mQueryYouTubeTask = null;
		this.mVideoView = null;

	}

	public void updateProgress(String pProgressMsg) {
		try {
			mProgressMessage.setText(pProgressMsg);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "Error updating video status!", e);
		}
	}

	private class ProgressUpdateInfo {

		public String mMsg;

		public ProgressUpdateInfo(String pMsg) {
			mMsg = pMsg;
		}
	}

	/**
	 * Task to figure out details by calling out to YouTube GData API. We only
	 * use public methods that don't require authentication.
	 */
	private class QueryYouTubeTask extends AsyncTask<YouTubeId, ProgressUpdateInfo, Uri> {

		private boolean mShowedError = false;

		@Override
		protected Uri doInBackground(YouTubeId... pParams) {
			if (pParams[0] instanceof FileId) {
				return Uri.parse(pParams[0].getId());
			} else {
				String lUriStr = null;
				String lYouTubeFmtQuality = "17"; // 3gpp medium quality, which
				// should be fast enough to
				// view over EDGE connection
				String lYouTubeVideoId = null;

				if (isCancelled())
					return null;

				try {

					publishProgress(new ProgressUpdateInfo(mMsgDetect));

					WifiManager lWifiManager = (WifiManager) IjoomerMediaPlayer.this.getSystemService(Context.WIFI_SERVICE);
					TelephonyManager lTelephonyManager = (TelephonyManager) IjoomerMediaPlayer.this.getSystemService(Context.TELEPHONY_SERVICE);

					// //////////////////////////
					// if we have a fast connection (wifi or 3g), then we'll get
					// a high quality YouTube video
					if ((lWifiManager.isWifiEnabled() && lWifiManager.getConnectionInfo() != null && lWifiManager.getConnectionInfo().getIpAddress() != 0)
							|| ((lTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS ||

							/*
							 * icky... using literals to make backwards
							 * compatible with 1.5 and 1.6
							 */
							lTelephonyManager.getNetworkType() == 9 /* HSUPA */|| lTelephonyManager.getNetworkType() == 10 /* HSPA */|| lTelephonyManager.getNetworkType() == 8 /* HSDPA */
									|| lTelephonyManager.getNetworkType() == 5 /* EVDO_0 */|| lTelephonyManager.getNetworkType() == 6 /*
																																		 * EVDO
																																		 * A
																																		 */)

							&& lTelephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)) {
						lYouTubeFmtQuality = "18";
					}

					// /////////////////////////////////
					// if the intent is to show a playlist, get the latest video
					// id from the playlist, otherwise the video
					// id was explicitly declared.
					if (pParams[0] instanceof PlaylistId) {
						publishProgress(new ProgressUpdateInfo(mMsgPlaylist));
						lYouTubeVideoId = YouTubeUtility.queryLatestPlaylistVideo((PlaylistId) pParams[0]);
					} else if (pParams[0] instanceof VideoId) {
						lYouTubeVideoId = pParams[0].getId();
					}

					mVideoId = lYouTubeVideoId;

					publishProgress(new ProgressUpdateInfo(mMsgToken));

					if (isCancelled())
						return null;

					// //////////////////////////////////
					// calculate the actual URL of the video, encoded with
					// proper YouTube token
					lUriStr = YouTubeUtility.calculateYouTubeUrl(lYouTubeFmtQuality, true, lYouTubeVideoId);

					if (isCancelled())
						return null;

					if (lYouTubeFmtQuality.equals("17")) {
						publishProgress(new ProgressUpdateInfo(mMsgLowBand));
					} else {
						publishProgress(new ProgressUpdateInfo(mMsgHiBand));
					}

				} catch (Exception e) {
					Log.e(this.getClass().getSimpleName(), "Error occurred while retrieving information from YouTube.", e);
				}

				if (lUriStr != null) {
					return Uri.parse(lUriStr);
				} else {
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(Uri pResult) {
			super.onPostExecute(pResult);

			try {
				if (isCancelled())
					return;

				if (pResult == null) {
					throw new RuntimeException("Invalid NULL Url.");
				}

				mVideoView.setVideoURI(pResult);

				if (isCancelled())
					return;

				mVideoView.setOnCompletionListener(new OnCompletionListener() {

					public void onCompletion(MediaPlayer pMp) {
						if (isCancelled())
							return;
						IjoomerMediaPlayer.this.finish();
					}

				});

				if (isCancelled())
					return;

				final MediaController lMediaController = new MediaController(IjoomerMediaPlayer.this);
				lMediaController.setAnchorView(mVideoView);
				mVideoView.setMediaController(lMediaController);
				Bundle bundle = getIntent().getExtras();

				boolean showControllerOnStartup = false;

				if (!(bundle == null))
					showControllerOnStartup = bundle.getBoolean("show_controller_on_startup", false);

				if (showControllerOnStartup)
					lMediaController.show(0);

				// mVideoView.setKeepScreenOn(true);
				mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

					public void onPrepared(MediaPlayer pMp) {
						if (isCancelled())
							return;
						IjoomerMediaPlayer.this.mProgressBar.setVisibility(View.GONE);
						IjoomerMediaPlayer.this.mProgressMessage.setVisibility(View.GONE);
						mVideoView.start();
					}

				});

				if (isCancelled())
					return;

				mVideoView.requestFocus();

			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "Error playing video!", e);

				if (!mShowedError) {
					showErrorAlert();
				}
			}
		}

		private void showErrorAlert() {

			try {
				Builder lBuilder = new AlertDialog.Builder(IjoomerMediaPlayer.this);
				lBuilder.setTitle(mMsgErrorTitle);
				lBuilder.setCancelable(false);
				lBuilder.setMessage(mMsgError);

				lBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface pDialog, int pWhich) {
						finish();
					}

				});

				AlertDialog lDialog = lBuilder.create();
				lDialog.show();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "Problem showing error dialog.", e);
			}
		}

		@Override
		protected void onProgressUpdate(ProgressUpdateInfo... pValues) {
			super.onProgressUpdate(pValues);

			IjoomerMediaPlayer.this.updateProgress(pValues[0].mMsg);
		}

	}

}