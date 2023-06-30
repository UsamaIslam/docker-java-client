package com.upwork.docker.controller;

import com.upwork.docker.dto.CreateContainerRequest;
import com.upwork.docker.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DockerController {
    private final DockerService dockerService;

    @Autowired
    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostMapping("/containers/create")
    public ResponseEntity<String> createContainer(@RequestBody CreateContainerRequest request) {
        try {
            return ResponseEntity.ok(dockerService.createContainer(request.getImage(), request.getCmd()).string());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

