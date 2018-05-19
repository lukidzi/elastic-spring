package com.example.elastic.elasticdemo.service;

import com.example.elastic.elasticdemo.api.model.NgramDocumentDto;
import com.example.elastic.elasticdemo.api.model.NgramDocumentResponseDto;
import com.example.elastic.elasticdemo.model.NgramDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
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
public class ElasticNgramDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ElasticNgramDocumentService.class);
    private static final String NAME_FIELD = "name";
    private static final String DOC_TYPE = "type";
    private final Client client;
    private final ObjectMapper objectMapper;

    @Value("${elastic.index.second}")
    private String index;

    public ElasticNgramDocumentService(Client client,
                                       ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<NgramDocumentResponseDto> searchByName(String name) {
        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setTypes(DOC_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryByName(name))
                .setSize(5);
        SearchResponse searchResponse = requestBuilder.execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        List<NgramDocumentResponseDto> docs = new ArrayList<>();
        for (SearchHit hit : hits) {
            docs.add(new NgramDocumentResponseDto((String) hit.getSourceAsMap().get(NAME_FIELD), hit.getScore()));
        }
        return docs;
    }

    private QueryBuilder queryByName(String name) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery(NAME_FIELD, name.toLowerCase()));
        return boolQuery;
    }

    public void insertDocument(NgramDocument document) {
        log.info("Inserting document: {}", document);
        Map values = toMap(document);
        IndexRequest indexRequest = new IndexRequest(index, DOC_TYPE, document.getName())
                .source(values);
        UpdateRequest updateRequest = new UpdateRequest(index, DOC_TYPE, document.getName())
                .doc(values)
                .upsert(indexRequest);
        client.update(updateRequest);
    }

    private Map toMap(NgramDocument document) {
        return objectMapper.convertValue(document, Map.class);
    }
}
