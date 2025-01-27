package com.eosos.common.classes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.LoginButton;
import com.eosos.src.R;
import com.eosos.theme.ThemeManager;

import java.util.Arrays;
import java.util.List;


/**
 * This Class Contains All Method Related FacebookLoginMainFragment.
 * 
 * @author tasol
 * 
 */
public class FacebookLoginMainFragment extends Fragment {

	private static final List<String> PERMISSIONS = Arrays.asList("email");
	/**
	 * Override method
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(ThemeManager.getInstance().getFacebook(), container, false);

		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(PERMISSIONS);
		authButton.setApplicationId(getString(R.string.facebook_app_id));

		return view;
	}
}
