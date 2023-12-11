package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.infrastructure.repositories.word.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WordService {
    private static final String UNEXPECTED_LANGUAGE_ID_ERROR_CODE = "UNEXPECTED_LANGUAGE_ID";
    private static final String UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE = "Неверный id языка: ";
    private static final String WORD_ALREADY_EXIST_ERROR_CODE = "WORD_ALREADY_EXIST_ERROR";
    private static final String WORD_ALREADY_EXIST_ERROR_MESSAGE = "Такое слово уже существует в словаре: ";
    private static final String WORD_ID_NOT_EXIST_ERROR_CODE = "WORD_ID_NOT_EXIST_ERROR";
    private static final String WORD_ID_NOT_EXIST_ERROR_MESSAGE = "Слово с таким id не существует: ";

    @Inject
    private WordRepository repository;
    @Inject
    private LanguageService languageService;

    public WordModelReturn getByName(String word) {
        word = word.toLowerCase().trim();
        WordModelReturn model = repository.getByName(word);
        model.setWord(StringUtils.capitalize(model.getWord()));
        return model;
    }

    public WordModelReturn getById(UUID id) {
        WordModelReturn model = repository.getById(id);
        model.setWord(StringUtils.capitalize(model.getWord()));
        return model;
    }

    public GuidResultModel save(WordModelPost model) {
        model.setWord(model.getWord().toLowerCase().trim());
        if (languageService.getById(model.getLanguageId()) == null) {
            return new GuidResultModel(UNEXPECTED_LANGUAGE_ID_ERROR_CODE, UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE + model.getLanguageId());
        }
        WordModelReturn word = repository.getByName(model.getWord());
        if (word != null && word.getLanguageId().equals(model.getLanguageId())) {
            return new GuidResultModel(WORD_ALREADY_EXIST_ERROR_CODE, WORD_ALREADY_EXIST_ERROR_MESSAGE + (word.getId()));
        }
        return repository.save(model);
    }

    public WordModelReturn update(WordModelReturn model) {
        if (getById(model.getId()) == null) {
            return new WordModelReturn(WORD_ID_NOT_EXIST_ERROR_CODE, WORD_ID_NOT_EXIST_ERROR_MESSAGE + model.getId());
        }
        model.setWord(model.getWord().toLowerCase().trim());
        WordModelReturn wordModelReturn = repository.update(model);
        wordModelReturn.setWord(StringUtils.capitalize(wordModelReturn.getWord()));
        return wordModelReturn;
    }

    public GeneralResultModel delete(UUID id) {
        if (repository.getById(id) == null) {
            return new GeneralResultModel(WORD_ID_NOT_EXIST_ERROR_CODE, WORD_ID_NOT_EXIST_ERROR_MESSAGE + id);
        }
        return repository.delete(id);
    }

    public List<WordModelReturn> saveList(List<WordModelPost> modelAddList) {
        modelAddList.forEach(model -> model.setWord(model.getWord().toLowerCase().trim()));
        var resultModels = new ArrayList<WordModelReturn>();
        var modelsToSave = new ArrayList<WordModelPost>();
        var existList = repository.findExist(modelAddList.stream().map(WordModelPost::getWord).collect(Collectors.toList()));
        var existLanguageList = languageService.findExist(modelAddList.stream().map(WordModelPost::getLanguageId).collect(Collectors.toList()));
        modelAddList.forEach(model -> {
            if (existList.contains(model.getWord())) {
                resultModels.add(new WordModelReturn(WORD_ALREADY_EXIST_ERROR_CODE, WORD_ALREADY_EXIST_ERROR_MESSAGE + StringUtils.capitalize(model.getWord())));
            } else if (!existLanguageList.contains(model.getLanguageId())) {
                resultModels.add(new WordModelReturn(UNEXPECTED_LANGUAGE_ID_ERROR_CODE, UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE + model.getLanguageId()));
            } else {
                modelsToSave.add(model);
            }
        });
        var savedWords = repository.saveList(modelsToSave);
        savedWords.forEach(model -> model.setWord(StringUtils.capitalize(model.getWord())));
        resultModels.addAll(savedWords);
        return resultModels;
    }

    public PageResult<WordModelReturn> criteriaQuery(WordCriteriaModel criteriaModel) {
        PageResult<WordModelReturn> pageResult = repository.criteriaQuery(criteriaModel);
        pageResult.getPageContent().forEach(model -> model.setWord(StringUtils.capitalize(model.getWord())));
        return pageResult;
    }

    public WordModelReturnEnriched getByNameEnriched(String word) {
        word = word.toLowerCase().trim();
        var result = repository.getByNameEnriched(word);
        result.setWord(StringUtils.capitalize(result.getWord()));
        result.setLanguageName(StringUtils.capitalize(result.getLanguageName()));
        return result;
    }

    public WordModelReturnEnriched getByIdEnriched(UUID id) {
        var result = repository.getByIdEnriched(id);
        result.setWord(StringUtils.capitalize(result.getWord()));
        result.setLanguageName(StringUtils.capitalize(result.getLanguageName()));
        return result;
    }

    public boolean isSameLanguage(UUID firstWord, UUID secondWord) {
        return repository.isSameLanguage(firstWord, secondWord);
    }

    public boolean exists(UUID id) {
        return repository.exists(id);
    }

    public boolean exists(String word) {
        return repository.exists(word);
    }
}
