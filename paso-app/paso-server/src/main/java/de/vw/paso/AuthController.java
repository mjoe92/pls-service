package de.vw.paso;

import de.vw.paso.logic.user.RequestDataKey;
import de.vw.paso.util.RequestData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@Slf4j
public class AuthController {

  @GetMapping("")
  public ResponseEntity<Object> auth() {
    String user = RequestData.getRequestData(RequestDataKey.USERID);
    log.info("Auth: {}", user);
    return ResponseEntity.ok("OK");
  }
}
