package com.urbanlife.dto;

import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateComplaintRequest {

    @NotNull(message = "Resident id is required")
    private Long residentId;

    @NotBlank(message = "Complaint title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Category is required")
    private ComplaintCategory category;

    @NotNull(message = "Priority is required")
    private ComplaintPriority priority;

    public CreateComplaintRequest() {
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }
}