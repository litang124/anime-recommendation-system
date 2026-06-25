package com.anime.service;

import com.anime.entity.Anime;
import com.anime.entity.Recommendation;
import com.anime.entity.Review;
import com.anime.entity.WatchRecord;
import com.anime.repository.AnimeRepository;
import com.anime.repository.ReviewRepository;
import com.anime.repository.WatchRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤算法服务（改进版）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CollaborativeFilteringService {
    
    private final ReviewRepository reviewRepository;
    private final WatchRecordRepository watchRecordRepository;
    private final AnimeRepository animeRepository;
    private final CacheService cacheService;
    
    /**
     * 为用户生成个性化推荐（核心算法）
     */
    public List<Recommendation> generateRecommendations(Long userId, int limit) {
        log.info("开始为用户 {} 生成推荐", userId);
        
        // 1. 获取用户的评分和观看记录
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        List<WatchRecord> userRecords = watchRecordRepository.findByUserId(userId);
        
        if (userReviews.isEmpty() && userRecords.isEmpty()) {
            log.info("用户无历史数据，返回热门推荐");
            return getHotRecommendations(userId, limit);
        }
        
        // 2. 基于用户的协同过滤
        List<Recommendation> userBasedRecs = userBasedCollaborativeFiltering(userId, userReviews);
        
        // 3. 基于物品的协同过滤
        List<Recommendation> itemBasedRecs = itemBasedCollaborativeFiltering(userId, userReviews, userRecords);
        
        // 4. 合并推荐结果，去重并排序
        Map<Long, Recommendation> mergedRecs = new HashMap<>();
        
        for (Recommendation rec : userBasedRecs) {
            mergedRecs.put(rec.getAnimeId(), rec);
        }
        
        for (Recommendation rec : itemBasedRecs) {
            if (mergedRecs.containsKey(rec.getAnimeId())) {
                // 如果已存在，取平均分数
                Recommendation existing = mergedRecs.get(rec.getAnimeId());
                existing.setScore((existing.getScore() + rec.getScore()) / 2);
                existing.setReason(existing.getReason() + "; " + rec.getReason());
            } else {
                mergedRecs.put(rec.getAnimeId(), rec);
            }
        }
        
        // 5. 按分数排序并返回前 N 个
        return mergedRecs.values().stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 基于用户的协同过滤
     */
    private List<Recommendation> userBasedCollaborativeFiltering(Long userId, List<Review> userReviews) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        // 获取所有用户的评分
        List<Review> allReviews = reviewRepository.findAll();
        
        // 计算用户相似度
        Map<Long, Double> userSimilarities = calculateUserSimilarities(userId, userReviews, allReviews);
        
        // 获取相似用户的推荐
        Set<Long> userAnimeIds = userReviews.stream()
                .map(Review::getAnimeId)
                .collect(Collectors.toSet());
        
        for (Map.Entry<Long, Double> entry : userSimilarities.entrySet()) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();
            
            if (similarity < 0.3) continue; // 相似度阈值
            
            List<Review> similarUserReviews = allReviews.stream()
                    .filter(r -> r.getUserId().equals(similarUserId))
                    .filter(r -> r.getRating() != null && r.getRating() >= 4.0)
                    .filter(r -> !userAnimeIds.contains(r.getAnimeId()))
                    .collect(Collectors.toList());
            
            for (Review review : similarUserReviews) {
                Recommendation rec = new Recommendation();
                rec.setUserId(userId);
                rec.setAnimeId(review.getAnimeId());
                rec.setScore(similarity * (review.getRating() / 5.0));
                rec.setType(1); // 协同过滤
                rec.setReason("与你口味相似的用户也喜欢这部动漫");
                recommendations.add(rec);
            }
        }
        
        return recommendations;
    }
    
    /**
     * 基于物品的协同过滤
     */
    private List<Recommendation> itemBasedCollaborativeFiltering(
            Long userId, List<Review> userReviews, List<WatchRecord> userRecords) {
        
        List<Recommendation> recommendations = new ArrayList<>();
        
        // 获取用户喜欢的动漫（评分>=4 或观看完成）
        Set<Long> likedAnimeIds = new HashSet<>();
        
        userReviews.stream()
                .filter(r -> r.getRating() != null && r.getRating() >= 4.0)
                .forEach(r -> likedAnimeIds.add(r.getAnimeId()));
        
        userRecords.stream()
                .filter(r -> r.getStatus() == 2) // 已完成
                .forEach(r -> likedAnimeIds.add(r.getAnimeId()));
        
        // 为每个喜欢的动漫找相似动漫
        for (Long animeId : likedAnimeIds) {
            Optional<Anime> animeOpt = animeRepository.findById(animeId);
            if (animeOpt.isEmpty()) continue;
            
            Anime likedAnime = animeOpt.get();
            List<Anime> similarAnimes = findSimilarAnimes(likedAnime, likedAnimeIds);
            
            for (Anime similar : similarAnimes) {
                Recommendation rec = new Recommendation();
                rec.setUserId(userId);
                rec.setAnimeId(similar.getId());
                rec.setScore(calculateAnimeSimilarity(likedAnime, similar));
                rec.setType(2); // 内容推荐
                rec.setReason("因为你喜欢《" + likedAnime.getTitle() + "》");
                recommendations.add(rec);
            }
        }
        
        return recommendations;
    }
    
    /**
     * 计算用户相似度（皮尔逊相关系数）
     */
    private Map<Long, Double> calculateUserSimilarities(
            Long userId, List<Review> userReviews, List<Review> allReviews) {
        
        Map<Long, Double> similarities = new HashMap<>();
        
        // 构建用户评分矩阵（处理重复评分，取平均值）
        Map<Long, Double> userRatings = userReviews.stream()
                .collect(Collectors.toMap(
                        Review::getAnimeId, 
                        Review::getRating,
                        (r1, r2) -> (r1 + r2) / 2.0  // 合并重复键，取平均值
                ));
        
        // 按用户分组
        Map<Long, List<Review>> reviewsByUser = allReviews.stream()
                .filter(r -> !r.getUserId().equals(userId))
                .collect(Collectors.groupingBy(Review::getUserId));
        
        for (Map.Entry<Long, List<Review>> entry : reviewsByUser.entrySet()) {
            Long otherUserId = entry.getKey();
            Map<Long, Double> otherRatings = entry.getValue().stream()
                    .collect(Collectors.toMap(
                            Review::getAnimeId, 
                            Review::getRating,
                            (r1, r2) -> (r1 + r2) / 2.0  // 合并重复键，取平均值
                    ));
            
            double similarity = calculatePearsonCorrelation(userRatings, otherRatings);
            if (similarity > 0) {
                similarities.put(otherUserId, similarity);
            }
        }
        
        return similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10) // 取前 10 个相似用户
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * 计算皮尔逊相关系数
     */
    private double calculatePearsonCorrelation(Map<Long, Double> ratings1, Map<Long, Double> ratings2) {
        Set<Long> commonAnimes = new HashSet<>(ratings1.keySet());
        commonAnimes.retainAll(ratings2.keySet());
        
        if (commonAnimes.size() < 2) return 0.0;
        
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        int n = commonAnimes.size();
        
        for (Long animeId : commonAnimes) {
            double r1 = ratings1.get(animeId);
            double r2 = ratings2.get(animeId);
            
            sum1 += r1;
            sum2 += r2;
            sum1Sq += r1 * r1;
            sum2Sq += r2 * r2;
            pSum += r1 * r2;
        }
        
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / n) * (sum2Sq - sum2 * sum2 / n));
        
        return den == 0 ? 0 : num / den;
    }
    
    /**
     * 查找相似动漫（基于标签、年份、评分）
     */
    private List<Anime> findSimilarAnimes(Anime targetAnime, Set<Long> excludeIds) {
        List<Anime> allAnimes = animeRepository.findAll();
        
        return allAnimes.stream()
                .filter(a -> !a.getId().equals(targetAnime.getId()))
                .filter(a -> !excludeIds.contains(a.getId()))
                .sorted((a, b) -> Double.compare(
                        calculateAnimeSimilarity(targetAnime, b),
                        calculateAnimeSimilarity(targetAnime, a)
                ))
                .limit(5)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算动漫相似度
     */
    private double calculateAnimeSimilarity(Anime anime1, Anime anime2) {
        double similarity = 0.0;
        
        // 标签相似度（权重 0.5）
        if (anime1.getTags() != null && anime2.getTags() != null) {
            Set<String> tags1 = new HashSet<>(Arrays.asList(anime1.getTags().split(",")));
            Set<String> tags2 = new HashSet<>(Arrays.asList(anime2.getTags().split(",")));
            Set<String> intersection = new HashSet<>(tags1);
            intersection.retainAll(tags2);
            Set<String> union = new HashSet<>(tags1);
            union.addAll(tags2);
            similarity += 0.5 * (intersection.size() / (double) union.size());
        }
        
        // 年份相似度（权重 0.2）
        if (anime1.getReleaseYear() != null && anime2.getReleaseYear() != null) {
            int yearDiff = Math.abs(anime1.getReleaseYear() - anime2.getReleaseYear());
            similarity += 0.2 * Math.max(0, 1 - yearDiff / 10.0);
        }
        
        // 评分相似度（权重 0.3）
        if (anime1.getScore() != null && anime2.getScore() != null) {
            double scoreDiff = Math.abs(anime1.getScore() - anime2.getScore());
            similarity += 0.3 * Math.max(0, 1 - scoreDiff / 5.0);
        }
        
        return similarity;
    }
    
    /**
     * 热门推荐（当用户无历史数据时）
     */
    private List<Recommendation> getHotRecommendations(Long userId, int limit) {
        List<Anime> hotAnimes = animeRepository.findTop10ByOrderByHeatDesc();
        
        return hotAnimes.stream()
                .limit(limit)
                .map(anime -> {
                    Recommendation rec = new Recommendation();
                    rec.setUserId(userId);
                    rec.setAnimeId(anime.getId());
                    rec.setScore(anime.getHeat() / 10000.0);
                    rec.setType(3); // 热门推荐
                    rec.setReason("当前热门动漫");
                    return rec;
                })
                .collect(Collectors.toList());
    }
}
