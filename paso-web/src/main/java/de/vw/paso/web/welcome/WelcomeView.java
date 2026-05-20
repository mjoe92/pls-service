package de.vw.paso.web.welcome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WelcomeView {

    @GetMapping("/")
    public String showIndex() {
        return "redirect:welcome";
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "welcome";
    }

    @GetMapping("/privacypolicy")
    public String showPrivacyPolicy() {
        return "privacypolicy";
    }

    @GetMapping("/static/privacypolicy/{fileName}")
    public ResponseEntity<InputStreamResource> getPdfFile(@PathVariable String fileName) throws FileNotFoundException {

        String filePath = "resources/static/privacypolicy/";
        File file = new File(filePath + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=" + fileName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf")).body(resource);
    }

}
