package com.anime.controller;

import com.anime.entity.Anime;
import com.anime.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/anime")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Anime anime = animeService.findById(id);
        if (anime != null) {
            // 增加热度
            animeService.increaseHeat(id);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", anime);
        } else {
            response.put("code", 404);
            response.put("message", "动漫不存在");
        }
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        Page<Anime> animePage = animeService.findAllWithPagination(page, size);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", animePage.getContent());
        response.put("total", animePage.getTotalElements());
        response.put("pages", animePage.getTotalPages());
        return response;
    }

    @GetMapping("/hot")
    public Map<String, Object> hot() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Anime> hotAnime = animeService.getHotAnime();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", hotAnime);
        } catch (Exception e) {
            // 缓存失败时，直接从数据库查询
            List<Anime> hotAnime = animeService.findAll().stream()
                    .sorted((a, b) -> Integer.compare(
                            b.getHeat() != null ? b.getHeat() : 0,
                            a.getHeat() != null ? a.getHeat() : 0))
                    .limit(10)
                    .toList();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", hotAnime);
        }
        return response;
    }

    @GetMapping("/new")
    public Map<String, Object> newAnime() {
        Map<String, Object> response = new HashMap<>();
        List<Anime> newAnime = animeService.getNewAnime();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", newAnime);
        return response;
    }

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        List<Anime> results = animeService.search(keyword);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results);
        response.put("count", results.size());
        return response;
    }
    
    @GetMapping("/search/page")
    public Map<String, Object> searchWithPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        Page<Anime> results = animeService.searchWithPagination(keyword, page, size);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results.getContent());
        response.put("total", results.getTotalElements());
        response.put("pages", results.getTotalPages());
        return response;
    }
    
    @GetMapping("/hot/page")
    public Map<String, Object> hotWithPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        Page<Anime> results = animeService.getHotAnimeWithPagination(page, size);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results.getContent());
        response.put("total", results.getTotalElements());
        response.put("pages", results.getTotalPages());
        return response;
    }

    @GetMapping("/tag/{tag}")
    public Map<String, Object> findByTag(@PathVariable String tag) {
        Map<String, Object> response = new HashMap<>();
        List<Anime> results = animeService.findByTag(tag);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results);
        return response;
    }

    @GetMapping("/year/{year}")
    public Map<String, Object> findByYear(@PathVariable Integer year) {
        Map<String, Object> response = new HashMap<>();
        List<Anime> results = animeService.findByYear(year);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results);
        return response;
    }

    @GetMapping("/status/{status}")
    public Map<String, Object> findByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        List<Anime> results = animeService.findByStatus(status);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", results);
        return response;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Anime anime) {
        Map<String, Object> response = new HashMap<>();
        Anime saved = animeService.save(anime);
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", saved);
        return response;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Anime anime) {
        Map<String, Object> response = new HashMap<>();
        Anime existing = animeService.findById(id);
        if (existing != null) {
            // 更新字段
            if (anime.getTitle() != null) existing.setTitle(anime.getTitle());
            if (anime.getCoverUrl() != null) existing.setCoverUrl(anime.getCoverUrl());
            if (anime.getDescription() != null) existing.setDescription(anime.getDescription());
            if (anime.getEpisodeCount() != null) existing.setEpisodeCount(anime.getEpisodeCount());
            if (anime.getStatus() != null) existing.setStatus(anime.getStatus());
            if (anime.getTags() != null) existing.setTags(anime.getTags());
            if (anime.getReleaseYear() != null) existing.setReleaseYear(anime.getReleaseYear());

            Anime updated = animeService.save(existing);
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updated);
        } else {
            response.put("code", 404);
            response.put("message", "动漫不存在");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        animeService.deleteById(id);
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }
}