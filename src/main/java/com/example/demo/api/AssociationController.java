package com.example.demo.api;

import com.example.demo.domain.association.*;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domainservices.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/associations")
@Tag(name = "Associations", description = "Associations management APIs")
@Validated
public class AssociationController {
    @Inject
    private AssociationService service;

    @PutMapping(value = "/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Add new association", description = "Returns the created object or an error")
    public GuidResultModel add(@RequestBody @Valid AssociationModelAdd model) {
        return service.save(model);
    }

    @GetMapping(value = "/searchById/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a association by id", description = "Returns a association as per the id or an error")
    public AssociationModelReturn getById(@PathVariable @Parameter(name = "association UUID", description = "association id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.getById(id);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update association", description = "Returns the updated association or an error")
    public AssociationModelReturn update(@RequestBody @Valid AssociationModelReturn model) {
        return service.update(model);
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete association by id", description = "Returns empty body or an error")
    public GeneralResultModel delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PutMapping(value = "/addList")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Add several new associations", description = "Returns a list containing added associations or errors")
    public List<AssociationModelReturn> addList(@RequestBody List<@Valid AssociationModelAdd> modelAddList) {
        return service.saveList(modelAddList);
    }

    @PostMapping(value = "/criteria")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Performs searches with filtering results", description = "Returns a list containing the found associations")
    public PageResult<AssociationModelReturn> criteriaQuery(@RequestBody @Valid AssociationCriteriaModel criteriaModel) {
        return service.getPage(criteriaModel);
    }

    @GetMapping(value = "/searchByIdEnriched/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a association with lettered words and language name by id", description = "Returns a enriched association as per the id or an error")
    public AssociationModelReturnEnriched getByIdEnriched(@PathVariable UUID id) {
        return service.getByIdEnriched(id);
    }

    @PostMapping(value = "/translate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Translate a word", description = "Returns a list containing the found translations")
    public PageResult<WordModelReturn> translate(@RequestBody @Valid TranslationRequest translationRequest) {
        return service.translate(translationRequest);
    }
}
