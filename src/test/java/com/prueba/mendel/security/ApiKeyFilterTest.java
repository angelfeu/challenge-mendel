package com.prueba.mendel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ApiKeyFilterTest {

    private ApiKeyFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyFilter();
        ReflectionTestUtils.setField(filter, "apiKey", "test-secret");
        filterChain = mock(FilterChain.class);
    }

    @Test
    void valid_api_key_passes_through() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/transactions/types/cars");
        request.addHeader(ApiKeyFilter.API_KEY_HEADER, "test-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void missing_api_key_returns_401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/transactions/types/cars");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void wrong_api_key_returns_401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/transactions/types/cars");
        request.addHeader(ApiKeyFilter.API_KEY_HEADER, "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void swagger_path_is_excluded() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui/index.html");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void actuator_path_is_excluded() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void api_docs_path_is_excluded() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api-docs");

        assertTrue(filter.shouldNotFilter(request));
    }
}
