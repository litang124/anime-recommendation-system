package com.anime.repository;

import com.anime.entity.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByTitleContaining(String keyword);
    List<Anime> findTop10ByOrderByHeatDesc();
    
    // 分页查询
    Page<Anime> findAll(Pageable pageable);
    Page<Anime> findByTitleContaining(String keyword, Pageable pageable);
    Page<Anime> findByOrderByHeatDesc(Pageable pageable);
    Page<Anime> findByOrderByScoreDesc(Pageable pageable);
}