package com.example.elastic.elasticdemo;

import com.example.elastic.elasticdemo.api.model.DocumentDto;
import com.example.elastic.elasticdemo.api.model.NgramDocumentDto;
import com.example.elastic.elasticdemo.api.model.NgramDocumentResponseDto;
import com.example.elastic.elasticdemo.api.model.OptionDto;
import com.example.elastic.elasticdemo.model.Document;
import com.example.elastic.elasticdemo.model.NgramDocument;
import com.example.elastic.elasticdemo.model.Option;
import com.example.elastic.elasticdemo.service.ElasticDocumentService;
import com.example.elastic.elasticdemo.service.ElasticNgramDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.ClassLoader.getSystemResourceAsStream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
public class ElasticDemoApplicationTests {

    private static final String SEARCH_INDEX = "search_test_1";
    private static final String NGRAM_INDEX = "ngram_test_1";
    private static final String DOC_TYPE = "type";

    @Autowired
    private Client elasticClient;

    @Autowired
    private ElasticDocumentService elasticDocumentService;

    @Autowired
    private ElasticNgramDocumentService elasticNgramDocumentService;

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static EmbeddedElastic elastic;


    @BeforeClass
    public static void setup() throws IOException, InterruptedException {
        elastic = EmbeddedElastic.builder()
                .withElasticVersion("6.2.4")
                .withStartTimeout(60, TimeUnit.SECONDS)
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9350)
                .withSetting(PopularProperties.CLUSTER_NAME, "my_cluster")
                .withTemplate("search_template", getSystemResourceAsStream("template-1.json"))
                .withTemplate("ngram_template", getSystemResourceAsStream("template-2.json"))
                .withIndex("search_test_1")
                .withIndex("ngram_test_1")
                .build()
                .start();
    }

    @Before
    public void cleanUp() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(elasticClient)
                .filter(QueryBuilders.matchAllQuery())
                .source("search_test_1")
                .get();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(elasticClient)
                .filter(QueryBuilders.matchAllQuery())
                .source("ngram_test_1")
                .get();
    }

    @Test
    public void shouldAddDocumentToElastic() throws IOException {
        //given
        DocumentDto document = new DocumentDto("Something special",
                List.of(new OptionDto("id", "2"),
                        new OptionDto("text", "wow amazing")));
        //when
        testClient.put().uri("http://localhost:" + port + "/document")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(document))
                .exchange()
                .expectStatus()
                .isCreated();

        //than
        GetResponse elasticResponse = elasticClient.get(new GetRequest(SEARCH_INDEX, DOC_TYPE, document.getName())).actionGet();
        DocumentDto elasticDocument = objectMapper.readValue(elasticResponse.getSourceAsString(), DocumentDto.class);
        assert elasticDocument.equals(document);
    }

    @Test
    public void shouldAddNgramDocumentToElastic() throws IOException {
        //given
        NgramDocumentDto document = new NgramDocumentDto("simple name");
        //when
        testClient.put().uri("http://localhost:" + port + "/ngram")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(document))
                .exchange()
                .expectStatus()
                .isCreated();

        //than
        GetResponse elasticResponse = elasticClient.get(new GetRequest(NGRAM_INDEX, DOC_TYPE, document.getName())).actionGet();
        NgramDocumentDto elasticDocument = objectMapper.readValue(elasticResponse.getSourceAsString(), NgramDocumentDto.class);
        assert elasticDocument.equals(document);
    }

    @Test
    public void shouldSearchForDocument() throws InterruptedException {
        //given
        Document document1 = new Document("Something special",
                List.of(new Option("id", "something"),
                        new Option("text", "wow amazing")));
        Document document2 = new Document("Something special but not nice",
                List.of(new Option("id", "nice"),
                        new Option("text", "wow amazing")));
        elasticDocumentService.insertDocument(document1);
        elasticDocumentService.insertDocument(document2);

        TimeUnit.SECONDS.sleep(3);
        //when
        testClient.get().uri("http://localhost:" + port + "/document?name=something+nice")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DocumentDto.class)
                .hasSize(2)
                .contains(DocumentDto.toDto(document2), DocumentDto.toDto(document1));

    }

    @Test
    public void shouldSearchForNgramDocument() throws InterruptedException {
        //given
        NgramDocument document1 = new NgramDocument("extra extra offer");
        NgramDocument document2 = new NgramDocument("some boring company");
        elasticNgramDocumentService.insertDocument(document1);
        elasticNgramDocumentService.insertDocument(document2);

        TimeUnit.SECONDS.sleep(3);
        //when
        testClient.get().uri("http://localhost:" + port + "/ngram?search=som")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(NgramDocumentResponseDto.class)
                .hasSize(1);

    }
}
