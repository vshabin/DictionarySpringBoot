package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    @Inject
    private LanguageRepository repository;

    public GeneralResultModel getByName(String name) {
        LanguageModelReturn model = repository.findByName(name);
        if (model == null) {
            return new GeneralResultModel("INCORRECT_NAME_ERROR", "Нет языка с таким именем");
        }
        return model;
    }

    public GeneralResultModel getById(UUID id) {
        LanguageModelReturn model = repository.findById(id);
        if (model == null) {
            return new GeneralResultModel("INCORRECT_ID_ERROR", "Нет языка с таким id");
        }
        return model;

    }

    public GeneralResultModel save(LanguageModelAdd model) {
        if (getByName(model.getName()) != null) {
            GeneralResultModel resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ALREADY_EXIST_ERROR");
            resultModel.setErrorMessage("Такой язык уже существует: " + model.getName());
            return resultModel;
        }
        return repository.save(model);
    }

    public GeneralResultModel update(LanguageModelReturn model) {
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

    public List<GeneralResultModel> saveList(List<LanguageModelAdd> modelAddList) {
        var resultModels = new ArrayList<GeneralResultModel>();
        var modelsToSave = new ArrayList<LanguageModelAdd>();
        var names = modelAddList.stream().map(LanguageModelAdd::getName).collect(Collectors.toList());
        var existList = repository.findExist(names);
        modelAddList.forEach(model -> {
            if (existList.contains(model.getName())) {
                GeneralResultModel resultModel = new GeneralResultModel();
                resultModel.setErrorCode("LANGUAGE_ALREADY_EXIST_ERROR");
                resultModel.setErrorMessage("Такой язык уже существует: " + model.getName());
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
        resultModel.setErrorCode("LANGUAGE_ID_NOT_EXIST_ERROR");
        resultModel.setErrorMessage("Языка с таким id не существует: " + id);
        return resultModel;
    }

    public PageResult<LanguageModelReturn> criteriaQuery(LanguageCriteriaModel languageCriteriaModel) {
        return repository.criteriaQuery(languageCriteriaModel);
    }
}
