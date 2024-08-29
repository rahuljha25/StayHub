package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.ReviewDto;
import com.StayHub.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @PostMapping("/addReviews")
    public ResponseEntity<ReviewDto> addReviews(@RequestParam long propertyId,
                                                @AuthenticationPrincipal User user,
                                                @RequestBody ReviewDto dto){
        ReviewDto addedReviews = reviewService.addReviews(propertyId, user, dto);
        return new ResponseEntity<>(addedReviews, HttpStatus.CREATED);
    }
    @GetMapping("/getReviewByUser")
    public ResponseEntity<List<ReviewDto>> getReviewByUser(@AuthenticationPrincipal User user){
        List<ReviewDto> reviewByUser = reviewService.getReviewByUser(user);
        return new ResponseEntity<>(reviewByUser,HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReview(@RequestParam long propertyId,
                                                  @AuthenticationPrincipal User user,
                                                @RequestParam long targetedUserId
                                               ){
        reviewService.deleteReview(propertyId,user,targetedUserId);
        return new ResponseEntity<>("Review is deleted!!",HttpStatus.OK);
    }
    @GetMapping("/get-allReview")
    public ResponseEntity<List<ReviewDto>> getAllReviewByPropertyId(@RequestParam long propertyId){
        List<ReviewDto> allReviewByPropertyId = reviewService.getAllReviewByPropertyId(propertyId);
        return new ResponseEntity<>(allReviewByPropertyId,HttpStatus.OK);
    }
}
