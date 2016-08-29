package com.param.gpsutilities.parseDirection.util;

import com.param.gpsutilities.parseDirection.model.GDLegs;
import com.param.gpsutilities.parseDirection.model.GDirection;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aritra on 5/23/2016.
 */
public class GPSSortUtils {
    public static void sortReversely(List<GDirection> swapList){
        Collections.sort(swapList, new Comparator<GDirection>() {
            @Override
            public int compare(GDirection lhs, GDirection rhs) {
                int leftDist = 0;
                int rightDist = 0;
                for (GDLegs legs : lhs.getLegsList()) {
                    leftDist = legs.getmDistance();
                }
                for (GDLegs legs : rhs.getLegsList()) {
                    rightDist = legs.getmDistance();
                }
                return leftDist - rightDist;
                //return rightDist - leftDist;
            }
        });

    }
}
