package com.charlie.tinyurl.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Set;

@Cacheable(value = "UserPreference")
@RedisHash("UserPreferences")
@Data
public class UserPreferences implements Serializable {
    @Indexed
    private final String id;
    private final Set<UserPreference> userPreference;

    public UserPreferences(String id, Set<UserPreference> userPreference) {
        this.id = id;
        this.userPreference = userPreference;
    }
}