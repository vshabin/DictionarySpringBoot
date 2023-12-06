package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.word.WordRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Inject
    private WordRepository repository;
    @Inject
    private LanguageService languageService;

    // model.setLanguageName(languageService.getById(model.getLanguageId()).getName());
    public GeneralResultModel getByName(String word) {
        WordModelReturn model = repository.getByName(word);
        if (model == null) {
            return new GeneralResultModel("INCORRECT_NAME_ERROR", "Нет слова с таким именем");
        }
        return model;
    }

    public GeneralResultModel getById(UUID id) {
        WordModelReturn model = repository.getById(id);
        if (model == null) {
            return new GeneralResultModel("INCORRECT_ID_ERROR", "Нет слова с таким id");
        }
        return model;
    }

    public GeneralResultModel save(WordModelPost model) {
        GeneralResultModel resultModel;
        if (languageService.getById(model.getLanguageId()) == null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("UNEXPECTED_LANGUAGE_ID_ERROR");
            resultModel.setErrorMessage("Неверный id языка: " + model.getLanguageId());
            return resultModel;
        }
        WordModelReturn word = repository.getByName(model.getWord());
        if (word != null && word.getLanguageId() == model.getLanguageId()) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("WORD_ALREADY_EXIST_ERROR");
            resultModel.setErrorMessage("Такое слово уже есть в словаре: " + (word.getId()));
            return resultModel;
        }
        return repository.save(model);
    }

    public GeneralResultModel update(WordModelReturn model) {
        if (getById(model.getId()) == null) {
            return getNotExistError(model.getId());
        }
        return repository.update(model);
    }

    public GeneralResultModel delete(UUID id) {
        if (getById(id) == null) {
            return getNotExistError(id);
        }
        return repository.delete(id);
    }

    public List<GeneralResultModel> saveList(List<WordModelPost> modelAddList) {
        var resultModels = new ArrayList<GeneralResultModel>();
        var modelsToSave = new ArrayList<WordModelPost>();
        var names = modelAddList.stream().map(WordModelPost::getWord).collect(Collectors.toList());
        var existList = repository.findExist(names);
        modelAddList.forEach(model -> {
            if (existList.contains(model.getWord())) {
                GeneralResultModel resultModel = new GeneralResultModel();
                resultModel.setErrorCode("WORD_ALREADY_EXIST_ERROR");
                resultModel.setErrorMessage("Такое слово уже существует: " + model.getWord());
                resultModels.add(resultModel);
            } else {
                modelsToSave.add(model);
            }
        });
        resultModels.addAll(repository.saveList(modelsToSave));
        return resultModels;
    }

    private GeneralResultModel getNotExistError(UUID id) {
        GeneralResultModel resultModel = new GeneralResultModel();
        resultModel.setErrorCode("WORD_ID_NOT_EXIST_ERROR");
        resultModel.setErrorMessage("Слова с таким id не существует: " + id);
        return resultModel;
    }

    public PageResult<WordModelReturn> criteriaQuery(WordCriteriaModel criteriaModel) {
        return repository.criteriaQuery(criteriaModel);
    }

    public GeneralResultModel getByNameEnriched(String word) {
        return repository.getByNameEnriched(word);
    }

    public GeneralResultModel getByIdEnriched(UUID id) {
        return repository.getByIdEnriched(id);
    }

    public boolean isSameLanguage(UUID firstWord, UUID secondWord) {
        return repository.isSameLanguage(firstWord, secondWord);
    }
}
