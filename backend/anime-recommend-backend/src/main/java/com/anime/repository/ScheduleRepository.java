package com.anime.repository;

import com.anime.entity.AnimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<AnimeSchedule, Long> {
    List<AnimeSchedule> findByAnimeId(Long animeId);

    @Query(value = "SELECT s.* FROM anime_schedule s LEFT JOIN anime a ON s.anime_id = a.id WHERE DATE(s.air_time) = ?1 ORDER BY s.air_time", nativeQuery = true)
    List<AnimeSchedule> findByDate(LocalDate date);

    @Query(value = "SELECT s.* FROM anime_schedule s LEFT JOIN anime a ON s.anime_id = a.id WHERE DATE(s.air_time) BETWEEN ?1 AND ?2 ORDER BY s.air_time", nativeQuery = true)
    List<AnimeSchedule> findByDateRange(LocalDate start, LocalDate end);
}