package com.project.paysnap.service;

public interface BlacklistService {
    public void blacklistToken(String token, Long expirationMillis);

    public boolean isTokenBlacklist(String token);
}
