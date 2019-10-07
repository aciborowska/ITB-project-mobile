package com.pinit.pinitmobile.ui;


import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.pinit.pinitmobile.R;

public class EventRenderer extends DefaultClusterRenderer<MarkerClusterItem> {

    public EventRenderer(Context context, GoogleMap map, ClusterManager<MarkerClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerClusterItem item, MarkerOptions markerOptions) {
        if (item.getCommentsAmount() < 2) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_grey_36dp));
        else if (item.getCommentsAmount() < 6)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_green_36dp));
        else if (item.getCommentsAmount() < 10)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_yellow_36dp));
        else markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_red_36dp));
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
