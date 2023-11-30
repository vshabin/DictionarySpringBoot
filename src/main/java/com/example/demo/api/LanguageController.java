package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.domainservices.LanguageService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/languages")
public class LanguageController {
    @Inject
    private LanguageService service;

    @PutMapping(value = "/add")
    public GeneralResultModel add(@RequestBody LanguageModelAdd languageModel) {
        return service.save(languageModel);
    }

    @GetMapping(value = "/searchById/{id}")
    public LanguageModelReturn getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping(value = "/searchByName/{name}")
    public LanguageModelReturn getByName(@PathVariable String name) {
        return service.getByName(name);
    }
    @PostMapping(value = "/update")
    public GeneralResultModel update(@RequestBody LanguageModelReturn languageModel){
        return service.update(languageModel);
    }
    @DeleteMapping(value = "/delete/{id}")
    public GeneralResultModel delete(@PathVariable UUID id){
        return service.delete(id);
    }

    @PutMapping(value="/addList")
    public List<GeneralResultModel> addList(@RequestBody List<LanguageModelAdd> modelAddList){
        return service.saveList(modelAddList);
    }
}
