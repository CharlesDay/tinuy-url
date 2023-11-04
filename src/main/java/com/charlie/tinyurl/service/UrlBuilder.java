package com.charlie.tinyurl.service;

import com.charlie.tinyurl.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UrlBuilder {

    @Autowired
    UrlRepository urlRepository;

    public Url buildUrl(String longUrl) {
        return createUniqueUrl(longUrl);
    }

    private Url createUniqueUrl(String longUrl) {
        String key = UUID.randomUUID().toString().substring(0, 5);
        String tinyUrl = String.format("http://localhost:8080/%s", key);

        Url createdUrl;
        while (true) {
            createdUrl = Url.builder().id(key).longUrl(longUrl).tinyUrl(tinyUrl).build();
            if (!urlRepository.existsById(createdUrl.getId())) {
                break;
            }
        }
        return createdUrl;
    }
}
