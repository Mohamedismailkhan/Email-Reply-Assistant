package com.email.Writer.Controller;

import com.email.Writer.Service.EmailGeneratorService;
import com.email.Writer.io.EmailRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins="*")
public class EmailGeneratorController {
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> getEmail(@RequestBody EmailRequest emailrequest){
        String response= emailGeneratorService.generateEmailReply(emailrequest);
        return ResponseEntity.ok(response);
    }
}
