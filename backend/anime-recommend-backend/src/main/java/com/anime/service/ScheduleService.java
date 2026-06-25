package com.anime.service;

import com.anime.entity.AnimeSchedule;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<AnimeSchedule> getByDate(LocalDate date);
    List<AnimeSchedule> getByDateRange(LocalDate start, LocalDate end);
    List<AnimeSchedule> getThisWeek();
    List<AnimeSchedule> getByAnimeId(Long animeId);
    AnimeSchedule save(AnimeSchedule schedule);
    void deleteById(Long id);
    
    /**
     * 根据 ID 查找日程
     */
    AnimeSchedule findById(Long id);
}