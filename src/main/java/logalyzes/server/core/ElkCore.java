package logalyzes.server.core;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import logalyzes.server.utils.Config;
import logalyzes.server.utils.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import  org.elasticsearch.client.RestClient;
import  org.elasticsearch.client.RestClientBuilder;

import java.util.concurrent.CompletableFuture;


public class ElkCore {

    // Elk instance
    private  static  ElkCore instance = null;

    private  String host;

    // Elasticsearch client
    private RestClient restClient;
    private ElasticsearchTransport transport;
    private ElasticsearchAsyncClient client;


    public static ElkCore getInstance() {
        if(instance == null) {
            instance = new ElkCore();
        }
        return instance;
    }


    public  ElkCore() {
        this.host = Config.ELK_HOST;

        this.restClient = RestClient
                .builder(HttpHost.create(this.host))
                .setDefaultHeaders(new Header[]{})
                .build();
        this.transport = new RestClientTransport(this.restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchAsyncClient(this.transport);
    }


    public ElasticsearchAsyncClient getClient() {
        return this.client;
    }

    public void close() throws Exception {
        this.transport.close();
        this.restClient.close();
    }

    /**
     *
     *  Creating index
     *
     */
    public String createIndex(String indexName) throws Exception {
        CompletableFuture<IndexResponse> response = this.client.index(i -> i.index(indexName));
        IndexResponse res = response.get();
        return res.index();
    }

    /**
     *
     *  Creating index with current date as suffix
     *
     */
    public String createIndexWithDateSuffix() throws Exception {
        String indexName = DateUtils.getStringDate();
        String response = createIndex(indexName);
        return response;
    }






}
