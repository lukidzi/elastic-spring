package com.example.elastic.elasticdemo.api;

import com.example.elastic.elasticdemo.api.model.DocumentDto;
import com.example.elastic.elasticdemo.model.Document;
import com.example.elastic.elasticdemo.service.ElasticDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
@RequestMapping("document")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private final ElasticDocumentService elasticDocumentService;

    public DocumentController(ElasticDocumentService elasticDocumentService) {
        this.elasticDocumentService = elasticDocumentService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DocumentDto> getDocumentByName(@RequestParam("name") String name) {
       return Flux.fromIterable(elasticDocumentService.searchByName(name))
               .doOnNext(it -> log.info("Sending: {}", it));
    }


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void upsertDocument(@RequestBody DocumentDto documentDto) {
        elasticDocumentService.insertDocument(Document.fromDto(documentDto));
    }
}
