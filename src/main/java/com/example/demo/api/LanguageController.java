package com.example.demo.api;
import com.example.demo.domain.LanguageModel;
import com.example.demo.domainservices.LanguageService;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapStructMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/languages")
public class LanguageController {
    @Inject
    private LanguageService service;

    @GetMapping(value = "/all")
    public List<LanguageModel> getAllLanguages(){
        return service.getAllLanguages();
    }

    @GetMapping(value="/byName")
    public LanguageModel getLanguageByName(@RequestParam String name){
        return service.getLanguageByName(name);
    }

    @PostMapping
    public UUID addLanguage(@RequestBody LanguageModel languageModel){
        return service.save(languageModel);
    }
}
