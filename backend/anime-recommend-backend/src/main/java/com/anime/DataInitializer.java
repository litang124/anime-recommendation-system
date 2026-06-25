package com.anime;

import com.anime.entity.Anime;
import com.anime.entity.AnimeSchedule;
import com.anime.entity.User;
import com.anime.repository.AnimeRepository;
import com.anime.repository.AnimeScheduleRepository;
import com.anime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AnimeRepository animeRepository;
    private final AnimeScheduleRepository scheduleRepository;

    @Override
    public void run(String... args) throws Exception {
        // 如果用户表为空，初始化测试数据
        if (userRepository.count() == 0) {
            initUsers();
        }

        // 如果动漫表为空，初始化测试数据
        if (animeRepository.count() == 0) {
            initAnime();
        }

        // 如果日历表为空，初始化测试数据
        if (scheduleRepository.count() == 0) {
            initSchedules();
        }
    }

    private void initUsers() {
        User user1 = new User();
        user1.setUsername("admin");
        user1.setNickname("系统管理员");
        user1.setAvatarUrl("https://api.multiavatar.com/admin.png");
        user1.setEmail("admin@anime.com");
        user1.setGender(1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setNickname("动漫爱好者");
        user2.setAvatarUrl("https://api.multiavatar.com/user1.png");
        user2.setEmail("user1@anime.com");
        user2.setGender(0);

        User user3 = new User();
        user3.setUsername("user2");
        user3.setNickname("新番追更");
        user3.setAvatarUrl("https://api.multiavatar.com/user2.png");
        user3.setEmail("user2@anime.com");
        user3.setGender(2);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("✅ 初始化用户数据完成");
    }

    private void initAnime() {
        String[] animeData = {
                "鬼灭之刃,https://example.com/cover1.jpg,讲述家人被鬼杀害的少年灶门炭治郎，为了让变成鬼的妹妹恢复人类身份，进入鬼杀队的故事,9.5,26,1,热血,战斗,奇幻,2019",
                "咒术回战,https://example.com/cover2.jpg,高中生虎杖悠仁吞下特级咒物「两面宿傩的手指」后，成为咒术师的故事,9.0,24,1,热血,战斗,校园,2020",
                "间谍过家家,https://example.com/cover3.jpg,间谍、杀手和超能力者组成的临时家庭，在互相隐瞒真实身份的情况下共同生活的故事,9.2,25,0,搞笑,家庭,日常,2022",
                "孤独摇滚！,https://example.com/cover4.jpg,极度怕生的少女后藤一里组建乐队，努力克服社交恐惧症的故事,9.3,12,1,音乐,校园,搞笑,2022",
                "葬送的芙莉莲,https://example.com/cover5.jpg,精灵魔法使芙莉莲在勇者死后踏上了解人类的旅程,9.7,28,0,奇幻,冒险,治愈,2023"
        };

        for (String data : animeData) {
            String[] parts = data.split(",");
            Anime anime = new Anime();
            anime.setTitle(parts[0]);
            anime.setCoverUrl(parts[1]);
            anime.setDescription(parts[2]);
            anime.setScore(Double.parseDouble(parts[3]));
            anime.setEpisodeCount(Integer.parseInt(parts[4]));
            anime.setStatus(Integer.parseInt(parts[5]));
            anime.setTags(String.join(",", parts[6], parts[7], parts[8]));
            anime.setHeat((int)(Math.random() * 10000) + 1000);
            anime.setReleaseYear(Integer.parseInt(parts[9]));

            animeRepository.save(anime);
        }

        System.out.println("✅ 初始化动漫数据完成");
    }

    private void initSchedules() {
        // 获取所有动漫
        var animeList = animeRepository.findAll();

        LocalDate today = LocalDate.now();

        for (Anime anime : animeList) {
            // 为每部动漫创建未来3天的播放计划
            for (int i = 0; i < 3; i++) {
                AnimeSchedule schedule = new AnimeSchedule();
                schedule.setAnimeId(anime.getId());
                schedule.setEpisode(i + 1);
                schedule.setAirTime(LocalDateTime.of(today.plusDays(i), LocalTime.of(20, 0)));
                schedule.setPlatform("哔哩哔哩");
                schedule.setPlatformUrl("https://www.bilibili.com");
                schedule.setStatus(0);

                scheduleRepository.save(schedule);
            }
        }

        System.out.println("✅ 初始化新番日历数据完成");
    }
}