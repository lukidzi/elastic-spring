# Spring and ElasticSearcg
This is simple application with elasticsearch and springboot 2.0

# Things done

  - Elasticsearch index with 2 types analyzers

### To start application

1. Start 2 instances of elasticsearch
```sh
$ docker-compose -f docker-compose.yml up
```

2. Create template
```sh
$ curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/_template/template_name -d '{
  "index_patterns": ["ngram_test_*"],
  "settings": {
    "analysis": {
      "analyzer": {
        "my_custom_analyzer": {
          "type":      "custom",
          "tokenizer": "my_tokenizer",
          "filter": [
            "lowercase"
          ]
        }
      },"tokenizer": {
        "my_tokenizer": {
          "type": "ngram",
          "min_gram": 3,
          "max_gram": 10,
          "token_chars": [
            "letter",
            "digit"
          ]
        }
      }
	},
  "number_of_shards": 2
  },
  "mappings": {
    "_doc": {
      "dynamic": false,
      "properties": {
        "name": {
          "type": "text",
          "fielddata": true,
          "analyzer" : "my_custom_analyzer"
        }
      }
    }
  }
}'
```

4. Create first index
```sh
$ curl -XPUT http://localhost:9200/ngram_test_1
```

5. Create second template
```sh
$ curl -XPOST -H 'Content-Type: application/json' http://localhost:9200/_template/template_name -d '{
  "index_patterns": [
    "search_test_*"
  ],
  "settings": {
    "analysis": {
      "analyzer": {
        "my_custom_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase"
          ]
        }
      }
    },
    "number_of_shards": 2
  },
  "mappings": {
    "type": {
      "dynamic": false,
      "properties": {
        "name": {
          "type": "text",
          "fielddata": true
        },
        "options": {
          "type": "nested",
          "dynamic": false,
          "properties": {
            "name": {
              "type": "keyword"
            },
            "value": {
              "type": "text",
              "fielddata": true,
              "analyzer": "my_custom_analyzer"
            }
          }
        }
      }
    }
  }
}'
```
6. Create second index
```sh
$ curl -XPUT http://localhost:9200/search_test_1
```
7. Start vagrant box (it will take a while to install java 10 )
```sh
$ vagrant up
```
8. Use vagrant ssh to enter VM and go to /vagrant_data where project will be mounted. You have to provide ip address of machine with running elasticsearch:

```sh
$ vagrant ssh
$ cd /vagrant_data
$ ./start.sh IP_OF_HOST_WITH_ELASTIC
```
9. Now you can use endpoints:
- GET http://localhost:8080/document?name=text (search in index search_test_1)
- PUT http://localhost:8080/document and data(check code)
- GET http://localhost:8080/ngram?search=text (search in index ngram_test_1)
- PUT http://localhost:8080/ngram and data(check code)


