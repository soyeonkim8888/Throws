package com.example.Throws.controller;

import com.example.Throws.service.ListeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listening")
public class ListeningController {
    private final ListeningService listeningService;

    @GetMapping
    public ResponseEntity<?> getListening(@RequestParam Long memberId) {
        try {
            String data = listeningService.getListeningContent(memberId);
            return ResponseEntity.ok(data);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("구독 후 리스닝 서비스를 이용할 수 있습니다.");
        }
    }
}
