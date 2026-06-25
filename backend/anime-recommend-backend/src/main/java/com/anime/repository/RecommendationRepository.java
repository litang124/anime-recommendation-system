package com.anime.repository;

import com.anime.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    List<Recommendation> findByUserIdOrderByScoreDesc(Long userId);
    
    List<Recommendation> findByUserIdAndCreateTimeAfter(Long userId, LocalDateTime time);
    
    void deleteByUserIdAndCreateTimeBefore(Long userId, LocalDateTime time);
}
