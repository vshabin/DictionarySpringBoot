package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageContent;
import com.example.demo.domain.language.LanguageModelPost;
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
    public GeneralResultModel addLanguage(@RequestBody LanguageModelPost languageModel) {
        return service.save(languageModel);
    }

    //    @GetMapping(value = "/all")
//    public List<LanguageModelReturn> getAllLanguages(){
//        return service.getAll();
//    }
    @GetMapping(value = "/searchById/{id}")
    public LanguageModelReturn getLanguageById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping(value = "/searchByName/{name}")
    public LanguageModelReturn getLanguageByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @GetMapping(value = "/getContent/{id}")
    public LanguageContent getContent(@PathVariable UUID id) {
        return service.getContent(id);
    }


}
