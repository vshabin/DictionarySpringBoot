package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.infrastructure.repositories.word.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordService {
    private static final String UNEXPECTED_LANGUAGE_ID_ERROR_CODE = "UNEXPECTED_LANGUAGE_ID";
    private static final String UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE = "Неверный id языка: ";
    private static final String WORD_ALREADY_EXIST_ERROR_CODE = "WORD_ALREADY_EXIST_ERROR";
    private static final String WORD_ALREADY_EXIST_ERROR_MESSAGE = "Такое слово уже существует в словаре: ";
    private static final String WORD_ID_NOT_EXIST_ERROR_CODE = "WORD_ID_NOT_EXIST_ERROR";
    private static final String WORD_ID_NOT_EXIST_ERROR_MESSAGE = "Слово с таким id не существует: ";
    private static final String INCORRECT_WORD_ERROR_CODE = "INCORRECT_WORD_ERROR_CODE";
    private static final String INCORRECT_WORD_ERROR_MESSAGE = "Ваше слово не удовлетворяет требование языка";


    @Inject
    private WordRepository repository;
    @Inject
    private LanguageService languageService;

    public WordModelReturn getByName(String word) {
        word = word.toLowerCase().trim();
        WordModelReturn model = repository.getByName(word);
        if (model != null) {
            model.setWord(StringUtils.capitalize(model.getWord()));
        }
        return model;
    }

    public WordModelReturn getById(UUID id) {
        WordModelReturn model = repository.getById(id);
        if (model != null) {
            model.setWord(StringUtils.capitalize(model.getWord()));
        }
        return model;
    }

    public GuidResultModel save(WordModelPost model) {
        model.setWord(model.getWord().toLowerCase().trim());
        var language = languageService.getById(model.getLanguageId());
        if (language == null) {
            return new GuidResultModel(UNEXPECTED_LANGUAGE_ID_ERROR_CODE, UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE + model.getLanguageId());
        }
        WordModelReturn word = repository.getByName(model.getWord());
        if (word != null && word.getLanguageId().equals(model.getLanguageId())) {
            return new GuidResultModel(WORD_ALREADY_EXIST_ERROR_CODE, WORD_ALREADY_EXIST_ERROR_MESSAGE + (word.getId()));
        }
        if (!model.getWord().matches(language.getRegEx())) {
            return new GuidResultModel(INCORRECT_WORD_ERROR_CODE, INCORRECT_WORD_ERROR_MESSAGE);
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
        var existLanguageMap = languageService.findExist(modelAddList.stream().map(WordModelPost::getLanguageId).collect(Collectors.toList())).stream().collect(Collectors.toMap(LanguageModelReturn::getId, Function.identity()));
        modelAddList.forEach(model -> {
            if (existList.contains(model.getWord())) {
                resultModels.add(new WordModelReturn(WORD_ALREADY_EXIST_ERROR_CODE, WORD_ALREADY_EXIST_ERROR_MESSAGE + StringUtils.capitalize(model.getWord())));
            } else if (!existLanguageMap.containsKey(model.getLanguageId())) {
                resultModels.add(new WordModelReturn(UNEXPECTED_LANGUAGE_ID_ERROR_CODE, UNEXPECTED_LANGUAGE_ID_ERROR_MESSAGE + model.getLanguageId()));
            } else if (!model.getWord().matches(existLanguageMap.get(model.getLanguageId()).getRegEx())) {
                resultModels.add(new WordModelReturn(INCORRECT_WORD_ERROR_CODE, INCORRECT_WORD_ERROR_MESSAGE));
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

    public List<WordModelReturnEnriched> getListEnrichedByIds(Collection<UUID> ids) {
        var words = repository.getListEnrichedByIdList(ids);
        var languageIds = words.stream()
                .map(WordModelReturn::getLanguageId)
                .distinct()
                .collect(Collectors.toList());
        var languageModels = languageService.getListByIdList(languageIds)
                .stream()
                .collect(Collectors.toMap(LanguageModelReturn::getId, Function.identity()));
        var enrichedWordsList = new ArrayList<WordModelReturnEnriched>();
        words.forEach(wordEntity -> {
            var wordModel = new WordModelReturnEnriched();
            wordModel.setId(wordEntity.getId());
            wordModel.setWord(wordEntity.getWord());
            wordModel.setLanguageName(languageModels.get(wordEntity.getLanguageId()).getName());
            wordModel.setLanguageId(wordEntity.getLanguageId());
            wordModel.setCreatedAt(wordEntity.getCreatedAt());
            enrichedWordsList.add(wordModel);
        });
        return enrichedWordsList;
    }
}
