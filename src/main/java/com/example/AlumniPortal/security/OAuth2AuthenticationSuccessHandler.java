package com.example.AlumniPortal.security;

import com.example.AlumniPortal.dto.LoginResponse;
import com.example.AlumniPortal.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            LoginResponse loginResponse = authService.processGoogleLogin(email, name);

            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendUrl + "/oauth-success")
                    .queryParam("token", loginResponse.getToken())
                    .queryParam("userId", loginResponse.getUserId())
                    .queryParam("email", loginResponse.getEmail())
                    .queryParam("role", loginResponse.getRole())
                    .queryParam("profileCompleted", loginResponse.isProfileCompleted())
                    .queryParam("firstTimeLogin", loginResponse.isFirstTimeLogin())
                    .build()
                    .encode()
                    .toUriString();

            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception exception) {
            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendUrl + "/login")
                    .queryParam("oauthError", exception.getMessage() == null ? "Google login failed" : exception.getMessage())
                    .build()
                    .encode()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
