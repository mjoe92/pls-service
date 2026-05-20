package de.vw.paso.service.idplogin;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.UserMapper;
import de.vw.paso.service.accesstoken.AccessTokenIssuerService;
import de.vw.paso.service.auth.AuthRestService;
import de.vw.paso.service.auth.AuthenticatedUserDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.user.domain.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = AuthRestService.URL)
public class AuthRestController implements AuthRestService {

    private final UserManager userManager;
    private final AccessTokenIssuerService accessTokenIssuerService;

    public AuthRestController(UserManager userManager, AccessTokenIssuerService accessTokenIssuerService) {
        this.userManager = userManager;
        this.accessTokenIssuerService = accessTokenIssuerService;
    }

    @Override
    @GetMapping
    @Transactional
    public AuthenticatedUserDTO getPasoJwt(@RequestParam String code) {
        User userFromIdp = userManager.getUserFromIdp(code);
        String pasoJwt = accessTokenIssuerService.issueAccessToken(userFromIdp);
        UserDTO user = UserMapper.toDto(userFromIdp, userFromIdp.getRoles(), userFromIdp.getUserGroups());

        return new AuthenticatedUserDTO(user, pasoJwt);
    }
}
