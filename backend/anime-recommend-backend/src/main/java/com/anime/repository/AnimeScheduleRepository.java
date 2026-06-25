package com.anime.repository;

import com.anime.entity.AnimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnimeScheduleRepository extends JpaRepository<AnimeSchedule, Long> {

    // 根据动漫ID查找放送计划
    List<AnimeSchedule> findByAnimeId(Long animeId);

    // 根据日期查找放送计划
    @Query("SELECT s FROM AnimeSchedule s WHERE DATE(s.airTime) = ?1 ORDER BY s.airTime")
    List<AnimeSchedule> findByDate(LocalDate date);

    // 根据日期范围查找放送计划
    @Query("SELECT s FROM AnimeSchedule s WHERE s.airTime BETWEEN ?1 AND ?2 ORDER BY s.airTime")
    List<AnimeSchedule> findByDateRange(LocalDate start, LocalDate end);

    // 根据平台查找
    List<AnimeSchedule> findByPlatform(String platform);

    // 根据状态查找
    List<AnimeSchedule> findByStatus(Integer status);

    // 查找未播放的放送计划
    List<AnimeSchedule> findByStatusOrderByAirTimeAsc(Integer status);
    
    // 查找指定时间范围内的放送计划（用于定时提醒）
    List<AnimeSchedule> findByAirTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, Integer status);
}