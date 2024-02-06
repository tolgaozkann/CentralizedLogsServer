package logalyzes.server.repositories;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.logalyzes.logs.dtos.LogsMessages;
import logalyzes.server.core.ElkCore;
import logalyzes.server.dtos.LogDto;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

class ListParams{
    // Default values
    public  int page = 1;
    public  int limit = 10;


    // Searching
    public String index;


    // Sorting
}


public class LogingRepository {
    // CURD
    public CompletableFuture<ArrayList<LogsMessages.Log>> list(ListParams params) throws Exception {
        ElasticsearchAsyncClient client = ElkCore.getInstance().getClient();
        return client.search(
                search -> search
                        .index(params.index)
                        .from((params.page - 1))
                        .size(params.limit),
                LogsMessages.Log.class
        ).thenApply(searchResponse -> {
            ArrayList<LogsMessages.Log> logs = new ArrayList<>();
            searchResponse.hits().hits().forEach(hit -> logs.add(hit.source()));
            return logs;
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<LogsMessages.Log> details(String id) throws Exception {
        ElasticsearchAsyncClient client = ElkCore.getInstance().getClient();
        return client.get(
                get -> get
                        .index("logs")
                        .id(id),
                LogsMessages.Log.class
        ).thenApply(getResponse -> getResponse.source());
    }

}
