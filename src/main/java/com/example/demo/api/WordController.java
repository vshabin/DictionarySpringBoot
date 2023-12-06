package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domainservices.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/words")
@Tag(name = "Words", description = "Words management APIs")
public class WordController {
    @Inject
    private WordService service;

    @PutMapping(value = "/add")
    @Operation(summary = "Add new word", description = "Returns the created object or an error")
    public GeneralResultModel add(@RequestBody WordModelPost model) {
        return service.save(model);
    }

    @GetMapping(value = "/searchById/{id}")
    @Operation(summary = "Get a word by id", description = "Returns a word as per the id or an error")
    public GeneralResultModel getById(@PathVariable @Parameter(name = "Word UUID", description = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.getById(id);
    }

    @GetMapping(value = "/searchByName/{name}")
    @Operation(summary = "Get a word by name", description = "Returns a word as per the name or an error")
    public GeneralResultModel getByName(@PathVariable @Parameter(name = "Word name", description = "Lettered word", example = "Dog") String name) {
        return service.getByName(name);
    }

    @PostMapping(value = "/update")
    @Operation(summary = "Update word", description = "Returns the updated word or an error")
    public GeneralResultModel update(@RequestBody WordModelReturn model) {
        return service.update(model);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete word by id", description = "Returns empty body or an error")
    public GeneralResultModel delete(@PathVariable @Parameter(name = "Word UUID", description = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.delete(id);
    }

    @PutMapping(value = "/addList")
    @Operation(summary = "Add several new words", description = "Returns a list containing added words or errors")
    public List<GeneralResultModel> addList(@RequestBody List<WordModelPost> addList) {
        return service.saveList(addList);
    }

    @PostMapping(value = "/criteria")
    @Operation(summary = "Performs searches with filtering results", description = "Returns a list containing the found words")
    public PageResult<WordModelReturn> criteriaQuery(@RequestBody WordCriteriaModel criteriaModel) {
        return service.criteriaQuery(criteriaModel);
    }

    @GetMapping(value = "/searchByIdEnriched/{id}")
    @Operation(summary = "Get a word with language name by id", description = "Returns a enriched word as per the id or an error")
    public GeneralResultModel getByIdEnriched(@PathVariable @Parameter(name = "Word UUID", description = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.getByIdEnriched(id);
    }

    @GetMapping(value = "/searchByNameEnriched/{name}")
    @Operation(summary = "Get a word with language name by name", description = "Returns a enriched word as per the name or an error")
    public GeneralResultModel getByNameEnriched(@PathVariable @Parameter(name = "Word name", description = "Lettered word", example = "Dog") String name) {
        return service.getByNameEnriched(name);
    }
}
