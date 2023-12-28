package com.example.demo.domainservices;

import com.example.demo.domain.association.*;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.association.AssociationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssociationService {
    private static final String PAIR_ALREADY_EXISTS_ERROR_CODE = "PAIR_ALREADY_EXISTS";
    private static final String PAIR_ALREADY_EXISTS_ERROR_MESSAGE = "Такая пара слов уже существует: ";
    private static final String SAME_LANGUAGE_ERROR_CODE = "SAME_LANGUAGE_ERROR";
    private static final String SAME_LANGUAGE_ERROR_MESSAGE = "Вы указали оба слова из одного языка";
    private static final String ASSOCIATION_ID_NOT_EXIST_ERROR_CODE = "ASSOCIATION_ID_NOT_EXIST_ERROR";
    private static final String ASSOCIATION_ID_NOT_EXIST_ERROR_MESSAGE = "Ассоциации с таким id не существует: ";
    private static final String WORD_NOT_EXIST_ERROR_CODE = "WORD_NOT_EXIST_ERROR";
    private static final String WORD_NOT_EXIST_ERROR_MESSAGE = "Слово не существует";
    private static final String LANGUAGE_NOT_EXIST_ERROR_CODE = "LANGUAGE_NOT_EXIST_ERROR";
    private static final String LANGUAGE_NOT_EXIST_ERROR_MESSAGE = "Языка слова не существует";
    private static final String TRANSLATION_LANGUAGE_NOT_EXIST_ERROR_MESSAGE = "Языка перевода не существует";
    @Inject
    AssociationRepository repository;
    @Inject
    WordService wordService;
    @Inject
    LanguageService languageService;

    public AssociationModelReturn getById(UUID id) {
        return repository.findById(id);
    }

    public GuidResultModel save(AssociationModelAdd model) {
        if (repository.getAllAssociations(model.getWord()).contains(model.getTranslation())) {
            return new GuidResultModel(PAIR_ALREADY_EXISTS_ERROR_CODE, PAIR_ALREADY_EXISTS_ERROR_MESSAGE + model.getWord() + " : " + model.getTranslation());
        }
        if (wordService.isSameLanguage(model.getWord(), model.getTranslation())) {
            return new GuidResultModel(SAME_LANGUAGE_ERROR_CODE, SAME_LANGUAGE_ERROR_MESSAGE);
        }
        return repository.save(model);
    }

    public AssociationModelReturn update(AssociationModelReturn model) {
        if (!repository.exists(model.getId())) {
            return new AssociationModelReturn(ASSOCIATION_ID_NOT_EXIST_ERROR_CODE, ASSOCIATION_ID_NOT_EXIST_ERROR_MESSAGE + model.getId());
        }
        return repository.update(model);
    }

    public GeneralResultModel delete(UUID id) {
        if (!repository.exists(id)) {
            return new AssociationModelReturn(ASSOCIATION_ID_NOT_EXIST_ERROR_CODE, ASSOCIATION_ID_NOT_EXIST_ERROR_MESSAGE + id);
        }
        return repository.delete(id);
    }

    public List<AssociationModelReturn> saveList(List<AssociationModelAdd> modelAddList) {
        var resultModels = new ArrayList<AssociationModelReturn>();
        var modelsToSave = new ArrayList<AssociationModelAdd>();
        modelAddList.forEach(model -> {
            if (repository.getAllAssociations(model.getWord()).contains(model.getTranslation())) {
                resultModels.add(new AssociationModelReturn(PAIR_ALREADY_EXISTS_ERROR_CODE, PAIR_ALREADY_EXISTS_ERROR_MESSAGE + model.getWord() + " : " + model.getTranslation()));
            } else if (wordService.isSameLanguage(model.getWord(), model.getTranslation())) {
                resultModels.add(new AssociationModelReturn(SAME_LANGUAGE_ERROR_CODE, SAME_LANGUAGE_ERROR_MESSAGE));
            } else {
                modelsToSave.add(model);
            }
        });
        resultModels.addAll(repository.saveList(modelsToSave));
        return resultModels;
    }

    public PageResult<AssociationModelReturn> getPage(AssociationCriteriaModel criteriaModel) {
        return repository.getPage(criteriaModel);
    }

    public AssociationModelReturnEnriched getByIdEnriched(UUID id) {
        var result = repository.getByIdEnriched(id);
        result.setFirstWordText(StringUtils.capitalize(result.getFirstWordText()));
        result.setSecondWordText(StringUtils.capitalize(result.getSecondWordText()));
        result.setFirstWordLangName(StringUtils.capitalize(result.getFirstWordLangName()));
        result.setSecondWordLangName(StringUtils.capitalize(result.getSecondWordLangName()));
        return result;
    }

    public PageResult<WordModelReturn> translate(TranslationRequest request) {
        request.setWord(request.getWord().toLowerCase().trim());
        if (!wordService.exists(request.getWord())) {
            return new PageResult<>(WORD_NOT_EXIST_ERROR_CODE, WORD_NOT_EXIST_ERROR_MESSAGE);
        }
        request.setWordLanguage(request.getWordLanguage().toLowerCase().trim());
        if (!languageService.exists(request.getWordLanguage())) {
            return new PageResult<>(LANGUAGE_NOT_EXIST_ERROR_CODE, LANGUAGE_NOT_EXIST_ERROR_MESSAGE);
        }
        request.setTranslationLanguage(request.getTranslationLanguage().toLowerCase().trim());
        if (!languageService.exists(request.getTranslationLanguage())) {
            return new PageResult<>(LANGUAGE_NOT_EXIST_ERROR_CODE, TRANSLATION_LANGUAGE_NOT_EXIST_ERROR_MESSAGE);
        }
        var result = repository.translate(request);
        result.getPageContent().forEach(model -> {
            model.setWord(StringUtils.capitalize(model.getWord()));

        });
        return result;
    }
}
