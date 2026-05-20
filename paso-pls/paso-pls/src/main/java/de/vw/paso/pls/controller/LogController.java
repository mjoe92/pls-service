package de.vw.paso.pls.controller;

import de.vw.paso.pls.LogService;
import de.vw.paso.pls.repository.PlsLog;
import de.vw.paso.pls.repository.PlsLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(path = "/log")
@AllArgsConstructor
public class LogController {

  private LogService logService;
  private PlsLogRepository plsLogRepository;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PlsLog>> listQueue() {
    return ResponseEntity.ok(plsLogRepository.findAll());
  }

  @GetMapping(value = "/show", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> showLog(@RequestParam(name = "start", required = false) LocalDateTime start) {
    if (start == null) {
      start = LocalDateTime.now().minusHours(2);
    }
    StringBuilder sb = new StringBuilder();
    plsLogRepository.findByLogTimeGreaterThan(start).stream().sorted(Comparator.comparing(PlsLog::getLogTime)).forEach(e -> {
      sb.append(e.getLogTime()).append("\t").append(e.getText()).append("\n");
    });
    return ResponseEntity.ok(sb.toString());
  }

  @GetMapping(value = "/cleanup", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PlsLog>> cleanup() {
    logService.cleanupLog();
    return ResponseEntity.ok(plsLogRepository.findAll());
  }
}
