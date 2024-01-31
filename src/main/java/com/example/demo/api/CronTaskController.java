package com.example.demo.api;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.crontasks.CronTaskPostModel;
import com.example.demo.domain.crontasks.CronTaskReturnModel;
import com.example.demo.domainservices.CronTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cron")
@Validated
@Tag(name = "Cron tasks", description = "Cron tasks management APIs")
public class CronTaskController {
    @Autowired
    CronTaskService service;

    @PutMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public GuidResultModel save(@Valid @RequestBody CronTaskPostModel model) {
        return service.addNew(model);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("isAuthenticated()")
    public CronTaskReturnModel getById(@PathVariable UUID id) {
        return service.findById(id);
    }
}
