package de.vw.paso.pls.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class RootController {

  /**
   * Returns the status of the pls. Used by the paso server for checking availability of the pls itself.
   * <p>
   * Can be used to check the status and print the informations to paso web admin for example.
   * <p>
   * Maybe use spring actuator for this.
   *
   * @return status of the pls as json.
   */
  @GetMapping("/")
  public ResponseEntity<Status> status() {
    //For now, just return ok(pls is available).
    return ResponseEntity.ok(new Status("ok"));
  }

  private record Status(String status) { }
}
