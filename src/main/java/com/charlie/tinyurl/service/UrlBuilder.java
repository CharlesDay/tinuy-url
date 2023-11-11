package com.charlie.tinyurl.service;

import com.charlie.tinyurl.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UrlBuilder {

    UrlRepository urlRepository;

    String serverBaseUrl;

    @Autowired
    public UrlBuilder(UrlRepository urlRepository, @Value("${server.base.url}") String serverBaseUrl) {
        this.urlRepository = urlRepository;
        this.serverBaseUrl = serverBaseUrl;
    }

    public Url buildUrl(String longUrl) {
        return createUniqueUrl(longUrl);
    }

    private Url createUniqueUrl(String longUrl) {

        Optional<Url> urlByLongId = urlRepository.findByLongUrl(longUrl);
        if (urlByLongId.isPresent()) return urlByLongId.get();

        String key = UUID.randomUUID().toString().substring(0, 5);
        String tinyUrl = serverBaseUrl + key;

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
