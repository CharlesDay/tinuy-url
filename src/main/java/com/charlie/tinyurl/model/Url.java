package com.charlie.tinyurl.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Cacheable(value = "Url")
@RedisHash("Urls")
@Builder
@Getter
public class Url implements Serializable {
    private String id;
    private String longUrl;
    private String tinyUrl;
}
