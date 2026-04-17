package com.blog.blogmanagementsystem.dto;

public class DashboardStatsResponse {
    private long totalPosts;
    private long totalUsers;
    private long totalComments;
    private long totalViews;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalPosts, long totalUsers, long totalComments, long totalViews) {
        this.totalPosts = totalPosts;
        this.totalUsers = totalUsers;
        this.totalComments = totalComments;
        this.totalViews = totalViews;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }
}
