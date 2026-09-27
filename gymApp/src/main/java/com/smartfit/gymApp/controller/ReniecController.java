package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.service.ReniecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reniec")
@CrossOrigin(origins = "*")
public class ReniecController {

    @Autowired
    private ReniecService reniecService;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {
        try {
            ReniecData data = reniecService.consultarDni(dni);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}