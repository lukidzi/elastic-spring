package com.example.elastic.elasticdemo.service;

import com.example.elastic.elasticdemo.api.model.DocumentDto;
import com.example.elastic.elasticdemo.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ElasticDocumentService.class);
    private static final String DOC_TYPE = "type";
    private static final String NAME_FIELD = "name";
    private static final String OPTIONS_VALUE = "options.value";
    private static final String OPTIONS_NAME = "options.name";

    private final Client client;
    private final ObjectMapper objectMapper;

    @Value("${elastic.index.first}")
    private String index;

    public ElasticDocumentService(Client client,
                                  ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<DocumentDto> searchByName(String name) {
        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setTypes(DOC_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryByName(name))
                .setSize(5);
        SearchResponse searchResponse = requestBuilder.execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        List<DocumentDto> docs = new ArrayList<>();
        for (SearchHit hit : hits) {
            try {
                docs.add(objectMapper.readValue(hit.getSourceAsString(), DocumentDto.class));
            } catch (IOException e) {
                log.error("Problem during deserialization");
            }
        }
        log.info("Found: {} ", docs);
        return docs;
    }

    private QueryBuilder queryByName(String name) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery(NAME_FIELD, name.toLowerCase()).fuzziness(Fuzziness.AUTO));
        boolQuery.should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termsQuery(OPTIONS_VALUE, name.split(" ")))
                        .must(QueryBuilders.termQuery(OPTIONS_NAME, "id")));
        return boolQuery;
    }

    public void insertDocument(Document document) {
        log.info("Inserting document: {}", document);
        Map values = toMap(document);
        IndexRequest indexRequest = new IndexRequest(index, DOC_TYPE, document.getName())
                .source(values);
        UpdateRequest updateRequest = new UpdateRequest(index, DOC_TYPE, document.getName())
                .doc(values)
                .upsert(indexRequest);
        client.update(updateRequest);
    }

    private Map toMap(Document document) {
        return objectMapper.convertValue(document, Map.class);
    }
}
