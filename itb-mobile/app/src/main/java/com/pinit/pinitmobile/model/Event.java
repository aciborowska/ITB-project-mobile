package com.pinit.pinitmobile.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class Event {

    private Long eventId;
    private String name;
    private String description;
    private long startDate;
    private int commentsAmount = 0;
    private int positivesAmount = 0;
    private int negativesAmount = 0;
    private boolean isPrivate = false;
    private Long eventTypeId;
    private Long groupId;
    @JsonIgnore
    private Long locationId;
    private Long adminId;
     @JsonIgnore
    private int mark = -2;
    private boolean active = true;
    private EventLocation location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPositivesAmount() {
        return positivesAmount;
    }

    public void setPositivesAmount(int positivesAmount) {
        this.positivesAmount = positivesAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNegativesAmount() {
        return negativesAmount;
    }

    public void setNegativesAmount(int negativesAmount) {
        this.negativesAmount = negativesAmount;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long id) {
        this.eventId = id;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public int getCommentsAmount() {
        return commentsAmount;
    }

    public void setCommentsAmount(int commentsAmount) {
        this.commentsAmount = commentsAmount;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public EventLocation getLocation() {
        return location;
    }

    public void setLocation(EventLocation location) {
        this.location = location;
    }

    public boolean isisPrivate() {
        return isPrivate;
    }

    public void setisPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public boolean isisActive() {
        return active;
    }

    public void setIsActive(boolean isActive) {
        this.active = isActive;
    }

    public void markEvent(int mark){
        if(mark<0){
            negativesAmount++;
        }
        else positivesAmount++;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return eventId == event.getEventId();

    }

    @Override
    public int hashCode() {
        return (int) (eventId ^ (eventId >>> 32));
    }


    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
