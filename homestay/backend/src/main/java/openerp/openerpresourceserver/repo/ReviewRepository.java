package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHomestayId(Long homestayId);

    List<Review> findByUserId(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.homestayId = :homestayId")
    Double findAverageRatingByHomestayId(@Param("homestayId") Long homestayId);

    @Query("SELECT r FROM Review r WHERE r.homestayId = :homestayId ORDER BY r.createdAt DESC")
    List<Review> findLatestReviewsByHomestayId(@Param("homestayId") Long homestayId);
}
