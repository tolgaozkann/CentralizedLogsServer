## Centralized Log Server Helm Chart

To be able to easyly deploy Log server and all dependencies on ks8 use this example command:

```bash
    helm install test-release ./centralized-server-chart
```

With namespace

```bash
    helm install test-release ./centralized-server-chart --namespace logging 
```