package com.paranoidandroid.journey.wizard.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.paranoidandroid.journey.wizard.models.LegItem;

/** Customizing AutoCompleteTextView to return Place Description
 * corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the place description and place id corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        LegItem item = (LegItem) selectedItem;
        return item.getDestination();
    }

}
