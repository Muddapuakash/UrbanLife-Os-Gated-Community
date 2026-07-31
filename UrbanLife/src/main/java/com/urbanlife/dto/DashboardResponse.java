package com.urbanlife.dto;

public class DashboardResponse {

    private Long communityId;
    private String communityName;

    private long totalResidents;
    private long activeResidents;

    private long totalFlats;
    private long totalVehicles;
    private long totalVisitors;

    private long totalComplaints;
    private long pendingComplaints;
    private long resolvedComplaints;

    private long totalEvents;
    private long activeEvents;

    private long totalPolls;
    private long activePolls;

    private long totalLostFoundItems;
    private long totalNotices;

    private long totalEmergencies;
    private long activeEmergencies;

    public DashboardResponse() {
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public long getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(long totalResidents) {
        this.totalResidents = totalResidents;
    }

    public long getActiveResidents() {
        return activeResidents;
    }

    public void setActiveResidents(long activeResidents) {
        this.activeResidents = activeResidents;
    }

    public long getTotalFlats() {
        return totalFlats;
    }

    public void setTotalFlats(long totalFlats) {
        this.totalFlats = totalFlats;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getTotalVisitors() {
        return totalVisitors;
    }

    public void setTotalVisitors(long totalVisitors) {
        this.totalVisitors = totalVisitors;
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(long activeEvents) {
        this.activeEvents = activeEvents;
    }

    public long getTotalPolls() {
        return totalPolls;
    }

    public void setTotalPolls(long totalPolls) {
        this.totalPolls = totalPolls;
    }

    public long getActivePolls() {
        return activePolls;
    }

    public void setActivePolls(long activePolls) {
        this.activePolls = activePolls;
    }

    public long getTotalLostFoundItems() {
        return totalLostFoundItems;
    }

    public void setTotalLostFoundItems(long totalLostFoundItems) {
        this.totalLostFoundItems = totalLostFoundItems;
    }

    public long getTotalNotices() {
        return totalNotices;
    }

    public void setTotalNotices(long totalNotices) {
        this.totalNotices = totalNotices;
    }

    public long getTotalEmergencies() {
        return totalEmergencies;
    }

    public void setTotalEmergencies(long totalEmergencies) {
        this.totalEmergencies = totalEmergencies;
    }

    public long getActiveEmergencies() {
        return activeEmergencies;
    }

    public void setActiveEmergencies(long activeEmergencies) {
        this.activeEmergencies = activeEmergencies;
    }
}