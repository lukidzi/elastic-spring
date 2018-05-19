package com.example.elastic.elasticdemo.api;

import com.example.elastic.elasticdemo.api.model.NgramDocumentDto;
import com.example.elastic.elasticdemo.api.model.NgramDocumentResponseDto;
import com.example.elastic.elasticdemo.model.NgramDocument;
import com.example.elastic.elasticdemo.service.ElasticNgramDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("ngram")
public class NgramController {

    private static final Logger log = LoggerFactory.getLogger(NgramController.class);
    private final ElasticNgramDocumentService elasticNgramDocumentService;

    public NgramController(ElasticNgramDocumentService elasticNgramDocumentService) {
        this.elasticNgramDocumentService = elasticNgramDocumentService;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void upsertDocument(@RequestBody NgramDocumentDto documentDto) {
        elasticNgramDocumentService.insertDocument(new NgramDocument(documentDto.getName()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<NgramDocumentResponseDto> search(@RequestParam("search") String search) {
        return Flux.fromIterable(elasticNgramDocumentService.searchByName(search))
                .doOnNext(it -> log.info("Sending: {}", it));
    }
}
