/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package net.reichholf.dreamdroid.fragment.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import net.reichholf.dreamdroid.R;
import net.reichholf.dreamdroid.helpers.BundleHelper;

import java.util.ArrayList;

/**
 * @author sre
 */
public class MultiChoiceDialog extends DialogFragment {
	private static final String KEY_TITLE_ID = "titleId";
	private static final String KEY_ITEMS = "items";
	private static final String KEY_CHECKED_ITEMS = "checkedItems";
	private static final String KEY_POSITIVE_STRING_ID = "positiveStringId";

	private int mTitleId;
	private CharSequence[] mItems;
	private boolean[] mCheckedItems;
	private int mPositiveStringId;

	public interface MultiChoiceDialogListener {
		public void onMultiChoiceDialogSelection(String dialogTag, DialogInterface dialog, Integer[] selected);

		public void onMultiChoiceDialogFinish(String dialogTag, int result);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		outState.putBooleanArray("checkedItems", mCheckedItems);
		super.onSaveInstanceState(outState);
	}

	public static MultiChoiceDialog newInstance(int titleId, CharSequence[] items, boolean[] checkedItems) {
		return MultiChoiceDialog.newInstance(titleId, items, checkedItems, R.string.ok, -1);
	}

	public static MultiChoiceDialog newInstance(int titleId, CharSequence[] items, boolean[] checkedItems,
	                                            int positiveStringId, int negativeStringId) {

		MultiChoiceDialog fragment = new MultiChoiceDialog();
		Bundle args = new Bundle();
		args.putInt(KEY_TITLE_ID, titleId);
		args.putStringArrayList(KEY_ITEMS, BundleHelper.toStringArrayList(items));
		args.putBooleanArray(KEY_CHECKED_ITEMS, checkedItems);
		args.putInt(KEY_POSITIVE_STRING_ID, positiveStringId);
		fragment.setArguments(args);
		return fragment;
	}

	public void init() {
		Bundle args = getArguments();
		mTitleId = args.getInt(KEY_TITLE_ID);
		mItems = BundleHelper.toCharSequenceArray(args.getStringArrayList((KEY_ITEMS)));
		mCheckedItems = args.getBooleanArray(KEY_CHECKED_ITEMS);
		mPositiveStringId = args.getInt(KEY_POSITIVE_STRING_ID);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		init();
		if (savedInstanceState != null) {
			boolean[] checked = savedInstanceState.getBooleanArray("checkedItems");
			if (checked != null)
				mCheckedItems = checked;
		}
		ArrayList<Integer> selectedList = new ArrayList<>();
		for (int i = 0; i < mCheckedItems.length; ++i) {
			if (mCheckedItems[i])
				selectedList.add(i);
		}
		Integer[] selected = new Integer[selectedList.size()];
		selectedList.toArray(selected);

		MaterialDialog.Builder builder;
		builder = new MaterialDialog.Builder(getActivity());
		builder.title(mTitleId)
				.items(mItems)
				.itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {
					@Override
					public boolean onSelection(MaterialDialog materialDialog, Integer[] selected, CharSequence[] charSequences) {
						((MultiChoiceDialogListener) getActivity()).onMultiChoiceDialogSelection(getTag(), materialDialog, selected);
						((MultiChoiceDialogListener) getActivity()).onMultiChoiceDialogFinish(getTag(), Activity.RESULT_OK);
						return true;
					}
				})
				.positiveText(mPositiveStringId);

		return builder.build();
	}
}