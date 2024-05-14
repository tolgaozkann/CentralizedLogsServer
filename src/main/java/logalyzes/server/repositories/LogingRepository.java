package logalyzes.server.repositories;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.IndicesStatsResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.logalyzes.logs.dtos.*;
import logalyzes.server.core.ElkCore;
import logalyzes.server.dtos.LogDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;



public class LogingRepository {

    private  ElasticsearchAsyncClient client;

    public  LogingRepository() throws Exception{
        client = ElkCore.getInstance().getClient();
    }

    public  CompletableFuture<ListIndexesResponse> listIndexes()  {
        CompletableFuture<IndicesStatsResponse> indicesStatsFuture = client.indices().stats();

        return indicesStatsFuture.thenApply(indicesStatsResponse -> {
            // Map each entry to an Index object using a builder and collect into an ArrayList
            ArrayList<Index> indexes = indicesStatsResponse.indices().entrySet().stream().map(entry -> {
                        // Ensure that total and docs are not null before accessing
                        if (entry.getValue().total() != null && entry.getValue().total().docs() != null) {
                            return Index.newBuilder()
                                    .setName(entry.getKey())
                                    .setSize(entry.getValue().total().docs().count())
                                    .build();
                        } else {
                            return null; // Alternatively, handle the case where the data is incomplete
                        }
                    })
                    .filter(index -> index != null)  // Remove null entries from the list
                    .collect(Collectors.toCollection(ArrayList::new));

            ListIndexesResponse listIndexesResponse = ListIndexesResponse.newBuilder()
                    .addAllIndexes(indexes)
                    .build();

            return listIndexesResponse;
        });
    }


    public CompletableFuture<LogsResponse> list(LogsRequest request) throws Exception {
        SearchRequest.Builder query = new SearchRequest.Builder()
                .index(request.getIndex())
                .from((request.getPage() - 1) * request.getPageSize())
                .size(request.getPageSize());




        /* Filtering */


        /* Sorting */


        return client.search(
                query.build(),
                LogDto.class
        ).thenApply(response -> {
            List<LogsMessages.Log> logs =  response.hits().hits().stream().map( hit ->
                hit.source().toLog()
            ).collect(Collectors.toList());

            return LogsResponse.newBuilder()
                    .addAllLogs(logs)
                    .setPage(request.getPage())
                    .setPageSize(request.getPageSize())
                    .setTotal((int) response.hits().total().value())
                    .setTotalPage((int) Math.ceil((double) response.hits().total().value() / request.getPageSize()))
                    .build();

        });
    }

    public CompletableFuture<LogsMessages.Log> details(String id) throws Exception {
       return  null;
    }

}
