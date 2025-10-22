package com.project.paysnap.service.impl;
import com.project.paysnap.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_TOKEN = "blacklist:";
// blacklist service user logout olubsa onu  blackliste alir blackliste alindiginie jwt auth filterde yoxlayiriq

    @Override
    public void blacklistToken(String token, Long expirationMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_TOKEN + token, "true", expirationMillis, TimeUnit.MILLISECONDS);

    }

    @Override
    public boolean isTokenBlacklist(String token) {

        return redisTemplate.hasKey(token);
    }
}
