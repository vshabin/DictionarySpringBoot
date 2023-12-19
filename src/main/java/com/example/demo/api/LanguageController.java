package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.domainservices.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/languages")
@Tag(name = "Languages", description = "Languages management APIs")
@Validated
public class LanguageController {
    @Inject
    private LanguageService service;

    @PutMapping(value = "/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Add new language", description = "Returns the created object or an error")
    public GuidResultModel add(@Valid @RequestBody LanguageModelAdd languageModel) {
        return service.save(languageModel);
    }

    @GetMapping(value = "/searchById/{id}")
    @Operation(summary = "Get a language by id", description = "Returns a language as per the id or an error")
    public LanguageModelReturn getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping(value = "/searchByName/{name}")
    @Operation(summary = "Get a language by name", description = "Returns a language as per the name or an error")
    public LanguageModelReturn getByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update language", description = "Returns the updated language or an error")
    public LanguageModelReturn update(@Valid @RequestBody LanguageModelReturn languageModel) {
        return service.update(languageModel);
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete language by id", description = "Returns empty body or an error")
    public GeneralResultModel delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PutMapping(value = "/addList")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Add several new languages", description = "Returns a list containing added languages or errors")
    public List<LanguageModelReturn> addList(@RequestBody List<@Valid LanguageModelAdd> modelAddList) {
        return service.saveList(modelAddList);
    }

    @PostMapping(value = "/criteria")
    @Operation(summary = "Performs searches with filtering results", description = "Returns a list containing the found languages")
    public PageResult<LanguageModelReturn> criteriaQuery(@Valid @RequestBody LanguageCriteriaModel languageCriteriaModel) {
        return service.criteriaQuery(languageCriteriaModel);
    }
}
