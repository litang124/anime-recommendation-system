package com.anime.repository;

import com.anime.entity.UserReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserReminderRepository extends JpaRepository<UserReminder, Long> {
    
    List<UserReminder> findByUserId(Long userId);
    
    List<UserReminder> findByUserIdAndStatus(Long userId, Integer status);
    
    UserReminder findByUserIdAndAnimeId(Long userId, Long animeId);
}