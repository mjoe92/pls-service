package de.vw.paso.service.auth;

public interface AuthRestService {
  String URL = "/api/auth";

  AuthenticatedUserDTO getPasoJwt(String code);
}
