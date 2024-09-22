package com.charlie.tinyurl.controller;

import com.charlie.tinyurl.model.UserPreference;
import com.charlie.tinyurl.model.UserPreferences;
import com.charlie.tinyurl.service.UserPreferencesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("userPreferences")
@Slf4j
@AllArgsConstructor
public class UserPreferenceController {

    private final UserPreferencesRepository userPreferencesRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserPreferences(@PathVariable String id) {
        Optional<UserPreferences> userPreferences = userPreferencesRepository.findById(id);
        if (userPreferences.isPresent())
            return ResponseEntity.ok().body(userPreferences.get().getUserPreference());
        else {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
    }


    @PostMapping("/{id}")
    public ResponseEntity<?> saveUserPreferences(@PathVariable String id, @RequestBody Set<UserPreference> userPreferences) {
        try {
            if (id == null || userPreferences == null || userPreferences.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid ID or preferences provided");
            }
            Optional<UserPreferences> exists = userPreferencesRepository.findById(id);

            if (exists.isPresent()) {
                if (exists.get().getUserPreference().equals(userPreferences)) {
                    return ResponseEntity.ok().body(userPreferences);
                }
            }
            UserPreferences redisPreferences = new UserPreferences(id, userPreferences);
            UserPreferences savedPreferences = userPreferencesRepository.save(redisPreferences);
            return ResponseEntity.ok(savedPreferences.getUserPreference());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while saving preferences");
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCache() {
        userPreferencesRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
