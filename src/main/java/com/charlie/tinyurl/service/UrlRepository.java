package com.charlie.tinyurl.service;


import com.charlie.tinyurl.model.Url;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UrlRepository extends CrudRepository<Url, String> {

    Optional<Url> findByLongUrl(String longId);
}
