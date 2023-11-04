package com.charlie.tinyurl.service;


import com.charlie.tinyurl.model.Url;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<Url, String> {
}
