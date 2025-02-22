/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ijoomer.common.classes;

import pl.mg6.android.maps.extensions.ClusteringSettings.IconDataProvider;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ijoomer.tracethesteps.src.R;

/**
 * This Class Contains All Method Related To IjoomerMapClusterIconProvider.
 * 
 * @author tasol
 * 
 */
public class IjoomerMapClusterIconProvider implements IconDataProvider {

	private Bitmap[] baseBitmaps;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Rect bounds = new Rect();
	private MarkerOptions markerOptions = new MarkerOptions().anchor(0.5f, 0.5f);

	private static final int[] res = { R.drawable.ijoomer_clustering_m1, R.drawable.ijoomer_clustering_m2, R.drawable.ijoomer_clustering_m3, R.drawable.ijoomer_clustering_m4, R.drawable.ijoomer_clustering_m5 };
	private static final int[] forCounts = { 10, 100, 1000, 10000, Integer.MAX_VALUE };


	/**
	 * Constructor
	 * @param resources represented {@link Resources}
	 */
	public IjoomerMapClusterIconProvider(Resources resources) {
		baseBitmaps = new Bitmap[res.length];
		for (int i = 0; i < res.length; i++) {
			baseBitmaps[i] = BitmapFactory.decodeResource(resources, res[i]);
		}
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(resources.getDimension(R.dimen.text_size));
	}

	
	/**
	 * Overrides method
	 */
	@Override
	public MarkerOptions getIconData(int markersCount) {
		Bitmap base;
		int i = 0;
		do {
			base = baseBitmaps[i];
		} while (markersCount >= forCounts[i++]);

		Bitmap bitmap = base.copy(Config.ARGB_8888, true);

		String text = String.valueOf(markersCount);
		paint.getTextBounds(text, 0, text.length(), bounds);
		float x = bitmap.getWidth() / 2.0f;
		float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;

		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, x, y, paint);

		BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
		return markerOptions.icon(icon);
	}
}
