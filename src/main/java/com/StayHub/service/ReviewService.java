package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.payload.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReviews(long propertyId, User user, ReviewDto dto);

    List<ReviewDto> getReviewByUser(User user);

    void deleteReview(long propertyId, User user, long targetedUserId);

    List<ReviewDto> getAllReviewByPropertyId(long propertyId);
}
