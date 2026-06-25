package com.anime.service;

import com.anime.entity.Anime;
import com.anime.entity.Recommendation;
import com.anime.entity.UserProfile;
import com.anime.repository.AnimeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.AbstractMap;

import com.anime.service.AbTestConfigService;

/**
 * 混合推荐策略服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HybridRecommendService {
    
    private final AbTestConfigService abTestConfigService;
    
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final UserProfileService userProfileService;
    private final AnimeRepository animeRepository;
    
    /**
     * 策略 A：原始混合推荐逻辑（兼容旧版）
     */
    /**
     * A/B 测试路由入口（策略 A）
     */
    private List<Anime> hybridRecommendStrategyA(Long userId, int topN) {
        String strategy = abTestConfigService.getStrategyForUser(userId);
        log.info("用户 {} 分配至 A/B 策略：{}", userId, strategy);
        
        switch (strategy) {
            case "A":
                return doHybridRecommendStrategyA(userId, topN);
            case "B":
                return hybridRecommendStrategyB(userId, topN);
            default:
                return doHybridRecommendStrategyA(userId, topN);
        }
    }

    /**
     * 策略 A：原始混合推荐逻辑（兼容旧版）
     */
    private List<Anime> doHybridRecommendStrategyA(Long userId, int topN) {
        // 1. 获取用户画像（判断用户类型）
        UserProfile profile = userProfileService.getUserProfile(userId);
        
        // 2. 根据用户活跃度动态调整权重（第一步：用户画像算法为最紧急）
        double weightUserCF = 0.1;   // 基于用户的 CF 权重（降权）
        double weightItemCF = 0.1;   // 基于物品的 CF 权重（降权）
        double weightPopular = 0.2;  // 热门推荐权重（保持）
        double weightProfile = 0.6;  // 基于画像的权重（提升至主导）
        
        if (profile.getTotalViews() < 10) {
            // 新用户：降低协同过滤权重，增加热门和画像权重
            log.info("用户 {} 为新用户，调整推荐权重", userId);
            weightUserCF = 0.1;
            weightItemCF = 0.2;
            weightPopular = 0.5;
            weightProfile = 0.2;
        }
        
        // 3. 获取各算法推荐结果
        List<Recommendation> userCFResults = collaborativeFilteringService
                .generateRecommendations(userId, topN * 2);
        List<Recommendation> itemCFResults = collaborativeFilteringService
                .generateRecommendations(userId, topN * 2);
        List<Anime> popularResults = getPopularAnime(topN);
        // 2. 基于用户画像的推荐（主导策略，扩大召回池）
        List<Anime> profileResults = getProfileBasedRecommend(userId, topN * 3, profile);
        
        // 4. 转换为 Anime 列表
        List<Anime> userCFAnimes = recommendationToAnime(userCFResults);
        List<Anime> itemCFAnimes = recommendationToAnime(itemCFResults);
        
        // 5. 加权融合去重，并记录每个动漫的得分来源
        Map<Long, Double> finalScores = new HashMap<>();
        Map<Long, Map<String, Double>> animeScoreBreakdown = new HashMap<>(); // 记录每个动漫的各算法得分
        
        // User-CF 贡献
        for (int i = 0; i < userCFAnimes.size(); i++) {
            Anime anime = userCFAnimes.get(i);
            double positionScore = (userCFAnimes.size() - i) / (double) userCFAnimes.size();
            double weightedScore = positionScore * weightUserCF;
            finalScores.merge(anime.getId(), weightedScore, Double::sum);
            
            animeScoreBreakdown.computeIfAbsent(anime.getId(), k -> new HashMap<>())
                .put("User-CF协同过滤", weightedScore);
        }
        
        // Item-CF 贡献
        for (int i = 0; i < itemCFAnimes.size(); i++) {
            Anime anime = itemCFAnimes.get(i);
            double positionScore = (itemCFAnimes.size() - i) / (double) itemCFAnimes.size();
            double weightedScore = positionScore * weightItemCF;
            finalScores.merge(anime.getId(), weightedScore, Double::sum);
            
            animeScoreBreakdown.computeIfAbsent(anime.getId(), k -> new HashMap<>())
                .put("Item-CF协同过滤", weightedScore);
        }
        
        // 热门贡献
        for (int i = 0; i < popularResults.size(); i++) {
            Anime anime = popularResults.get(i);
            double positionScore = (popularResults.size() - i) / (double) popularResults.size();
            double weightedScore = positionScore * weightPopular;
            finalScores.merge(anime.getId(), weightedScore, Double::sum);
            
            animeScoreBreakdown.computeIfAbsent(anime.getId(), k -> new HashMap<>())
                .put("热门推荐", weightedScore);
        }
        
        // 用户画像贡献
        for (int i = 0; i < profileResults.size(); i++) {
            Anime anime = profileResults.get(i);
            double positionScore = (profileResults.size() - i) / (double) profileResults.size();
            double weightedScore = positionScore * weightProfile;
            finalScores.merge(anime.getId(), weightedScore, Double::sum);
            
            animeScoreBreakdown.computeIfAbsent(anime.getId(), k -> new HashMap<>())
                .put("用户画像匹配", weightedScore);
        }
        
        // 6. 排序取 TopN
        List<Long> animeIds = finalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // 7. 为每个动漫确定主导算法（得分最高的）
        Map<Long, String> reasonSources = new HashMap<>();
        for (Long animeId : animeIds) {
            Map<String, Double> breakdown = animeScoreBreakdown.get(animeId);
            if (breakdown != null && !breakdown.isEmpty()) {
                // 找出得分最高的算法
                String dominantAlgorithm = breakdown.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("混合推荐算法");
                reasonSources.put(animeId, dominantAlgorithm);
            }
        }
        
        // 8. 将推荐理由来源存储到ThreadLocal，供RecommendationService使用
        RecommendationReasonContextHolder.setReasons(reasonSources);
        
        // 打印调试日志
        log.info("用户 {} 的推荐算法来源分布: {}", userId, reasonSources.values().stream()
            .collect(Collectors.groupingBy(s -> s, Collectors.counting())));
        
        return animeRepository.findAllById(animeIds);
    }
    
    /**
     * 策略 B：用户画像加权增强版（高权重标签匹配 + 时间衰减）
     */
    private List<Anime> hybridRecommendStrategyB(Long userId, int topN) {
        UserProfile profile = userProfileService.getUserProfile(userId);
            
        // 1. 获取热门动漫（基础召回）
        List<Anime> popularResults = getPopularAnime(topN * 2);
            
        // 2. 基于用户画像的深度推荐（主策略）
        List<Anime> profileResults = getProfileBasedRecommend(userId, topN * 3, profile);
            
        // 3. 加权融合：画像结果占 70%，热门占 30%
        Map<Long, Double> finalScores = new HashMap<>();
            
        addWithWeight(finalScores, profileResults, 0.7);
        addWithWeight(finalScores, popularResults, 0.3);
            
        // 4. 排序取 TopN
        List<Long> animeIds = finalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
        return animeRepository.findAllById(animeIds);
    }
    
    /**
     * 将 Recommendation 转换为 Anime
     */
    private List<Anime> recommendationToAnime(List<Recommendation> recommendations) {
        Set<Long> animeIds = recommendations.stream()
                .map(Recommendation::getAnimeId)
                .collect(Collectors.toSet());
        return animeRepository.findAllById(animeIds);
    }
    
    /**
     * 按权重添加推荐结果
     */
    private void addWithWeight(Map<Long, Double> scores, List<Anime> animes, double weight) {
        for (int i = 0; i < animes.size(); i++) {
            Anime anime = animes.get(i);
            double positionScore = (animes.size() - i) / (double) animes.size();
            scores.merge(anime.getId(), positionScore * weight, Double::sum);
        }
    }
    
    /**
     * 获取热门动漫
     */
    private List<Anime> getPopularAnime(int limit) {
        return animeRepository.findTop10ByOrderByHeatDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 基于用户画像的推荐
     */
    private List<Anime> getProfileBasedRecommend(Long userId, int limit, UserProfile profile) {
        log.info("基于用户画像为用户 {} 生成推荐", userId);
        
        // 1. 解析用户偏好数据
        Map<String, Double> tagPreferences = parseUserProfilePreferences(profile.getFavoriteTags());
        Map<String, Double> categoryPreferences = parseUserProfilePreferences(profile.getFavoriteCategories());
        
        // 2. 计算用户画像新鲜度衰减因子
        double freshnessFactor = calculateFreshnessFactor(profile);
        
        // 3. 获取所有动漫
        List<Anime> allAnimes = animeRepository.findAll();
        
        // 4. 计算每个动漫的匹配度得分
        List<Map.Entry<Anime, Double>> scoredAnimes = new ArrayList<>();
        
        for (Anime anime : allAnimes) {
            double score = 0.0;
            
            // 标签匹配度（权重 0.6）
            if (anime.getTags() != null && !anime.getTags().isEmpty()) {
                String[] animeTags = anime.getTags().split(",");
                for (String animeTag : animeTags) {
                    String trimmedTag = animeTag.trim();
                    if (!trimmedTag.isEmpty()) {
                        // 精确匹配
                        double tagWeight = tagPreferences.getOrDefault(trimmedTag, 0.0);
                        score += tagWeight * 0.6 * freshnessFactor;
                        
                        // 模糊匹配（包含关系）
                        if (tagWeight == 0.0) {
                            for (Map.Entry<String, Double> entry : tagPreferences.entrySet()) {
                                if (entry.getKey().toLowerCase().contains(trimmedTag.toLowerCase()) ||
                                    trimmedTag.toLowerCase().contains(entry.getKey().toLowerCase())) {
                                    score += entry.getValue() * 0.2 * freshnessFactor;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            // 分类匹配度（权重 0.3）
            if (anime.getTags() != null && !anime.getTags().isEmpty()) {
                String[] animeCategories = anime.getTags().split(",");
                for (String animeCategory : animeCategories) {
                    String trimmedCategory = animeCategory.trim();
                    if (!trimmedCategory.isEmpty()) {
                        double categoryWeight = categoryPreferences.getOrDefault(trimmedCategory, 0.0);
                        score += categoryWeight * 0.3 * freshnessFactor;
                    }
                }
            }
            
            // 年份匹配度（新番优先，权重 0.1）
            if (anime.getReleaseYear() != null) {
                int currentYear = java.time.Year.now().getValue();
                int yearDiff = Math.abs(currentYear - anime.getReleaseYear());
                if (yearDiff <= 1) {
                    score += 0.1; // 最新年番
                } else if (yearDiff <= 3) {
                    score += 0.05; // 近年番
                }
            }
            
            // 评分加权（高质量内容，权重 0.1）
            if (anime.getScore() != null && anime.getScore() >= 7.5) {
                score += (anime.getScore() - 7.5) * 0.05; // 7.5-10分区间加权
            }
            
            // 用户活跃度加权（新用户额外加权）
            if (profile.getTotalViews() < 5) {
                score *= 1.5; // 新用户更倾向于热门和新番
            }
            
            scoredAnimes.add(new AbstractMap.SimpleEntry<>(anime, score));
        }
        
        // 5. 按得分排序并返回前N个
        return scoredAnimes.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算用户画像新鲜度衰减因子
     * 基于用户画像最后更新时间
     */
    private double calculateFreshnessFactor(UserProfile profile) {
        if (profile.getUpdateTime() == null) {
            return 0.5; // 默认衰减因子
        }
        
        long daysSinceUpdate = java.time.temporal.ChronoUnit.DAYS.between(
                profile.getUpdateTime(), java.time.LocalDateTime.now());
        
        // 使用指数衰减：f(t) = e^(-t/7)，7天半衰期
        double decayFactor = Math.exp(-daysSinceUpdate / 7.0);
        
        // 确保衰减因子在合理范围内 [0.3, 1.0]
        return Math.max(0.3, Math.min(1.0, decayFactor));
    }
    
    /**
     * 解析用户画像偏好JSON字符串（增强 null-safe）
     */
    protected Map<String, Double> parseUserProfilePreferences(String preferencesJson) {
        if (preferencesJson == null || preferencesJson.trim().isEmpty() || "{}".equals(preferencesJson.trim())) {
            return new HashMap<>();
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(preferencesJson, new TypeReference<Map<String, Double>>() {});
        } catch (Exception e) {
            log.warn("解析用户画像偏好失败: {}，使用空偏好", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 公共推荐入口方法（供 RecommendationService 调用）
     */
    public List<Anime> hybridRecommend(Long userId, int topN) {
        return hybridRecommendStrategyA(userId, topN);
    }
}
