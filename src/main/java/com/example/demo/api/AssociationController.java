package com.example.demo.api;

import com.example.demo.domain.association.AssociationCriteriaModel;
import com.example.demo.domain.association.AssociationModelAdd;
import com.example.demo.domain.association.AssociationModelReturn;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domainservices.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/associations")
@Tag(name = "Associations", description = "Associations management APIs")
public class AssociationController {
    @Inject
    private AssociationService service;

    @PutMapping(value = "/add")
    @Operation(summary = "Add new association", description = "Returns the created object or an error")
    public GeneralResultModel add(@RequestBody AssociationModelAdd model) {
        return service.save(model);
    }

    @GetMapping(value = "/searchById/{id}")
    @Operation(summary = "Get a association by id", description = "Returns a association as per the id or an error")
    public GeneralResultModel getById(@PathVariable @Parameter(name = "association UUID", description = "association id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
        return service.getById(id);
    }

    @PostMapping(value = "/update")
    @Operation(summary = "Update association", description = "Returns the updated association or an error")
    public GeneralResultModel update(@RequestBody AssociationModelReturn model) {
        return service.update(model);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete association by id", description = "Returns empty body or an error")
    public GeneralResultModel delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PutMapping(value = "/addList")
    @Operation(summary = "Add several new associations", description = "Returns a list containing added associations or errors")
    public List<GeneralResultModel> addList(@RequestBody List<AssociationModelAdd> modelAddList) {
        return service.saveList(modelAddList);
    }

    @PostMapping(value = "/criteria")
    @Operation(summary = "Performs searches with filtering results", description = "Returns a list containing the found associations")
    public PageResult<AssociationModelReturn> criteriaQuery(@RequestBody AssociationCriteriaModel criteriaModel) {
        return service.criteriaQuery(criteriaModel);
    }

    @GetMapping(value = "/searchByIdEnriched/{id}")
    @Operation(summary = "Get a association with lettered words and language name by id", description = "Returns a enriched association as per the id or an error")
    public GeneralResultModel getByIdEnriched(@PathVariable UUID id) {
        return service.getByIdEnriched(id);
    }

}
