package com.StayHub.service.Impl;

import com.StayHub.entity.Property;
import com.StayHub.entity.Reviews;
import com.StayHub.entity.User;
import com.StayHub.exception.ReviewException;
import com.StayHub.payload.ReviewDto;
import com.StayHub.repository.PropertyRepository;
import com.StayHub.repository.ReviewsRepository;
import com.StayHub.repository.UserRepository;
import com.StayHub.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewsRepository reviewsRepository;
    private PropertyRepository propertyRepository;
    private UserRepository userRepository;

    public ReviewServiceImpl(ReviewsRepository reviewsRepository, PropertyRepository propertyRepository, UserRepository userRepository) {
        this.reviewsRepository = reviewsRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewDto addReviews(long propertyId, User user, ReviewDto dto) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ReviewException("Property not found with id: " + propertyId)
        );
        Optional<Reviews> reviewByUser = reviewsRepository.findReviewByUser(user, property);
        if (reviewByUser.isPresent()){
            throw new ReviewException("Already review exist ");
        }else {
            dto.setProperty(property);
            dto.setUser(user);
            Reviews reviews = dtoToEntity(dto);
            if (reviews.getRatings()!=null && reviews.getRatings()>=1 && reviews.getRatings()<=5){
                Reviews save = reviewsRepository.save(reviews);
                return entityToDto(save);
            }else {
                throw new ReviewException("Give proper rating: ");
            }
        }


    }

    @Override
    public List<ReviewDto> getReviewByUser(User user) {
        List<Reviews> byUserReview = reviewsRepository.findByUserReview(user);
        List<ReviewDto> collect = byUserReview.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void deleteReview(long propertyId, User user, long targetedUserId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ReviewException("Property not found with id: " + propertyId)
        );
        User existingUser = userRepository.findById(targetedUserId).orElseThrow(
                () -> new ReviewException("User not found with id: " + targetedUserId)
        );
        Optional<Reviews> reviewByUser = reviewsRepository.findReviewByUser(existingUser, property);
        if (reviewByUser.isPresent()){
            reviewsRepository.deleteById(reviewByUser.get().getId());
        }else {
            throw new ReviewException("Review does not exist");
        }

    }

    @Override
    public List<ReviewDto> getAllReviewByPropertyId(long propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ReviewException("Property not found with id: " + propertyId)
        );
        List<Reviews> byProperty = reviewsRepository.findByProperty(property);
        List<ReviewDto> collect = byProperty.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    //dto to entity
    private Reviews dtoToEntity(ReviewDto dto){
        Reviews reviews=new Reviews();
        reviews.setRatings(dto.getRatings());
        reviews.setDescription(dto.getDescription());
        reviews.setProperty(dto.getProperty());
        reviews.setUser(dto.getUser());
        return reviews;
    }
    //entity to dto
    private ReviewDto entityToDto(Reviews reviews){
        ReviewDto  dto=new ReviewDto();
        dto.setId(reviews.getId());
        dto.setRatings(reviews.getRatings());
        dto.setDescription(reviews.getDescription());
        dto.setUser(reviews.getUser());
        dto.setProperty(reviews.getProperty());
        return dto;
    }
}
