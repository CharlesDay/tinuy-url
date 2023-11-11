package com.charlie.tinyurl.controller;

import com.charlie.tinyurl.model.Url;
import com.charlie.tinyurl.service.UrlBuilder;
import com.charlie.tinyurl.service.UrlRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping()
@Slf4j
public class UrlController {

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    UrlBuilder urlBuilder;

    @PostMapping("/url")
    public String saveUrl(@RequestParam(required = false) String url, Model model, RedirectAttributes redirectAttributes) {
        if (url != null) {
            if (!url.startsWith("http")){
                url = "https://" + url;
            }
            Url builtUrl = urlBuilder.buildUrl(url);
            if (builtUrl == null) {
                model.addAttribute("error", "Unsafe url, it will not be shortened");
                return "url-form";
            }
            Url urlSaved = urlRepository.save(builtUrl);
            redirectAttributes.addFlashAttribute("longUrl", urlSaved.getLongUrl());
            redirectAttributes.addFlashAttribute("shortUrl", urlSaved.getTinyUrl());
            redirectAttributes.addFlashAttribute("error", null);
            return "redirect:/url";
        }
        model.addAttribute("error", "Missing field: url");
        return "url-form";
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

    @GetMapping("/url")
    public String showUrlForm(@ModelAttribute("longUrl") String longUrl,
                              @ModelAttribute("shortUrl") String shortUrl,
                              @ModelAttribute("error") String error,
                              Model model) {
        // Clear the shortUrl attribute to ensure it's not displayed on the initial load

        model.addAttribute("url", longUrl);
        model.addAttribute("error", error);
        model.addAttribute("shortUrl", shortUrl);

        return "url-form";
    }
}
