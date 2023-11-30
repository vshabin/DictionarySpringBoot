package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service
public class LanguageService {

    @Inject
    private LanguageRepository langRepo;

    public LanguageModelReturn getByName(String name) {
        return langRepo.findByName(name);
    }

    public LanguageModelReturn getById(UUID id) {
        return langRepo.findById(id);
    }

    public GeneralResultModel save(LanguageModelAdd model) {
        GeneralResultModel resultModel;
        LanguageModelReturn check = getByName(model.getName());
        if (check != null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ALREADY_EXIST_ERROR");
            resultModel.setErrorMessage("Такой язык уже существует: " + (check.getId()));
            return resultModel;
        }
        return langRepo.save(model);
    }

    public GeneralResultModel update(LanguageModelReturn languageModel) {
        GeneralResultModel resultModel;
        LanguageModelReturn check = getById(languageModel.getId());
        if (check == null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ID_NOT_EXIST_ERROR");
            resultModel.setErrorMessage("Языка с таким id не существует: " + languageModel.getId());
            return resultModel;
        }
        return langRepo.update(languageModel);
    }

    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        LanguageModelReturn check = getById(id);
        if (check == null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ID_NOT_EXIST_ERROR");
            resultModel.setErrorMessage("Языка с таким id не существует: " + id);
            return resultModel;
        }
        return langRepo.delete(id);
    }
}
