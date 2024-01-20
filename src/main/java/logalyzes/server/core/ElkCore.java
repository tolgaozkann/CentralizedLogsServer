package logalyzes.server.core;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.logalyzes.health.dtos.HealthCheckResponse;
import logalyzes.server.utils.Config;
import logalyzes.server.utils.DateUtils;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import  org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ElkCore {

    // Elk instance
    private  static  ElkCore instance = null;

    private  String host;
    private int port;

    private ElasticsearchAsyncClient client;
    private Logger logger = Logger.getInstance();


    public static ElkCore getInstance() {
        if(instance == null) {
            instance = new ElkCore();
        }
        return instance;
    }


    public  ElkCore() {
        this.host = Config.ELK_HOST;
        this.port = Config.ELK_PORT;

        this.connect(this.host, this.port);





    }

    private  void connect(String host, int port){
        RestClient _client = RestClient
                .builder(HttpHost.create("http://"+host+":"+port))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Content-Type", "application/json"),
                        new BasicHeader("Accept", "application/json")
                })
                .build();

        ElasticsearchTransport _transport = new RestClientTransport(
                _client, new JacksonJsonpMapper());

        this.client = new ElasticsearchAsyncClient(_transport);;
    }


    public ElasticsearchAsyncClient getClient() {
        return this.client;
    }


    /**
     *
     *
     *
     *  Function to check index exists
     */
    public boolean indexExists(String indexName) throws Exception {
        CompletableFuture<BooleanResponse> response = this.client.indices().exists(i -> i.index(indexName));
        return response.get().value();
    }

    /**
     *
     *
     *  Ping the server, check if it's alive
     */
    public boolean ping() throws Exception {
        CompletableFuture<BooleanResponse> response = this.client.ping();
        return response.get().value();
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
        return this.createIndex(indexName);
    }

    /**
     *
     *  Storing object in index
     *
     */
    public <T> CompletableFuture<Boolean> storeDoc(String index, T obj) throws Exception  {

        return this.client.index(i -> i.index(index).document(obj))

        .thenApply(res -> {
            System.out.println(res);

            return true;
        }).exceptionallyAsync(e -> {
            System.out.println(e);
            return false;
        });



    }


    /**
     *
     *
     *  Store object in index with current date as suffix
     *
     *
     */
    public <T> CompletableFuture<Boolean> storeDocWithDateSuffix(T obj) throws Exception {
        String index = DateUtils.getStringDate();
        return this.storeDoc(index, obj);
    }




}
