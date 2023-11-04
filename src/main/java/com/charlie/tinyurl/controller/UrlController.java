package com.charlie.tinyurl.controller;

import com.charlie.tinyurl.model.IncomingUrl;
import com.charlie.tinyurl.model.Url;
import com.charlie.tinyurl.service.UrlBuilder;
import com.charlie.tinyurl.service.UrlRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping()
@Slf4j
public class UrlController {

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    UrlBuilder urlBuilder;

    @PostMapping
    public ResponseEntity<?> saveUrl(@RequestBody IncomingUrl url) {
        if (url == null || !StringUtils.hasText(url.url())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing field: url");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(urlRepository.save(urlBuilder.buildUrl(url.url())));
    }

    @GetMapping("/{id}")
    public void redirect(HttpServletResponse httpServletResponse, @PathVariable String id) {
        Optional<Url> urlById = urlRepository.findById(id);
        if (urlById.isEmpty())
            httpServletResponse.setStatus(404);
        else {
            httpServletResponse.setHeader("Location", urlById.get().getLongUrl());
            httpServletResponse.setStatus(302);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUrl(@PathVariable String id) {
        if (urlRepository.existsById(id)) {
            urlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();

    }
}
