package com.urbanlife.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "staff_ratings")
public class StaffRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private DomesticStaff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 500)
    private String review;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StaffRating() {
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public DomesticStaff getStaff() {
        return staff;
    }

    public void setStaff(DomesticStaff staff) {
        this.staff = staff;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}