package com.anime.service;

import com.anime.entity.Anime;
import com.anime.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 定时任务服务 - 离线计算推荐相关数据
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    private final AnimeRepository animeRepository;
    private final CacheService cacheService;
    
    /**
     * 每天凌晨 3 点计算物品相似度矩阵
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void computeItemSimilarityMatrix() {
        log.info("开始计算物品相似度矩阵");
        
        List<Anime> allAnime = animeRepository.findAll();
        Map<Long, Map<Long, Double>> similarityMatrix = new HashMap<>();
        
        for (Anime anime1 : allAnime) {
            Map<Long, Double> similarities = new HashMap<>();
            for (Anime anime2 : allAnime) {
                if (anime1.getId().equals(anime2.getId())) continue;
                
                double sim = calculateAnimeSimilarity(anime1, anime2);
                if (sim > 0.2) { // 相似度阈值
                    similarities.put(anime2.getId(), sim);
                }
            }
            
            if (!similarities.isEmpty()) {
                similarityMatrix.put(anime1.getId(), similarities);
            }
        }
        
        // 存入 Redis，过期时间 7 天
        cacheService.set("item_similarity_matrix", similarityMatrix, 7, java.util.concurrent.TimeUnit.DAYS);
        
        log.info("物品相似度矩阵计算完成，共 {} 个动漫", allAnime.size());
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
}
