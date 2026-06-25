package com.anime.service.impl;

import com.anime.entity.AnimeSchedule;
import com.anime.repository.ScheduleRepository;
import com.anime.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<AnimeSchedule> getByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }

    @Override
    public List<AnimeSchedule> getByDateRange(LocalDate start, LocalDate end) {
        return scheduleRepository.findByDateRange(start, end);
    }

    @Override
    public List<AnimeSchedule> getThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return scheduleRepository.findByDateRange(monday, sunday);
    }

    @Override
    public List<AnimeSchedule> getByAnimeId(Long animeId) {
        return scheduleRepository.findByAnimeId(animeId);
    }

    @Override
    public AnimeSchedule save(AnimeSchedule schedule) {
        if (schedule.getCreateTime() == null) {
            schedule.setCreateTime(LocalDateTime.now());
        }
        return scheduleRepository.save(schedule);
    }

    @Override
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public AnimeSchedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }
}