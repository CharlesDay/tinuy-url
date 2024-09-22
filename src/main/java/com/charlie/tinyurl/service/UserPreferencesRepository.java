package com.charlie.tinyurl.service;

import com.charlie.tinyurl.model.UserPreferences;
import org.springframework.data.repository.CrudRepository;

public interface UserPreferencesRepository extends CrudRepository<UserPreferences, String> {
}
