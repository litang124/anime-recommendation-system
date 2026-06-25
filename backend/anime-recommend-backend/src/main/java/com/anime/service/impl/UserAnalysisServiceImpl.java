package com.anime.service.impl;

import com.anime.entity.Anime;
import com.anime.entity.Review;
import com.anime.entity.WatchRecord;
import com.anime.repository.AnimeRepository;
import com.anime.repository.ReviewRepository;
import com.anime.repository.WatchRecordRepository;
import com.anime.service.CacheService;
import com.anime.service.UserAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户画像分析服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalysisServiceImpl implements UserAnalysisService {

    private final WatchRecordRepository watchRecordRepository;
    private final ReviewRepository reviewRepository;
    private final AnimeRepository animeRepository;
    private final CacheService cacheService;

    /**
     * 缓存过期时间：30 分钟
     */
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        String cacheKey = "user:profile:" + userId;
        
        // 1. 先查缓存
        if (cacheService.hasKey(cacheKey)) {
            log.info("命中用户画像缓存：userId={}", userId);
            return (Map<String, Object>) cacheService.get(cacheKey);
        }
        
        // 2. 缓存未命中，查询数据库
        log.info("未命中缓存，从数据库查询：userId={}", userId);
        Map<String, Object> profile = new HashMap<>();
        
        // 1. 获取观看统计
        profile.put("statistics", getWatchStatistics(userId));
        
        // 2. 获取偏好标签
        profile.put("preferences", getUserPreferences(userId));
        
        // 3. 获取评分分布
        profile.put("ratingDistribution", getRatingDistribution(userId));
        
        // 4. 获取活跃时间
        profile.put("activeTimeSlots", getActiveTimeSlots(userId));
        
        // 5. 计算用户等级
        profile.put("userLevel", calculateUserLevel(userId));
        
        // 3. 写入缓存
        cacheService.set(cacheKey, profile, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return profile;
    }

    @Override
    public Map<String, Object> getWatchStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<WatchRecord> allRecords = watchRecordRepository.findByUserId(userId);
        
        if (allRecords.isEmpty()) {
            return createEmptyStats();
        }
        
        // 总观看数
        int totalWatched = allRecords.size();
        
        // 已完成数
        long completed = allRecords.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 2)
                .count();
        
        // 在看数
        long watching = allRecords.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 1)
                .count();
        
        // 想看数
        long planToWatch = allRecords.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 0)
                .count();
        
        // 总观看集数
        int totalEpisodes = allRecords.stream()
                .mapToInt(r -> r.getWatchedEpisodes() != null ? r.getWatchedEpisodes() : 0)
                .sum();
        
        // 获取所有动漫的年份分布
        List<Integer> years = allRecords.stream()
                .map(WatchRecord::getAnimeId)
                .distinct()
                .map(animeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Anime::getReleaseYear)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // 最早和最晚年份
        Integer earliestYear = years.stream().min(Integer::compareTo).orElse(null);
        Integer latestYear = years.stream().max(Integer::compareTo).orElse(null);
        
        // 统计不同状态的数量
        Map<Integer, Long> statusCount = allRecords.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getStatus() != null ? r.getStatus() : 0,
                        Collectors.counting()
                ));
        
        stats.put("totalWatched", totalWatched);
        stats.put("completed", completed);
        stats.put("watching", watching);
        stats.put("planToWatch", planToWatch);
        stats.put("totalEpisodes", totalEpisodes);
        stats.put("earliestYear", earliestYear);
        stats.put("latestYear", latestYear);
        stats.put("statusDistribution", statusCount);
        
        // 计算平均完成率
        double avgCompletion = totalWatched > 0 ? 
                (double) totalEpisodes / totalWatched : 0.0;
        stats.put("avgCompletionRate", String.format("%.1f", avgCompletion));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getUserPreferences(Long userId) {
        List<Map<String, Object>> preferences = new ArrayList<>();
        
        // 1. 基于观看记录的标签分析
        List<WatchRecord> watchRecords = watchRecordRepository.findByUserId(userId);
        Map<String, Long> tagFrequency = new HashMap<>();
        
        for (WatchRecord record : watchRecords) {
            Optional<Anime> animeOpt = animeRepository.findById(record.getAnimeId());
            if (animeOpt.isPresent()) {
                Anime anime = animeOpt.get();
                if (anime.getTags() != null && !anime.getTags().isEmpty()) {
                    // 分割标签（假设用逗号分隔）
                    String[] tags = anime.getTags().split("[,，]");
                    for (String tag : tags) {
                        String trimmedTag = tag.trim();
                        if (!trimmedTag.isEmpty()) {
                            tagFrequency.merge(trimmedTag, 1L, Long::sum);
                        }
                    }
                }
            }
        }
        
        // 2. 基于评分的高分偏好
        List<Review> reviews = reviewRepository.findByUserId(userId);
        Map<String, Double> highRatedTags = new HashMap<>();
        
        for (Review review : reviews) {
            if (review.getRating() != null && review.getRating() >= 4.0) {
                Optional<Anime> animeOpt = animeRepository.findById(review.getAnimeId());
                if (animeOpt.isPresent()) {
                    Anime anime = animeOpt.get();
                    if (anime.getTags() != null && !anime.getTags().isEmpty()) {
                        String[] tags = anime.getTags().split("[,，]");
                        for (String tag : tags) {
                            String trimmedTag = tag.trim();
                            if (!trimmedTag.isEmpty()) {
                                highRatedTags.merge(trimmedTag, review.getRating(), Double::sum);
                            }
                        }
                    }
                }
            }
        }
        
        // 3. 整合结果，排序取前 10
        Map<String, Double> preferenceScore = new HashMap<>();
        
        // 观看频率得分
        tagFrequency.forEach((tag, count) -> 
                preferenceScore.put(tag, count.doubleValue() * 1.0));
        
        // 高评分加成
        highRatedTags.forEach((tag, score) -> 
                preferenceScore.merge(tag, score, Double::sum));
        
        // 排序并取前 10
        preferenceScore.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    Map<String, Object> pref = new HashMap<>();
                    pref.put("tag", entry.getKey());
                    pref.put("score", entry.getValue());
                    pref.put("level", getPreferenceLevel(entry.getValue()));
                    preferences.add(pref);
                });
        
        return preferences;
    }

    @Override
    public Map<String, Object> getRatingDistribution(Long userId) {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Review> reviews = reviewRepository.findByUserId(userId);
        
        if (reviews.isEmpty()) {
            distribution.put("total", 0);
            distribution.put("average", 0.0);
            distribution.put("distribution", new HashMap<>());
            return distribution;
        }
        
        // 总评分数
        int total = reviews.size();
        
        // 平均分
        double average = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating())
                .average()
                .orElse(0.0);
        
        // 分数段分布
        Map<String, Long> rangeDistribution = reviews.stream()
                .filter(r -> r.getRating() != null)
                .collect(Collectors.groupingBy(
                        r -> {
                            double rating = r.getRating();
                            if (rating >= 4.5) return "5 星";
                            if (rating >= 3.5) return "4 星";
                            if (rating >= 2.5) return "3 星";
                            if (rating >= 1.5) return "2 星";
                            return "1 星";
                        },
                        Collectors.counting()
                ));
        
        distribution.put("total", total);
        distribution.put("average", String.format("%.1f", average));
        distribution.put("distribution", rangeDistribution);
        distribution.put("highest", reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating())
                .max()
                .orElse(0.0));
        distribution.put("lowest", reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating())
                .min()
                .orElse(0.0));
        
        return distribution;
    }

    @Override
    public Map<String, Object> getActiveTimeSlots(Long userId) {
        Map<String, Object> activeSlots = new HashMap<>();
        
        List<WatchRecord> records = watchRecordRepository.findByUserId(userId);
        
        if (records.isEmpty()) {
            activeSlots.put("mostActiveDay", "无数据");
            activeSlots.put("mostActiveHour", "无数据");
            activeSlots.put("dayDistribution", new HashMap<>());
            activeSlots.put("hourDistribution", new HashMap<>());
            return activeSlots;
        }
        
        // 按星期统计
        Map<DayOfWeek, Long> dayDistribution = records.stream()
                .filter(r -> r.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreateTime().getDayOfWeek(),
                        Collectors.counting()
                ));
        
        // 按小时统计
        Map<Integer, Long> hourDistribution = records.stream()
                .filter(r -> r.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreateTime().getHour(),
                        Collectors.counting()
                ));
        
        // 找出最活跃的星期
        DayOfWeek mostActiveDay = dayDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(DayOfWeek.MONDAY);
        
        // 找出最活跃的小时
        Integer mostActiveHour = hourDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
        
        // 转换为中文显示
        Map<String, Long> dayDisplay = new HashMap<>();
        dayDistribution.forEach((day, count) -> 
                dayDisplay.put(day.getDisplayName(TextStyle.FULL, Locale.CHINA), count));
        
        activeSlots.put("mostActiveDay", mostActiveDay.getDisplayName(TextStyle.FULL, Locale.CHINA));
        activeSlots.put("mostActiveHour", mostActiveHour + ":00");
        activeSlots.put("dayDistribution", dayDisplay);
        activeSlots.put("hourDistribution", hourDistribution);
        
        return activeSlots;
    }

    // ===== 辅助方法 =====
    
    private Map<String, Object> createEmptyStats() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("totalWatched", 0);
        empty.put("completed", 0);
        empty.put("watching", 0);
        empty.put("planToWatch", 0);
        empty.put("totalEpisodes", 0);
        empty.put("earliestYear", null);
        empty.put("latestYear", null);
        empty.put("statusDistribution", new HashMap<>());
        empty.put("avgCompletionRate", "0.0");
        return empty;
    }
    
    private String getPreferenceLevel(double score) {
        if (score >= 10) return "狂热";
        if (score >= 5) return "喜爱";
        if (score >= 2) return "偏好";
        return "一般";
    }
    
    private String calculateUserLevel(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<WatchRecord> records = watchRecordRepository.findByUserId(userId);
        
        int score = reviews.size() * 10 + records.size() * 5;
        
        if (score >= 500) return "资深漫迷";
        if (score >= 200) return "活跃用户";
        if (score >= 50) return "普通用户";
        return "新手用户";
    }
}
