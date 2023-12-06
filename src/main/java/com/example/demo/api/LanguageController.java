package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.domainservices.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/languages")
@Tag(name = "Languages", description = "Languages management APIs")
public class LanguageController {
    @Inject
    private LanguageService service;

    @PutMapping(value = "/add")
    @Operation(summary = "Add new language", description = "Returns the created object or an error")
    public GeneralResultModel add(@RequestBody LanguageModelAdd languageModel) {
        return service.save(languageModel);
    }

    @GetMapping(value = "/searchById/{id}")
    @Operation(summary = "Get a language by id", description = "Returns a language as per the id or an error")
    public GeneralResultModel getById(@PathVariable @Parameter(name = "Language UUID", description = "Language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.getById(id);
    }

    @GetMapping(value = "/searchByName/{name}")
    @Operation(summary = "Get a language by name", description = "Returns a language as per the name or an error")
    public GeneralResultModel getByName(@PathVariable @Parameter(name = "Language name", description = "Language name", example = "Japan") String name) {
        return service.getByName(name);
    }

    @PostMapping(value = "/update")
    @Operation(summary = "Update language", description = "Returns the updated language or an error")
    public GeneralResultModel update(@RequestBody LanguageModelReturn languageModel) {
        return service.update(languageModel);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete language by id", description = "Returns empty body or an error")
    public GeneralResultModel delete(@PathVariable @Parameter(name = "Language UUID", description = "Language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.delete(id);
    }

    @PutMapping(value = "/addList")
    @Operation(summary = "Add several new languages", description = "Returns a list containing added languages or errors")
    public List<GeneralResultModel> addList(@RequestBody List<LanguageModelAdd> modelAddList) {
        return service.saveList(modelAddList);
    }

    @PostMapping(value = "/criteria")
    @Operation(summary = "Performs searches with filtering results", description = "Returns a list containing the found languages")
    public PageResult<LanguageModelReturn> criteriaQuery(@RequestBody LanguageCriteriaModel languageCriteriaModel) {
        return service.criteriaQuery(languageCriteriaModel);
    }
}
