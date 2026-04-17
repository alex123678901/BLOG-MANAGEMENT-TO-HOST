package com.blog.blogmanagementsystem.dto;

import com.blog.blogmanagementsystem.model.PostStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class PostRequest {

    private String title;

    private String content;

    private String summary;
    private String featuredImage;
    private PostStatus status;
    private com.blog.blogmanagementsystem.model.MediaType mediaType;
    private Set<Long> categoryIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public com.blog.blogmanagementsystem.model.MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(com.blog.blogmanagementsystem.model.MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
