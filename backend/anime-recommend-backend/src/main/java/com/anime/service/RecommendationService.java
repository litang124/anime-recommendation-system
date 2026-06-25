package com.anime.service;

import com.anime.dto.MiniprogramAnimeDTO;
import com.anime.entity.Anime;
import com.anime.entity.Recommendation;
import com.anime.entity.UserProfile;
import com.anime.repository.AnimeRepository;
import com.anime.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 推荐服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    
    private final RecommendationRepository recommendationRepository;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final HybridRecommendService hybridRecommendService;
    private final UserProfileService userProfileService;
    private final AnimeRepository animeRepository;
    private final CacheService cacheService;
    
    /**
     * 获取用户的推荐列表（标准推荐）
     */
    public List<Anime> getRecommendationsForUser(Long userId, int limit) {
        String cacheKey = "recommendations:user:" + userId;
        
        // 先从缓存获取
        @SuppressWarnings("unchecked")
        List<Anime> cached = (List<Anime>) cacheService.get(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            log.info("从缓存获取用户 {} 的推荐", userId);
            return cached.stream().limit(limit).collect(Collectors.toList());
        }
        
        // 生成新的推荐
        List<Recommendation> recommendations = collaborativeFilteringService
                .generateRecommendations(userId, limit);
        
        // 保存推荐记录
        saveRecommendations(recommendations);
        
        // 获取动漫详情
        List<Anime> animes = recommendations.stream()
                .map(rec -> animeRepository.findById(rec.getAnimeId()).orElse(null))
                .filter(anime -> anime != null)
                .collect(Collectors.toList());
        
        // 缓存 30 分钟
        cacheService.set(cacheKey, animes, 30, TimeUnit.MINUTES);
        
        return animes;
    }
    
    /**
     * 获取用户的推荐列表（混合推荐 - 增强版）
     */
    public List<Anime> getHybridRecommendationsForUser(Long userId, int limit) {
        // TODO: 暂时禁用缓存以调试序列化问题
        // String cacheKey = "recommendations:hybrid:" + userId;
        
        // // 先从缓存获取
        // @SuppressWarnings("unchecked")
        // List<Anime> cached = (List<Anime>) cacheService.get(cacheKey);
        // if (cached != null && !cached.isEmpty()) {
        //     log.info("从缓存获取用户 {} 的混合推荐", userId);
        //     return cached.stream().limit(limit).collect(Collectors.toList());
        // }
        
        // 使用混合推荐算法
        List<Anime> animes = hybridRecommendService.hybridRecommend(userId, limit);
        
        // // 缓存 30 分钟
        // cacheService.set(cacheKey, animes, 30, TimeUnit.MINUTES);
        
        // 更新用户画像
        userProfileService.updateUserProfile(userId);
        
        return animes;
    }
    
    /**
     * 获取用户的混合推荐列表（带推荐理由）
     */
    public List<MiniprogramAnimeDTO> getHybridRecommendationsWithReasons(Long userId, int limit) {
        // 使用混合推荐算法获取动漫列表（会自动设置算法推荐理由到ThreadLocal）
        List<Anime> animes = hybridRecommendService.hybridRecommend(userId, limit);
        
        // 从 ThreadLocal 获取算法层面的推荐理由
        Map<Long, String> algorithmReasons = RecommendationReasonContextHolder.getReasons();
        
        // 基于用户画像生成详细推荐理由
        java.util.Map<Long, String> reasonMap = new java.util.HashMap<>();
        try {
            UserProfile profile = userProfileService.getUserProfile(userId);
            // 获取用户画像偏好
            Map<String, Double> tagPreferences = hybridRecommendService.parseUserProfilePreferences(profile.getFavoriteTags());
            Map<String, Double> categoryPreferences = hybridRecommendService.parseUserProfilePreferences(profile.getFavoriteCategories());
            
            // 获取用户偏好的前3个标签
            List<Map.Entry<String, Double>> topTags = tagPreferences.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
            
            // 为每个动漫生成算法层面的理由
            for (int i = 0; i < animes.size(); i++) {
                Anime anime = animes.get(i);
                StringBuilder reason = new StringBuilder();
                
                // 获取算法来源
                String algorithmSource = algorithmReasons != null ? algorithmReasons.get(anime.getId()) : null;
                
                // 策略1：User-CF 协同过滤
                if ("User-CF协同过滤".equals(algorithmSource)) {
                    reason.append("👥 User-CF协同过滤：与你兴趣相似的用户也喜欢这部动漫");
                    
                    // 补充标签匹配信息
                    if (!topTags.isEmpty() && anime.getTags() != null) {
                        String[] animeTags = anime.getTags().split(",");
                        for (String tag : animeTags) {
                            String trimmedTag = tag.trim();
                            if (tagPreferences.containsKey(trimmedTag)) {
                                reason.append("，且匹配你的兴趣标签「").append(trimmedTag).append("」");
                                break;
                            }
                        }
                    }
                }
                // 策略2：Item-CF 协同过滤
                else if ("Item-CF协同过滤".equals(algorithmSource)) {
                    reason.append("🎬 Item-CF协同过滤：基于你观看过的动漫相似度推荐");
                    
                    // 补充说明
                    if (anime.getScore() != null && anime.getScore() >= 8.0) {
                        reason.append("，评分高达").append(anime.getScore()).append("分");
                    }
                }
                // 策略3：用户画像匹配
                else if ("用户画像匹配".equals(algorithmSource)) {
                    reason.append("🎯 用户画像匹配：");
                    
                    // 详细标签匹配
                    if (anime.getTags() != null && !anime.getTags().isEmpty()) {
                        String[] animeTags = anime.getTags().split(",");
                        List<String> matchedTags = new ArrayList<>();
                        
                        for (String animeTag : animeTags) {
                            String trimmedTag = animeTag.trim();
                            if (!trimmedTag.isEmpty()) {
                                double tagWeight = tagPreferences.getOrDefault(trimmedTag, 0.0);
                                if (tagWeight > 0.3) {
                                    matchedTags.add(String.format("%s(%.0f%%)", trimmedTag, tagWeight * 100));
                                }
                            }
                        }
                        
                        if (!matchedTags.isEmpty()) {
                            reason.append("匹配你的兴趣标签 ").append(String.join("、", matchedTags));
                        } else {
                            reason.append("符合你的观看偏好");
                        }
                    }
                }
                // 策略4：热门推荐
                else if ("热门推荐".equals(algorithmSource)) {
                    reason.append("🔥 热门推荐：");
                    
                    if (anime.getHeat() != null) {
                        reason.append("已有").append(anime.getHeat()).append("人关注");
                    }
                    
                    if (anime.getScore() != null && anime.getScore() >= 8.5) {
                        reason.append("，评分").append(anime.getScore()).append("分的高口碑动漫");
                    }
                    
                    // 补充标签
                    if (!topTags.isEmpty() && anime.getTags() != null) {
                        String[] animeTags = anime.getTags().split(",");
                        for (String tag : animeTags) {
                            String trimmedTag = tag.trim();
                            if (tagPreferences.containsKey(trimmedTag)) {
                                reason.append("，且符合你的「").append(trimmedTag).append("」偏好");
                                break;
                            }
                        }
                    }
                }
                // 策略5：默认算法理由
                else {
                    reason.append("🤖 混合推荐算法：");
                    
                    // 综合分析
                    List<String> factors = new ArrayList<>();
                    
                    if (anime.getScore() != null && anime.getScore() >= 8.0) {
                        factors.add("高评分" + anime.getScore() + "分");
                    }
                    
                    if (anime.getHeat() != null && anime.getHeat() > 5000) {
                        factors.add("人气" + anime.getHeat());
                    }
                    
                    if (!topTags.isEmpty() && anime.getTags() != null) {
                        String[] animeTags = anime.getTags().split(",");
                        for (String tag : animeTags) {
                            String trimmedTag = tag.trim();
                            if (tagPreferences.containsKey(trimmedTag)) {
                                factors.add("标签匹配「" + trimmedTag + "」");
                                break;
                            }
                        }
                    }
                    
                    if (!factors.isEmpty()) {
                        reason.append(String.join("、", factors));
                    } else {
                        reason.append("综合多维度特征智能推荐");
                    }
                }
                
                reasonMap.put(anime.getId(), reason.toString());
            }
        } catch (Exception e) {
            log.warn("生成算法推荐理由失败，使用默认理由", e);
            for (Anime anime : animes) {
                String algoSource = algorithmReasons != null ? algorithmReasons.get(anime.getId()) : null;
                if (algoSource != null) {
                    reasonMap.put(anime.getId(), "🤖 基于" + algoSource + "算法推荐");
                } else {
                    reasonMap.put(anime.getId(), "🤖 混合推荐算法智能推荐");
                }
            }
        } finally {
            // 清除ThreadLocal，防止内存泄漏
            RecommendationReasonContextHolder.clear();
        }
            
        // 转换为 MiniprogramAnimeDTO 并添加推荐理由
        return animes.stream()
                .map(anime -> {
                    MiniprogramAnimeDTO dto = new MiniprogramAnimeDTO(
                            anime.getId(),
                            anime.getTitle(),
                            anime.getCoverUrl(),
                            anime.getScore(),
                            anime.getStatus(),
                            anime.getTags()
                    );
                    dto.setHeat(anime.getHeat());
                    dto.setReleaseYear(anime.getReleaseYear());
                        
                    // 设置算法推荐理由
                    String reason = reasonMap.get(anime.getId());
                    if (reason != null && !reason.trim().isEmpty()) {
                        dto.setRecommendReason(reason);
                    } else {
                        dto.setRecommendReason("🤖 混合推荐算法智能推荐");
                    }
                        
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 保存推荐记录
     */
    @Transactional
    public void saveRecommendations(List<Recommendation> recommendations) {
        recommendationRepository.saveAll(recommendations);
        log.info("保存了 {} 条推荐记录", recommendations.size());
    }
    
    /**
     * 清理过期的推荐记录（7天前）
     */
    @Transactional
    public void cleanOldRecommendations(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        recommendationRepository.deleteByUserIdAndCreateTimeBefore(userId, sevenDaysAgo);
    }
    
    /**
     * 标记推荐为已点击
     */
    @Transactional
    public void markAsClicked(Long recommendationId) {
        recommendationRepository.findById(recommendationId).ifPresent(rec -> {
            rec.setIsClicked(true);
            recommendationRepository.save(rec);
        });
    }
    
    /**
     * 获取推荐历史
     */
    public List<Recommendation> getRecommendationHistory(Long userId) {
        return recommendationRepository.findByUserIdOrderByScoreDesc(userId);
    }
    
    /**
     * 更新用户的推荐（用于用户行为反馈）
     */
    @Transactional
    public void updateRecommendationsForUser(Long userId) {
        log.info("更新用户 {} 的推荐", userId);
        
        // 清理旧的推荐记录
        cleanOldRecommendations(userId);
        
        // 重新生成推荐
        List<Recommendation> recommendations = collaborativeFilteringService
                .generateRecommendations(userId, 20);
        
        // 保存新的推荐记录
        saveRecommendations(recommendations);
        
        // 更新用户画像
        userProfileService.updateUserProfile(userId);
        
        // 清除缓存
        String cacheKey = "recommendations:user:" + userId;
        String hybridCacheKey = "recommendations:hybrid:" + userId;
        cacheService.delete(cacheKey);
        cacheService.delete(hybridCacheKey);
        
        log.info("用户 {} 的推荐已更新", userId);
    }
}
