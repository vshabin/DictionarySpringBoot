package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domainservices.WordService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/words")
public class WordController {
    @Inject
    private WordService wordService;

//    @GetMapping(value = "/all")
//    public List<WordModelReturn> getAllWords() {
//        return wordService.getAllWords();
//    }

    @PutMapping(value = "/add")
    public GeneralResultModel add(@RequestBody WordModelPost word) {
        return wordService.save(word);
    }
    @GetMapping(value = "/searchByName/{word}")
    public WordModelReturn getByName(@PathVariable String word) {
        return wordService.getWordByName(word);
    }

    @GetMapping(value = "/searchById/{id}")
    public WordModelReturn getById(@PathVariable UUID id) {
        return wordService.getWordById(id);
    }

}
