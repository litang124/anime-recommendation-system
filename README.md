# Anime Recommendation System

> WeChat Mini Program for anime recommendation based on collaborative filtering

## Tech Stack

- **Backend**: Spring Boot 2.7.18 + JPA + MySQL 8.0 + Redis
- **Frontend**: WeChat Mini Program (Native)

## Core Features

- WeChat one-click login
- Hybrid recommendation algorithm (User-CF + Item-CF + User Profile + Hot)
- Anime search (title/tags)
- Rating & review community
- New anime calendar

## Project Structure

```
anime-recommend-system（后端）/backend/anime-recommend-backend/  # Spring Boot backend
miniprogram-1（微信小程序前端）/  # WeChat Mini Program
anime_recommend.sql  # Database SQL script
```

## How to Run

### Backend

1. Import `anime_recommend.sql` to MySQL
2. Configure environment variables:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=anime_recommend
   DB_USERNAME=root
   DB_PASSWORD=your_password
   WECHAT_APP_ID=your_app_id
   WECHAT_APP_SECRET=your_app_secret
   ```
3. Ensure Redis is running
4. Run `AnimeRecommendApplication.java`

### Frontend

1. Open WeChat DevTools
2. Import `miniprogram-1（微信小程序前端）` directory
3. Configure your AppID in `project.config.json`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| DB_HOST | MySQL host | localhost |
| DB_PORT | MySQL port | 3306 |
| DB_NAME | Database name | anime_recommend |
| DB_USERNAME | Database username | root |
| DB_PASSWORD | Database password | (required) |
| WECHAT_APP_ID | WeChat AppID | (required) |
| WECHAT_APP_SECRET | WeChat AppSecret | (required) |

## Notes

- Configure your own WeChat AppID and AppSecret
- Redis must be running before starting backend
- Java 8+ required
- MySQL 8.0+ recommended
