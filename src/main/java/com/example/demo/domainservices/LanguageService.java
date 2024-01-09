package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LanguageService {
    private static final String LANGUAGE_ALREADY_EXIST_ERROR_CODE = "LANGUAGE_ALREADY_EXIST";
    private static final String LANGUAGE_ALREADY_EXIST_ERROR_MESSAGE = "Такой язык уже существует: ";
    private static final String LANGUAGE_NOT_EXIST_ERROR_CODE = "LANGUAGE_NOT_EXIST";
    private static final String LANGUAGE_NOT_EXIST_ERROR_MESSAGE = "Такой язык не существует: ";
    private static final String LANGUAGE_ID_NOT_EXIST_ERROR_CODE = "LANGUAGE_ID_NOT_EXIST";
    private static final String LANGUAGE_ID_NOT_EXIST_ERROR_MESSAGE = "Языка с таким id не существует: ";

    @Inject
    private LanguageRepository repository;

    public LanguageModelReturn getByName(String name) {
        name = name.toLowerCase().trim();
        LanguageModelReturn model = repository.findByName(name);
        if (model != null) {
            model.setName(StringUtils.capitalize(model.getName()));
        }
        return model;
    }

    public LanguageModelReturn getById(UUID id) {
        LanguageModelReturn model = repository.findById(id);
        if (model != null) {
            model.setName(StringUtils.capitalize(model.getName()));
        }
        return model;
    }

    public GuidResultModel save(LanguageModelAdd model) {
        model.setName(model.getName().toLowerCase().trim());
        LanguageModelReturn existModel = getByName(model.getName());
        if (existModel != null) {
            return new GuidResultModel(LANGUAGE_ALREADY_EXIST_ERROR_CODE, LANGUAGE_ALREADY_EXIST_ERROR_MESSAGE + StringUtils.capitalize(model.getName()));
        }
        GuidResultModel answer = repository.save(model);
        return answer;
    }

    public LanguageModelReturn update(LanguageModelReturn model) {
        model.setName(model.getName().toLowerCase().trim());
        if (getById(model.getId()) == null) {
            return new LanguageModelReturn(LANGUAGE_ID_NOT_EXIST_ERROR_CODE, LANGUAGE_ID_NOT_EXIST_ERROR_MESSAGE + model.getId());
        }
        LanguageModelReturn languageModelReturn = repository.update(model);
        languageModelReturn.setName(StringUtils.capitalize(languageModelReturn.getName()));
        return languageModelReturn;
    }

    public GeneralResultModel delete(UUID id) {
        if (getById(id) == null) {
            return new GeneralResultModel(LANGUAGE_ID_NOT_EXIST_ERROR_CODE, LANGUAGE_ID_NOT_EXIST_ERROR_MESSAGE + id);
        }
        return repository.delete(id);
    }

    public List<LanguageModelReturn> saveList(List<LanguageModelAdd> modelAddList) {
        modelAddList.forEach(languageModelAdd -> {
            languageModelAdd.setName(languageModelAdd.getName().toLowerCase().trim());
        });
        var resultModels = new ArrayList<LanguageModelReturn>();
        var modelsToSave = new ArrayList<LanguageModelAdd>();
        var existList = repository.findExist(modelAddList.stream().map(LanguageModelAdd::getName).collect(Collectors.toList()));
        modelAddList.forEach(model -> {
            if (existList.contains(model.getName())) {
                resultModels.add(new LanguageModelReturn(LANGUAGE_ALREADY_EXIST_ERROR_CODE, LANGUAGE_ALREADY_EXIST_ERROR_MESSAGE + StringUtils.capitalize(model.getName())));
            } else {
                modelsToSave.add(model);
            }
        });
        List<LanguageModelReturn> successResultModels = repository.saveList(modelsToSave);
        successResultModels.forEach(model -> {
            model.setName(StringUtils.capitalize(model.getName()));
        });
        resultModels.addAll(successResultModels);
        return resultModels;
    }

    public PageResult<LanguageModelReturn> criteriaQuery(LanguageCriteriaModel languageCriteriaModel) {
        PageResult<LanguageModelReturn> pageResult = repository.criteriaQuery(languageCriteriaModel);
        pageResult.getPageContent().forEach(model -> {
            model.setName(StringUtils.capitalize(model.getName()));
        });
        return pageResult;
    }

    public List<LanguageModelReturn> findExist(List<UUID> languageIds) {
        return repository.findExistById(languageIds);
    }

    public boolean exists(UUID id) {
        return repository.exists(id);
    }

    public boolean exists(String name) {
        return repository.exists(name);
    }

    public List<LanguageModelReturn> getListByIdList(List<UUID> ids) {
        return repository.getListByIdList(ids);
    }
}
