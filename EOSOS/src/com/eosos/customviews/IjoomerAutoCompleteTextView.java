package com.eosos.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.eosos.common.configuration.IjoomerApplicationConfiguration;

/**
 * This Class Contains All Method Related To IjoomerButton.
 * 
 * @author tasol
 * 
 */
public class IjoomerAutoCompleteTextView extends AutoCompleteTextView {

	public IjoomerAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public IjoomerAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public IjoomerAutoCompleteTextView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context mContext) {

		try {
			if (IjoomerApplicationConfiguration.getFontFace() != null) {
				setTypeface(IjoomerApplicationConfiguration.getFontFace());
			} else {
				Typeface tf = Typeface.createFromAsset(mContext.getAssets(), IjoomerApplicationConfiguration.getFontNameWithPath());
				setTypeface(tf);
				IjoomerApplicationConfiguration.setFontFace(tf);
			}
		} catch (Throwable e) {
		}
	}

}
