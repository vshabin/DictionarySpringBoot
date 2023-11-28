package com.example.demo.domainservices;

import com.example.demo.domain.LanguageModel;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapStructMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import com.example.demo.infrastructure.repositories.language.LanguageRepository;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class LanguageService {

    @Inject
    LanguageRepository langRepo;

    public List<LanguageModel> getAllLanguages() {
        return langRepo.findAll();

    }

    public LanguageModel getLanguageByName(String name){
        return langRepo.findByName(name);
    }

    @Transactional
    public UUID save(LanguageModel model) {
        return langRepo.save(model);
    }
}
