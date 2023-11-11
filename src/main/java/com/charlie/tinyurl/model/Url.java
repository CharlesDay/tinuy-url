package com.charlie.tinyurl.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Cacheable(value = "Url")
@RedisHash("Urls")
@Builder
@Getter
@Data
public class Url implements Serializable {
    private String id;
    @Indexed
    private String longUrl;
    private String tinyUrl;
}
