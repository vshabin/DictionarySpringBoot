package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageContent;
import com.example.demo.domain.language.LanguageModelPost;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class LanguageService {

    @Inject
    private LanguageRepository langRepo;
    @Inject
    private WordService wordService;

    public List<LanguageModelReturn> getAll() {
        return langRepo.findAll();
    }
    public LanguageModelReturn getByName(String name){
        return langRepo.findByName(name);
    }
    public LanguageModelReturn getById(UUID id) {
        return langRepo.findById(id);
    }
    public LanguageContent getContent(UUID id){
        LanguageContent languageContent= new LanguageContent();
        LanguageModelReturn languageModelReturn= langRepo.findById(id);
        languageContent.setId(languageModelReturn.getId());
        languageContent.setName(languageModelReturn.getName());
        languageContent.setWords(wordService.getAllWordByDictionaryId(id));
        return languageContent;
    }
    public GeneralResultModel save(LanguageModelPost model) {
        return langRepo.save(model);
    }


}
