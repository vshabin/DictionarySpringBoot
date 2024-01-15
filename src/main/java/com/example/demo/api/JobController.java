package com.example.demo.api;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domainservices.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@Tag(name = "Jobs", description = "Jobs management APIs")
public class JobController {
    @Autowired
    private JobService service;

    @GetMapping("/findById/{id}")
    @PreAuthorize("isAuthenticated()")
    public JobModelReturn getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public GuidResultModel save(@Valid @RequestBody JobModelPost model){
        return service.addNew(model);
    }
}
