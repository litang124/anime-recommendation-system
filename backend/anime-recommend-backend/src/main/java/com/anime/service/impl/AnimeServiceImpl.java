package com.anime.service.impl;

import com.anime.entity.Anime;
import com.anime.repository.AnimeRepository;
import com.anime.service.AnimeService;
import com.anime.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeServiceImpl implements AnimeService {

    private final AnimeRepository animeRepository;
    private final CacheService cacheService;

    /**
     * 热门推荐缓存过期时间：10 分钟
     */
    private static final long HOT_ANIME_CACHE_MINUTES = 10;

    @Override
    public Anime save(Anime anime) {
        if (anime.getCreateTime() == null) {
            anime.setCreateTime(LocalDateTime.now());
        }
        return animeRepository.save(anime);
    }

    @Override
    public Anime findById(Long id) {
        return animeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Anime> findAll() {
        return animeRepository.findAll();
    }

    @Override
    public List<Anime> search(String keyword) {
        return animeRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Anime> getHotAnime() {
        String cacheKey = "anime:hot";
        
        // 1. 先查缓存
        if (cacheService.hasKey(cacheKey)) {
            return (List<Anime>) cacheService.get(cacheKey);
        }
        
        // 2. 缓存未命中，查询数据库
        List<Anime> hotAnime = animeRepository.findTop10ByOrderByHeatDesc();
        
        // 3. 写入缓存
        cacheService.set(cacheKey, hotAnime, HOT_ANIME_CACHE_MINUTES, TimeUnit.MINUTES);
        
        return hotAnime;
    }

    @Override
    public List<Anime> getNewAnime() {
        return animeRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    @Override
    public List<Anime> findByTag(String tag) {
        List<Anime> allAnime = findAll();
        return allAnime.stream()
                .filter(anime -> anime.getTags() != null && anime.getTags().contains(tag))
                .toList();
    }

    @Override
    public List<Anime> findByYear(Integer year) {
        return animeRepository.findAll().stream()
                .filter(anime -> anime.getReleaseYear() != null && anime.getReleaseYear().equals(year))
                .toList();
    }

    @Override
    public List<Anime> findByStatus(Integer status) {
        return animeRepository.findAll().stream()
                .filter(anime -> anime.getStatus() != null && anime.getStatus().equals(status))
                .toList();
    }

    @Override
    public Page<Anime> findAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return animeRepository.findAll(pageable);
    }
    
    @Override
    public Page<Anime> searchWithPagination(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return animeRepository.findByTitleContaining(keyword, pageable);
    }
    
    @Override
    public Page<Anime> getHotAnimeWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return animeRepository.findByOrderByHeatDesc(pageable);
    }

    @Override
    @Transactional
    public void increaseHeat(Long animeId) {
        Anime anime = findById(animeId);
        if (anime != null) {
            anime.setHeat(anime.getHeat() + 1);
            animeRepository.save(anime);
        }
    }

    @Override
    @Transactional
    public void updateScore(Long animeId) {
        // 清除热门动漫缓存，以便重新加载更新后的数据
        String cacheKey = "anime:hot";
        if (cacheService.hasKey(cacheKey)) {
            cacheService.delete(cacheKey);
            log.info("清除热门动漫缓存 - animeId: {}", animeId);
        }
    }

    @Override
    public void deleteById(Long id) {
        animeRepository.deleteById(id);
    }
}