package com.arpaul.gpslibrary.parseDirection;

import android.text.Html;

import com.arpaul.gpslibrary.parseDirection.model.GDLegs;
import com.arpaul.gpslibrary.parseDirection.model.GDPath;
import com.arpaul.gpslibrary.parseDirection.model.GDirection;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Aritra on 5/19/2016.
 */
public class FormatterSimple implements IGDFormatter {
    @Override
    public String getTitle(GDPath path) {
        return "Distance :" + StringUtils.getMeterToMile(path.getDistance()) + " mile";
    }

    @Override
    public String getSnippet(GDPath path) {
        return Html.fromHtml(path.getHtmlText()).toString();
    }

    @Override
    public boolean isInfoWindows() {
        return false;
    }

    @Override
    public void setContents(Marker marker, GDirection direction, GDLegs legs, GDPath path) {
    }
}
