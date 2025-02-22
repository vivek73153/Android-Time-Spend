package com.ijoomer.customviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ijoomer.tracethesteps.src.R;

/**
 * This Class Contains All Method Related To IjoomerRatingBar.
 * 
 * @author tasol
 * 
 */
public class IjoomerRatingBar extends LinearLayout {

	@SuppressWarnings("unused")
	private Context context;
	private RatingHandler ratingHandler;
	private ImageView[] imgStar;

	private int emptyStarResourceId;
	private int starBgColor = 0;
	private int filledStarResourceId;
	private int halfFilledStarResourceId;
	private int starSize;
	private float starRating = 0;
	private boolean isEditable;

	@SuppressLint("NewApi")
	public IjoomerRatingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(attrs);
	}

	public IjoomerRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public IjoomerRatingBar(Context context) {
		super(context);
		this.context = context;
		init(null);
	}

	private void init(AttributeSet attrs) {

		setGravity(Gravity.CENTER_VERTICAL);
		if (attrs == null) {
			filledStarResourceId = R.drawable.ijoomer_ratingstar_green;
			emptyStarResourceId = R.drawable.ijoomer_ratingstar_gray;
			halfFilledStarResourceId = R.drawable.ijoomer_ratingstarhalf_green;
			starRating = 0;
			starSize = convertSizeToDeviceDependent(10);
			isEditable = false;

		} else {
			String namespace = "http://schemas.android.com/apk/res/" + getContext().getPackageName();
			filledStarResourceId = attrs.getAttributeResourceValue(namespace, "star_filled", R.drawable.ijoomer_ratingstar_green);
			emptyStarResourceId = attrs.getAttributeResourceValue(namespace, "star_empty", R.drawable.ijoomer_ratingstar_gray);
			halfFilledStarResourceId = attrs.getAttributeResourceValue(namespace, "star_half", R.drawable.ijoomer_ratingstarhalf_green);
			starRating = attrs.getAttributeFloatValue(namespace, "star_rating", 0);
			starSize = attrs.getAttributeIntValue(namespace, "star_size", convertSizeToDeviceDependent(10));
			isEditable = attrs.getAttributeBooleanValue(namespace, "isEditable", false);
		}
		createView();
	}

	/**
	 * This method used to create view.
	 */
	private void createView() {

		View v = LayoutInflater.from(getContext()).inflate(R.layout.ijoomer_ratingbar, null);
		imgStar = new ImageView[5];
		imgStar[0] = (ImageView) v.findViewById(R.id.imgStar1);
		imgStar[1] = (ImageView) v.findViewById(R.id.imgStar2);
		imgStar[2] = (ImageView) v.findViewById(R.id.imgStar3);
		imgStar[3] = (ImageView) v.findViewById(R.id.imgStar4);
		imgStar[4] = (ImageView) v.findViewById(R.id.imgStar5);
		for (int i = 0; i < 5; i++) {
			imgStar[i].setImageResource(emptyStarResourceId);

			imgStar[i].getLayoutParams().width = starSize;
			imgStar[i].getLayoutParams().height = starSize;
			imgStar[i].setTag(i);

			imgStar[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isEditable) {
						starRating = ((Integer) v.getTag()) + 1;
						updateView();
						if (getRatingHandler() != null)
							ratingHandler.onRatingChangedListener(starRating);
					}

				}
			});
		}
		v.setBackgroundColor(Color.TRANSPARENT);
		addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		updateView();

	}

	/**
	 * This method used to update view.
	 */
	private void updateView() {

		for (int i = 0; i < 5; i++) {
			imgStar[i].setImageResource(emptyStarResourceId);
			imgStar[i].getLayoutParams().width = starSize;
			imgStar[i].getLayoutParams().height = starSize;

		}
		if (starRating > 0 && starRating <= 5.0) {
			for (int i = 0; i < Math.ceil(starRating); i++) {
				if (i == Math.floor(starRating) && Math.ceil(starRating) > Math.floor(starRating)) {
					imgStar[i].setImageResource(halfFilledStarResourceId);
					if (starBgColor != 0) {
						 imgStar[i].setBackgroundColor(starBgColor);
					}

				} else {
					imgStar[i].setImageResource(filledStarResourceId);
					if (starBgColor != 0) {
						 imgStar[i].setBackgroundColor(starBgColor);
					}

				}
			}
		}

	}

	/**
	 * This method used to get rating handler.
	 * 
	 * @return represented {@link RatingHandler}
	 */
	public RatingHandler getRatingHandler() {
		return ratingHandler;
	}

	/**
	 * This method used to get star bg color id.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getStarBgColor() {
		return starBgColor;
	}

	/**
	 * This method used to set star bg color id.
	 * 
	 * @param starBgColor
	 *            represented bg color id
	 */
	public void setStarBgColor(int starBgColor) {
		this.starBgColor = starBgColor;
	}

	/**
	 * This method used to set rating handler.
	 * 
	 * @param ratingHandler
	 *            represented handler
	 */
	public void setRatingHandler(RatingHandler ratingHandler) {
		this.ratingHandler = ratingHandler;
	}

	/**
	 * This method used to get star size.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getStarSize() {
		return starSize;
	}

	/**
	 * This method used to set star size.
	 * 
	 * @param starSize
	 *            represented size
	 */
	public void setStarSize(int starSize) {
		this.starSize = convertSizeToDeviceDependent(starSize);
		updateView();

	}

	/**
	 * This method used to get star rating.
	 * 
	 * @return represented {@link Float}
	 */
	public float getStarRating() {
		return starRating;
	}

	/**
	 * This method used to set star rating.
	 * 
	 * @param starRating
	 */
	public void setStarRating(float starRating) {
		this.starRating = starRating;
		updateView();
	}

	/**
	 * This method used to check is editable.
	 * 
	 * @return represented
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * This method set is editable.
	 * 
	 * @param isEditable
	 *            represented editable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	/**
	 * This method used to get empty star resource id.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getEmptyStarResourceId() {
		return emptyStarResourceId;
	}

	/**
	 * This method used to set empty star resource id.
	 * 
	 * @param emptyStarResourceId
	 *            represented resource id
	 */
	public void setEmptyStarResourceId(int emptyStarResourceId) {
		this.emptyStarResourceId = emptyStarResourceId;
	}

	/**
	 * This method used to get filled star resource id.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getFilledStarResourceId() {
		return filledStarResourceId;
	}

	/**
	 * This method used to set filled star resource id.
	 * 
	 * @param filledStarResourceId
	 *            represented resource id
	 */
	public void setFilledStarResourceId(int filledStarResourceId) {
		this.filledStarResourceId = filledStarResourceId;
	}

	/**
	 * This method used to get half start resource id.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getHalfFilledStarResourceId() {
		return halfFilledStarResourceId;
	}

	/**
	 * This method used to set half resource id.
	 * 
	 * @param halfFilledStarResourceId
	 *            represented resource id
	 */
	public void setHalfFilledStarResourceId(int halfFilledStarResourceId) {
		this.halfFilledStarResourceId = halfFilledStarResourceId;
	}

	/**
	 * This method used to convert size to device dependent.
	 * 
	 * @param value
	 *            represented size
	 * @return represented {@link Integer}
	 */
	public int convertSizeToDeviceDependent(int value) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return ((dm.densityDpi * value) / 160);
	}

	/**
	 * Inner interface
	 * 
	 * @author tasol
	 * 
	 */
	public interface RatingHandler {
		void onRatingChangedListener(float rating);

	}

}
