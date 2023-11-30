package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.word.WordModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.word.WordRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class WordService {

    @Inject
    private WordRepository wordRepo;
    @Inject
    private LanguageService languageService;

    public List<WordModelReturn> getAllWords() {
        List<WordModelReturn> modelList =wordRepo.findAll();
        for(WordModelReturn model: modelList){
            model.setLanguageName(languageService.getById(model.getLanguageId()).getName());
        }
        return modelList;
    }
    public WordModelReturn getWordByName(String word) {
        WordModelReturn model= wordRepo.getWordByName(word);
        if(model!=null) {
            model.setLanguageName(languageService.getById(model.getLanguageId()).getName());
        }
        return model;
    }
    public WordModelReturn getWordById(UUID id) {
        WordModelReturn model= wordRepo.getWordById(id);
        if(model!=null) {
            model.setLanguageName(languageService.getById(model.getLanguageId()).getName());
        }
        return model;
    }
    public List<WordModel> getAllWordByDictionaryId(UUID id){
        return wordRepo.getAllWordByDictionaryId(id);
    }
    public GeneralResultModel save(WordModelPost model) {
        GeneralResultModel resultModel;
        if(languageService.getById(model.getLanguageId())==null){
            resultModel=new GeneralResultModel();
            resultModel.setErrorCode("UNEXPECTED_LANGUAGE_ID_ERROR");
            resultModel.setErrorMessage("Неверный id языка: "+model.getLanguageId());
            return resultModel;
        }
        if(getWordByName(model.getWord())!=null){
            resultModel=new GeneralResultModel();
            resultModel.setErrorCode("WORD_ALREADY_EXIST_ERROR");
            resultModel.setErrorMessage("Такое слово уже есть в словаре: "+(getWordByName(model.getWord()).getId()));
            return resultModel;
        }
        return wordRepo.save(model);
    }


}
