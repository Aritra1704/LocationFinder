package com.param.gpsutilities.parseDirection;

import android.text.Html;

import com.example.libraryutilities.StringUtils;
import com.google.android.gms.maps.model.Marker;
import com.param.gpsutilities.parseDirection.model.GDLegs;
import com.param.gpsutilities.parseDirection.model.GDPath;
import com.param.gpsutilities.parseDirection.model.GDirection;

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
