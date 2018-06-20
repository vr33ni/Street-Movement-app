package com.example.vreeni.StreetMovementApp;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * class responsible for formatting the values displayed inside the chart to a particular format
 */
public class Charts_InsideValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public Charts_InsideValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0"); // use no decimals
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(value) + " activities"; // e.g. append name of activity
    }
}