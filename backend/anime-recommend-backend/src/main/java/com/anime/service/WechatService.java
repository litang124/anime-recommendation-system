package com.anime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序服务
 */
@Service
@Slf4j
public class WechatService {
    
    @Value("${wechat.app-id:}")
    private String appId;
    
    @Value("${wechat.app-secret:}")
    private String appSecret;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 通过code获取openid和session_key
     */
    public Map<String, String> getSessionInfo(String code) throws Exception {
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            appId, appSecret, code
        );
        
        log.info("请求微信API: {}", url);
        
        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);
        
        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
            throw new RuntimeException("微信接口调用失败: " + errMsg);
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("openid", jsonNode.get("openid").asText());
        result.put("session_key", jsonNode.get("session_key").asText());
        
        if (jsonNode.has("unionid")) {
            result.put("unionid", jsonNode.get("unionid").asText());
        }
        
        return result;
    }
    
    /**
     * 生成自定义token（简化版）
     */
    public String generateToken(String openid, String sessionKey) {
        try {
            String data = openid + ":" + sessionKey + ":" + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("生成token失败", e);
            return null;
        }
    }
    
    /**
     * 解密用户信息
     */
    public Map<String, Object> decryptUserInfo(String encryptedData, String iv, String sessionKey) throws Exception {
        byte[] dataByte = Base64.getDecoder().decode(encryptedData);
        byte[] keyByte = Base64.getDecoder().decode(sessionKey);
        byte[] ivByte = Base64.getDecoder().decode(iv);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.DECRYPT_MODE, spec, ivSpec);
        
        byte[] resultByte = cipher.doFinal(dataByte);
        String result = new String(resultByte, StandardCharsets.UTF_8);
        
        return objectMapper.readValue(result, Map.class);
    }
    
    /**
     * 获取access_token（用于发送订阅消息）
     */
    public String getAccessToken() throws Exception {
        String url = String.format(
            "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
            appId, appSecret
        );
        
        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);
        
        if (jsonNode.has("errcode")) {
            throw new RuntimeException("获取access_token失败: " + jsonNode.get("errmsg").asText());
        }
        
        return jsonNode.get("access_token").asText();
    }
    
    /**
     * 发送订阅消息
     */
    public void sendSubscriptionMessage(String openid, String templateId, String page, Map<String, Object> data) throws Exception {
        String accessToken = getAccessToken();
        String url = String.format(
            "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=%s",
            accessToken
        );
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("touser", openid);
        requestBody.put("template_id", templateId);
        if (page != null && !page.trim().isEmpty()) {
            requestBody.put("page", page);
        }
        requestBody.put("data", data);
        
        String response = restTemplate.postForObject(url, requestBody, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);
        
        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            throw new RuntimeException("发送订阅消息失败: " + jsonNode.get("errmsg").asText());
        }
    }
}
