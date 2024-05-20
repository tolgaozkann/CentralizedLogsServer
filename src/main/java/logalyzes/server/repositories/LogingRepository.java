package logalyzes.server.repositories;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.indices.IndicesStatsResponse;
import logalyzes.server.core.ElkCore;
import logalyzes.server.dtos.LogDto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;



public class LogingRepository {

    private  ElasticsearchAsyncClient client;

    public  LogingRepository() throws Exception{
        client = ElkCore.getInstance().getClient();
    }

    public  CompletableFuture<com.logalyzes.logs.dtos.ListIndexesResponse> listIndexes()  {
        CompletableFuture<IndicesStatsResponse> indicesStatsFuture = client.indices().stats();

        return indicesStatsFuture.thenApply(indicesStatsResponse -> {
            // Map each entry to an Index object using a builder and collect into an ArrayList
            ArrayList<com.logalyzes.logs.dtos.Index> indexes = indicesStatsResponse.indices().entrySet().stream().map(entry -> {
                        // Ensure that total and docs are not null before accessing
                        if (entry.getValue().total() != null && entry.getValue().total().docs() != null) {
                            return com.logalyzes.logs.dtos.Index.newBuilder()
                                    .setName(entry.getKey())
                                    .setSize(entry.getValue().total().docs().count())
                                    .build();
                        } else {
                            return null; // Alternatively, handle the case where the data is incomplete
                        }
                    })
                    .filter(index -> index != null)  // Remove null entries from the list
                    .collect(Collectors.toCollection(ArrayList::new));

            com.logalyzes.logs.dtos.ListIndexesResponse listIndexesResponse = com.logalyzes.logs.dtos.ListIndexesResponse.newBuilder()
                    .addAllIndexes(indexes)
                    .build();

            return listIndexesResponse;
        });
    }


    public CompletableFuture<com.logalyzes.logs.dtos.LogsResponse> list(com.logalyzes.logs.dtos.LogsRequest request) throws Exception {
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
            List<com.logalyzes.logs.dtos.LogsMessages.Log> logs =  response.hits().hits().stream().map(hit ->
                hit.source().toLog()
            ).collect(Collectors.toList());

            return com.logalyzes.logs.dtos.LogsResponse.newBuilder()
                    .addAllLogs(logs)
                    .setPage(request.getPage())
                    .setPageSize(request.getPageSize())
                    .setTotal((int) response.hits().total().value())
                    .setTotalPage((int) Math.ceil((double) response.hits().total().value() / request.getPageSize()))
                    .build();

        });
    }

    public CompletableFuture<com.logalyzes.logs.dtos.LogsMessages.Log> details(String id) throws Exception {
       return  null;
    }

}
