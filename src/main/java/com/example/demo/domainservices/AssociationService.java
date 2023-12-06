package com.example.demo.domainservices;

import com.example.demo.domain.association.AssociationCriteriaModel;
import com.example.demo.domain.association.AssociationModelAdd;
import com.example.demo.domain.association.AssociationModelReturn;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.infrastructure.repositories.association.AssociationRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssociationService {
    @Inject
    AssociationRepository repository;
    @Inject
    WordService wordService;

    public GeneralResultModel getById(UUID id) {
        AssociationModelReturn model = repository.findById(id);
        if (model == null) {
            return new GeneralResultModel("INCORRECT_ID_ERROR", "Нет ассоциации с таким id");
        }
        return model;
    }

    public GeneralResultModel save(AssociationModelAdd model) {
        if (repository.getAllAssociations(model.getWord()).contains(model.getTranslation())) {
            return new GeneralResultModel("PAIR_ALREADY_EXISTS_ERROR", "Такая пара слов уже существует");
        }
        if (wordService.isSameLanguage(model.getWord(), model.getTranslation())) {
            return new GeneralResultModel("SAME_LANGUAGE_ERROR", "Вы указали оба слова из одного языка");
        }
        return repository.save(model);
    }

    public GeneralResultModel update(AssociationModelReturn model) {
        if (!repository.exists(model.getId())) {
            return getNotExistError(model.getId());
        }
        return repository.update(model);
    }

    public GeneralResultModel delete(UUID id) {
        if (!repository.exists(id)) {
            return getNotExistError(id);
        }
        return repository.delete(id);
    }

    public List<GeneralResultModel> saveList(List<AssociationModelAdd> modelAddList) {
        var resultModels = new ArrayList<GeneralResultModel>();
        var modelsToSave = new ArrayList<AssociationModelAdd>();
        modelAddList.forEach(model -> {
            if (repository.getAllAssociations(model.getWord()).contains(model.getTranslation())) {
                resultModels.add(new GeneralResultModel("PAIR_ALREADY_EXISTS_ERROR", "Такая пара слов уже существует :" + model.getWord() + " : " + model.getTranslation()));
            } else if (wordService.isSameLanguage(model.getWord(), model.getTranslation())) {
                resultModels.add(new GeneralResultModel("SAME_LANGUAGE_ERROR", "Вы указали оба слова из одного языка"));
            } else {
                modelsToSave.add(model);
            }
        });
        resultModels.addAll(repository.saveList(modelsToSave));
        return resultModels;
    }

    private GeneralResultModel getNotExistError(UUID id) {
        GeneralResultModel resultModel = new GeneralResultModel();
        resultModel.setErrorCode("ASSOCIATION_ID_NOT_EXIST_ERROR");
        resultModel.setErrorMessage("Ассоциации с таким id не существует: " + id);
        return resultModel;
    }

    public PageResult<AssociationModelReturn> criteriaQuery(AssociationCriteriaModel criteriaModel) {
        return repository.criteriaQuery(criteriaModel);
    }

    public GeneralResultModel getByIdEnriched(UUID id) {
        return repository.getByIdEnriched(id);
    }
}
