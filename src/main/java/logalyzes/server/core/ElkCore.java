package logalyzes.server.core;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import logalyzes.server.utils.Config;
import logalyzes.server.utils.DateUtils;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import  org.elasticsearch.client.RestClient;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class ElkCore {

    // Elk instance
    private  static volatile  ElkCore instance = null;

    private  String host;
    private int port;

    private ElasticsearchAsyncClient client;
    private Logger logger = Logger.getInstance();


    public static ElkCore getInstance() throws Exception {
        if(instance == null) {
            synchronized (ElkCore.class) {
                if(instance == null) {
                    instance = new ElkCore();
                }
            }
        }
        return instance;
    }


    public  ElkCore() throws Exception {
        this.host = Config.ELK_HOST;
        this.port = Config.ELK_PORT;

        this.connect(this.host, this.port);

        /*
        if(!this.ping()){
            logger.log(LOG_LEVEL.ERROR, "ELK server is not responding");
            // Exit
            System.exit(1);
        }
        
         */

        logger.log(LOG_LEVEL.INFO, "ELK server is up and running");
    }

    private  void connect(String host, int port){
        RestClient _client = RestClient
                .builder(HttpHost.create("http://"+host+":"+port))
                .setDefaultHeaders(new Header[]{})
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
        return this.client.index(i -> i.index(index).id(UUID.randomUUID().toString()).document(obj))
        .thenApply(res -> {
            System.out.println(res);
            return true;
        }).exceptionallyAsync(e -> {
            System.out.println(e);
            throw new RuntimeException(e);
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
