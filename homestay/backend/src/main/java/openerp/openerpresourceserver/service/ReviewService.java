package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.Review;

public interface ReviewService {
    Review getReviewById(Long reviewId);

    List<Review> getReviewsByHomestay(Long homestayId);

    List<Review> getReviewsByUser(Long userId);

    Double getAverageRatingForHomestay(Long homestayId);

    List<Review> getLatestReviewsForHomestay(Long homestayId);

    Review createReview(Review review);

    Review updateReview(Long reviewId, Review review);

    void deleteReview(Long reviewId);
    
}
