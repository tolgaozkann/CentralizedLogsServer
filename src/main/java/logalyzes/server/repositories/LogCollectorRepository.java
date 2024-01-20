package logalyzes.server.repositories;


import logalyzes.server.core.ElkCore;

import java.util.concurrent.CompletableFuture;

public class LogCollectorRepository  {

    public <T> CompletableFuture<Boolean> save(T doc) throws Exception {
        ElkCore client = ElkCore.getInstance();
        return client.storeDocWithDateSuffix(doc);
    }
}
