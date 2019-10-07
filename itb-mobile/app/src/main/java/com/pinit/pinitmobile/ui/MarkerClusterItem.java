package com.pinit.pinitmobile.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClusterItem implements ClusterItem {
    private final LatLng position;
    private final long eventId;
    private int commentsAmount = 0;

    public MarkerClusterItem(LatLng latLng, long eventId, int commentsAmount) {
        position = latLng;
        this.eventId = eventId;
        this.commentsAmount = commentsAmount;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public long getEventId() {
        return eventId;
    }

    public int getCommentsAmount() {
        return commentsAmount;
    }
}
