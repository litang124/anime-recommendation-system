package com.anime.service;

import com.anime.entity.Anime;
import com.anime.entity.Review;
import com.anime.entity.UserProfile;
import com.anime.entity.WatchRecord;
import com.anime.repository.AnimeRepository;
import com.anime.repository.ReviewRepository;
import com.anime.repository.UserProfileRepository;
import com.anime.repository.WatchRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户画像服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final ReviewRepository reviewRepository;
    private final WatchRecordRepository watchRecordRepository;
    private final AnimeRepository animeRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取用户画像
     */
    public UserProfile getUserProfile(Long userId) {
        String cacheKey = "user_profile:" + userId;
        try {
            UserProfile cached = (UserProfile) cacheService.get(cacheKey);
            if (cached != null) {
                log.debug("从缓存命中用户画像：{}", userId);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis 缓存读取失败，从数据库查询：{}", e.getMessage());
        }
        
        log.debug("查询用户画像：{}", userId);
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        
        try {
            cacheService.set(cacheKey, profile, 1, TimeUnit.HOURS);
            log.debug("用户画像已缓存：{}", userId);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败：{}", e.getMessage());
        }
        return profile;
    }
    
    /**
     * 更新用户画像（用户行为触发）
     */
    @Transactional
    public UserProfile updateUserProfile(Long userId) {
        log.info("更新用户 {} 的画像", userId);
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        
        // 1. 计算偏好分类权重
        Map<String, Double> categoryWeights = calculateCategoryPreference(userId);
        try {
            profile.setFavoriteCategories(objectMapper.writeValueAsString(categoryWeights));
        } catch (JsonProcessingException e) {
            log.error("序列化分类偏好失败", e);
            profile.setFavoriteCategories("{}");
        }
        
        // 2. 计算偏好标签权重
        Map<String, Double> tagWeights = calculateTagPreference(userId);
        try {
            profile.setFavoriteTags(objectMapper.writeValueAsString(tagWeights));
        } catch (JsonProcessingException e) {
            log.error("序列化标签偏好失败", e);
            profile.setFavoriteTags("{}");
        }
        
        // 3. 更新统计数据
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<WatchRecord> records = watchRecordRepository.findByUserId(userId);
        
        profile.setTotalViews((int) records.size());
        profile.setTotalFavorites(0); // TODO: 如果有收藏功能再实现
        
        double avgScore = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0);
        profile.setAvgScore(Math.round(avgScore * 10) / 10.0);
        
        profile.setUpdateTime(LocalDateTime.now());
        userProfileRepository.save(profile);
        
        // 更新缓存
        String cacheKey = "user_profile:" + userId;
        try {
            cacheService.set(cacheKey, profile, 1, TimeUnit.HOURS);
            log.debug("用户画像缓存已更新：{}", userId);
        } catch (Exception e) {
            log.warn("Redis 缓存更新失败：{}", e.getMessage());
        }
        
        return profile;
    }
    
    /**
     * 计算分类偏好（带时间衰减）
     */
    private Map<String, Double> calculateCategoryPreference(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<WatchRecord> records = watchRecordRepository.findByUserId(userId);
        
        Map<String, Double> categoryScores = new HashMap<>();
        
        // 从评论中计算
        for (Review review : reviews) {
            if (review.getRating() == null) continue;
            
            Anime anime = animeRepository.findById(review.getAnimeId()).orElse(null);
            if (anime == null) continue;
            
            String category = anime.getTags();
            if (category == null) continue;
            
            double baseScore = review.getRating() / 5.0;
            double timeDecay = calculateTimeDecay(review.getCreateTime());
            double score = baseScore * timeDecay;
            
            Arrays.stream(category.split(","))
                    .map(String::trim)
                    .forEach(cat -> categoryScores.merge(cat, score, Double::sum));
        }
        
        // 从观看记录中计算
        for (WatchRecord record : records) {
            Anime anime = animeRepository.findById(record.getAnimeId()).orElse(null);
            if (anime == null) continue;
            
            String category = anime.getTags();
            if (category == null) continue;
            
            double baseScore = record.getStatus() == 2 ? 0.8 : 0.3;
            double timeDecay = calculateTimeDecay(record.getCreateTime());
            double score = baseScore * timeDecay;
            
            Arrays.stream(category.split(","))
                    .map(String::trim)
                    .forEach(cat -> categoryScores.merge(cat, score, Double::sum));
        }
        
        // 归一化
        normalizeWeights(categoryScores);
        return categoryScores;
    }
    
    /**
     * 计算标签偏好（带时间衰减）
     */
    private Map<String, Double> calculateTagPreference(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<WatchRecord> records = watchRecordRepository.findByUserId(userId);
        
        Map<String, Double> tagScores = new HashMap<>();
        
        // 从评论中计算
        for (Review review : reviews) {
            if (review.getRating() == null) continue;
            
            Anime anime = animeRepository.findById(review.getAnimeId()).orElse(null);
            if (anime == null || anime.getTags() == null) continue;
            
            double baseScore = review.getRating() / 5.0;
            double timeDecay = calculateTimeDecay(review.getCreateTime());
            double score = baseScore * timeDecay;
            
            Arrays.stream(anime.getTags().split(","))
                    .map(String::trim)
                    .forEach(tag -> tagScores.merge(tag, score, Double::sum));
        }
        
        // 从观看记录中计算
        for (WatchRecord record : records) {
            Anime anime = animeRepository.findById(record.getAnimeId()).orElse(null);
            if (anime == null || anime.getTags() == null) continue;
            
            double baseScore = record.getStatus() == 2 ? 0.8 : 0.3;
            double timeDecay = calculateTimeDecay(record.getCreateTime());
            double score = baseScore * timeDecay;
            
            Arrays.stream(anime.getTags().split(","))
                    .map(String::trim)
                    .forEach(tag -> tagScores.merge(tag, score, Double::sum));
        }
        
        // 归一化
        normalizeWeights(tagScores);
        return tagScores;
    }
    
    /**
     * 时间衰减函数（越近的行为权重越高）
     */
    private double calculateTimeDecay(LocalDateTime actionTime) {
        long daysDiff = ChronoUnit.DAYS.between(actionTime, LocalDateTime.now());
        return Math.exp(-daysDiff / 30.0); // 30 天半衰期
    }
    
    /**
     * 归一化权重
     */
    private void normalizeWeights(Map<String, Double> weights) {
        if (weights.isEmpty()) return;
        
        double total = weights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        if (total > 0) {
            weights.replaceAll((k, v) -> v / total);
        }
    }
    
    /**
     * 创建默认用户画像
     */
    private UserProfile createDefaultProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        // 已移除 User 关联字段，直接设置 userId 即可
        profile.setFavoriteCategories("{}");
        profile.setFavoriteTags("{}");
        profile.setTotalViews(0);
        profile.setTotalFavorites(0);
        profile.setAvgScore(0.0);
        return profile;
    }
    
    /**
     * 获取最近24小时内有活跃行为的用户ID列表
     */
    public List<Long> getActiveUserIds() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        
        // 从评论表中获取最近24小时有评论的用户
        List<Long> reviewUserIds = reviewRepository.findUserIdsByCreateTimeAfter(twentyFourHoursAgo);
        
        // 从观看记录表中获取最近24小时有观看记录的用户
        List<Long> watchUserIds = watchRecordRepository.findUserIdsByCreateTimeAfter(twentyFourHoursAgo);
        
        // 合并去重
        Set<Long> activeUserIds = new HashSet<>();
        activeUserIds.addAll(reviewUserIds);
        activeUserIds.addAll(watchUserIds);
        
        return new ArrayList<>(activeUserIds);
    }

    /**
     * 将动漫加入用户想看列表
     * @param userId 用户ID
     * @param animeId 动漫ID
     */
    @Transactional
    public void addWishAnime(Long userId, Long animeId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        
        List<Long> wishList = new ArrayList<>();
        
        // 解析现有 wishList
        if (profile.getWishList() != null && !profile.getWishList().trim().isEmpty()) {
            try {
                wishList = objectMapper.readValue(
                    profile.getWishList(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class)
                );
            } catch (Exception e) {
                log.warn("解析 wishList 失败，使用空列表：{}", e.getMessage());
            }
        }
        
        // 添加新 animeId（去重）
        if (!wishList.contains(animeId)) {
            wishList.add(animeId);
        }
        
        // 保存回数据库
        try {
            profile.setWishList(objectMapper.writeValueAsString(wishList));
            userProfileRepository.save(profile);
            
            // 清除缓存
            String cacheKey = "user_profile:" + userId;
            cacheService.delete(cacheKey);
            
            log.info("用户 {} 已将动漫 {} 加入想看列表，当前总数：{}", userId, animeId, wishList.size());
        } catch (Exception e) {
            log.error("保存 wishList 失败：{}", e.getMessage(), e);
            throw new RuntimeException("更新想看列表失败", e);
        }
    }
}
