package de.vw.paso.service.auth;

import de.vw.paso.service.user.UserDTO;

public record AuthenticatedUserDTO(UserDTO user, String pasoJwt) { }
