/* EditTextFragment is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import com.codeshane.representing.R;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/** @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 23, 2013
 * @version 1 */
public class EditTextFragment extends DialogFragment {
	/** Qualified-name, that is, class name including package/namespace. */
	public static final String TAG = EditTextFragment.class.getName();

	public static final String KEY_mEditText_text = TAG + "mEditText.text";

	private EditText	mEditText;

	public EditTextFragment () {
		// Empty constructor required for DialogFragment.
	}

	/** @see android.support.v4.app.DialogFragment#onCreateView(onCreateView, android.view.ViewGroup, android.os.Bundle) */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View view = inflater.inflate(R.layout.fragment_edit_text, container);
		return mEditText = (EditText) view.findViewById(android.R.id.edit);
	}

	/** @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle) */
	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		savedInstanceState.get(TAG.concat(KEY_mEditText_text));
	}

	/** @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle) */
	@Override public Dialog onCreateDialog ( Bundle savedInstanceState ) {
		return super.onCreateDialog(savedInstanceState);
	}

	/** @see android.support.v4.app.DialogFragment#onSaveInstanceState(android.os.Bundle) */
	@Override public void onSaveInstanceState ( Bundle bundle ) {
		super.onSaveInstanceState( bundle );
		if (null==mEditText) { return; }
		bundle.putString(TAG+KEY_mEditText_text, mEditText.getText().toString());
	}

}