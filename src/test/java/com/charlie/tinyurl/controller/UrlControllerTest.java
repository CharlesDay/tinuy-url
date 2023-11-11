package com.charlie.tinyurl.controller;

import com.charlie.tinyurl.model.Url;
import com.charlie.tinyurl.service.UrlBuilder;
import com.charlie.tinyurl.service.UrlRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlControllerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlBuilder urlBuilder;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSaveUrlWithMissingUrl() {
        // Arrange
        String url = null;

        // Act
        String result = urlController.saveUrl(url, model, redirectAttributes);

        // Assert
        assertEquals("url-form", result);
        verify(model).addAttribute(eq("error"), eq("Missing field: url"));
        verifyNoInteractions(urlRepository, urlBuilder, redirectAttributes);
    }

    @Test
    void testSaveUrlWithValidUrl() {
        // Arrange
        String inputUrl = "http://example.com";
        String expectedLongUrl = "http://example.com";
        String expectedShortUrl = "http://short.url";

        Url urlMock = Url.builder()
                .id("1")
                .tinyUrl(expectedShortUrl)
                .longUrl(expectedLongUrl)
                .build();

        when(urlRepository.save(any())).thenReturn(urlMock);
        when(urlBuilder.buildUrl(inputUrl)).thenReturn(urlMock);

        // Act
        String result = urlController.saveUrl(inputUrl, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/url", result);
        verify(urlBuilder).buildUrl(inputUrl);
        verify(urlRepository).save(any(Url.class));
        verify(redirectAttributes).addFlashAttribute("longUrl", expectedLongUrl);
        verify(redirectAttributes).addFlashAttribute("shortUrl", expectedShortUrl);
        verifyNoMoreInteractions(urlBuilder, urlRepository, redirectAttributes, model);
    }

    @Test
    void testSaveUrlWithHttpsUrl() {
        // Arrange
        String inputUrl = "https://example.com";
        String expectedLongUrl = "https://example.com";
        String expectedShortUrl = "https://short.url";

        Url urlMock = Url.builder()
                .id("1")
                .tinyUrl(expectedShortUrl)
                .longUrl(expectedLongUrl)
                .build();

        when(urlRepository.save(any())).thenReturn(urlMock);
        when(urlBuilder.buildUrl(inputUrl)).thenReturn(urlMock);

        // Act
        String result = urlController.saveUrl(inputUrl, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/url", result);
        verify(urlBuilder).buildUrl(inputUrl);
        verify(urlRepository).save(any(Url.class));
        verify(redirectAttributes).addFlashAttribute("longUrl", expectedLongUrl);
        verify(redirectAttributes).addFlashAttribute("shortUrl", expectedShortUrl);
        verifyNoMoreInteractions(urlBuilder, urlRepository, redirectAttributes, model);
    }

    @Test
    void testRedirectWithValidUrl() {
        // Arrange
        String id = "validId";
        String longUrl = "http://example.com";

        Url urlMock = Url.builder()
                .id(id)
                .tinyUrl("http://short.url")
                .longUrl(longUrl)
                .build();

        when(urlRepository.findById(id)).thenReturn(Optional.of(urlMock));

        // Act
        urlController.redirect(httpServletResponse, id);

        // Assert
        verify(httpServletResponse).setHeader("Location", longUrl);
        verify(httpServletResponse).setStatus(302);
    }

    @Test
    void testRedirectWithInvalidUrl() {
        // Arrange
        String id = "invalidId";

        when(urlRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        urlController.redirect(httpServletResponse, id);

        // Assert
        verify(httpServletResponse).setStatus(404);
        verifyNoMoreInteractions(httpServletResponse);
    }

    @Test
    void testDeleteUrlIfExists() {
        // Arrange
        String id = "existingId";

        when(urlRepository.existsById(id)).thenReturn(true);

        // Act
        ResponseEntity<?> response = urlController.deleteUrl(id);

        // Assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(urlRepository).existsById(id);
        verify(urlRepository).deleteById(id);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void testDeleteUrlIfNotExists() {
        // Arrange
        String id = "nonExistingId";

        when(urlRepository.existsById(id)).thenReturn(false);

        // Act
        ResponseEntity<?> response = urlController.deleteUrl(id);

        // Assert
        assertEquals(ResponseEntity.notFound().build(), response);
        verify(urlRepository).existsById(id);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void testShowUrlFormWithAttributes() {
        // Arrange
        String longUrl = "http://example.com";
        String shortUrl = "http://short.url";

        // Act
        String result = urlController.showUrlForm(longUrl, shortUrl, model);

        // Assert
        assertEquals("url-form", result);
        verify(model).addAttribute("url", longUrl);
        verify(model).addAttribute("shortUrl", shortUrl);
        verifyNoMoreInteractions(model);
    }

    @Test
    void testShowUrlFormWithoutAttributes() {
        // Act
        String result = urlController.showUrlForm(null, null, model);

        // Assert
        assertEquals("url-form", result);
        verify(model).addAttribute("shortUrl", null);
        verify(model).addAttribute("url", null);
        verifyNoMoreInteractions(model);
    }
}