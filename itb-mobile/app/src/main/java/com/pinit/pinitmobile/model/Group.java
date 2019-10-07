package com.pinit.pinitmobile.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private List<Event> events = new ArrayList<>();
    private Long groupId;
    private String name;
    private Long adminId;
    private int peopleAmount = 0;
    private long createdDate;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void addEvent(Event e) {
        events.add(e);
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return groupId == group.groupId;

    }

    @Override
    public int hashCode() {
        return (int) (groupId ^ (groupId >>> 32));
    }

    @Override
    public String toString() {
        return name;
    }


    public int getPeopleAmount() {
        return peopleAmount;
    }

    public void setPeopleAmount(int peopleAmount) {
        this.peopleAmount = peopleAmount;
    }
}
