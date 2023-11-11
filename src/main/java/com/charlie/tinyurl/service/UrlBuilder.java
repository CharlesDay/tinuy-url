package com.charlie.tinyurl.service;

import com.charlie.tinyurl.model.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UrlBuilder {

    UrlRepository urlRepository;

    private final WebClient webClient;

    private final String fraudUrl;

    private final String fraudToken;

    private final String serverBaseUrl;

    @Autowired
    public UrlBuilder(UrlRepository urlRepository, WebClient.Builder webClientBuilder, @Value("${server.fraud.url}") String fraudUrl, @Value("${server.fraud.token}") String fraudToken,@Value("${server.base.url}") String serverBaseUrl) {
        this.urlRepository = urlRepository;
        this.webClient = webClientBuilder.build();
        this.fraudUrl = fraudUrl;
        this.fraudToken = fraudToken;
        this.serverBaseUrl = serverBaseUrl;
    }

    public Url buildUrl(String longUrl) {
        return createUniqueUrl(longUrl);
    }

    private Url createUniqueUrl(String longUrl) {

        Optional<Url> urlByLongId = urlRepository.findByLongUrl(longUrl);
        if (urlByLongId.isPresent()) return urlByLongId.get();

        boolean safeUrl = checkIfSafeUrl(longUrl);

        if (!safeUrl){
            log.error("The url {} is suspicious and will not be created", longUrl);
            return null;
        }

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

    private boolean checkIfSafeUrl(String longUrl) {
        String url = longUrl.substring(6);

        String finalUri = fraudUrl + fraudToken + "/" + url;
        java.util.Map response;
        try {
            response = webClient.get().uri(finalUri).accept(MediaType.ALL).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).retrieve().bodyToMono(java.util.Map.class).block();
        } catch (WebClientResponseException e){
            log.error("An error occurred", e);
            return true;
        }

        if (response != null && response.get("unsafe") instanceof Boolean) {
            return !((boolean) response.get("unsafe"));
        }

        return false;
    }
}
