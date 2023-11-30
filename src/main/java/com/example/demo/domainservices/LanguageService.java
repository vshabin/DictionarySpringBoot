package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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
        GeneralResultModel resultModel= existCheck(model.getName());
        if(resultModel!=null){
            return resultModel;
        }
        return langRepo.save(model);
    }

    public GeneralResultModel update(LanguageModelReturn languageModel) {
        GeneralResultModel resultModel= notExistCheck(languageModel.getId());
        if(resultModel!=null){
            return resultModel;
        }
        return langRepo.update(languageModel);
    }

    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel= notExistCheck(id);
        if(resultModel!=null){
            return resultModel;
        }
        return langRepo.delete(id);
    }

    public List<GeneralResultModel> saveList(List<LanguageModelAdd> modelAddList) {
        List<GeneralResultModel> resultModels=new ArrayList<>();
        for(LanguageModelAdd model: modelAddList){
            GeneralResultModel resultModel= existCheck(model.getName());
            if(resultModel!=null){
                resultModels.add(resultModel);
                modelAddList.remove(resultModel);
            }
        }
        resultModels.addAll(langRepo.saveList(modelAddList));
        return resultModels;
    }
    private GeneralResultModel existCheck(String name){
        GeneralResultModel resultModel;
        LanguageModelReturn check = getByName(name);
        if (check != null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ALREADY_EXIST_ERROR");
            resultModel.setErrorMessage("Такой язык уже существует: " + (check.getId()));
            return resultModel;
        }
        return null;
    }

    private GeneralResultModel notExistCheck(UUID id){
        GeneralResultModel resultModel;
        LanguageModelReturn check = getById(id);
        if (check == null) {
            resultModel = new GeneralResultModel();
            resultModel.setErrorCode("LANGUAGE_ID_NOT_EXIST_ERROR");
            resultModel.setErrorMessage("Языка с таким id не существует: " + id);
            return resultModel;
        }
        return null;
    }
}
