package com.anime.service;

import com.anime.entity.Anime;
import org.springframework.data.domain.Page;
import java.util.List;

public interface AnimeService {
    Anime save(Anime anime);
    Anime findById(Long id);
    List<Anime> findAll();
    List<Anime> search(String keyword);
    List<Anime> getHotAnime();
    List<Anime> getNewAnime();
    List<Anime> findByTag(String tag);
    List<Anime> findByYear(Integer year);
    List<Anime> findByStatus(Integer status);
    Page<Anime> findAllWithPagination(int page, int size);
    Page<Anime> searchWithPagination(String keyword, int page, int size);
    Page<Anime> getHotAnimeWithPagination(int page, int size);
    void increaseHeat(Long animeId);
    void updateScore(Long animeId);
    void deleteById(Long id);
}