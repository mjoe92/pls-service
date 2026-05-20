package de.vw.paso.configuration;

import de.vw.paso.logic.user.RequestDataKey;
import de.vw.paso.service.accesstoken.AccessTokenDecoderService;
import de.vw.paso.util.RequestData;
import de.vw.paso.utility.StringConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for binding userdata to the thread for later use.
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterceptor.class);
    private static final String AUTHORIZATION_HEADER_FORMAT = "^Bearer .+$";
    private static final int AUTHORIZATION_HEADER_TOKEN_PART = 1;

    private final AccessTokenDecoderService tokenDecoderService;

    UserInterceptor(AccessTokenDecoderService tokenDecoderService) {
        this.tokenDecoderService = tokenDecoderService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LOGGER.debug("Extracting UserId from Request");
        String userId = extractUserId(request);

        if (userId == null) {
            LOGGER.debug("No auth data. Expected for Token Request");
        } else {
            LOGGER.debug("Setting auth data");
            RequestData.setRequestData(RequestDataKey.USERID, userId);
        }

        return true;
    }

    private String extractUserId(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isValidAuthorizationHeader(authorizationHeader)) {
            String[] authorizationHeaderParts = authorizationHeader.split(StringConstant.SPACE);
            String token = authorizationHeaderParts[AUTHORIZATION_HEADER_TOKEN_PART];

            Jwt jwt = tokenDecoderService.decode(token);
            return jwt.getSubject();
        }

        LOGGER.debug("Invalid Authorization Header. Expected for Token Request");
        return null;
    }

    private boolean isValidAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader != null && !authorizationHeader.isBlank() && authorizationHeader.matches(
                AUTHORIZATION_HEADER_FORMAT);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        RequestData.clearRequestData();
    }
}