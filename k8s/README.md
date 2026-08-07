# Creating k8s

If you use local docker env  
Switch `kubectl` to local Docker env.
```
kubectl config use-context docker-desktop
```

Install OpenLens (if needed)  
UI Tool for kubernetes management 
```
C:\Users\dev>winget install openlens
```

Adding Ingress controller:
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

```
Create namespace:

`kubectl create namespace skais-2-test`

Apply all k8s YAML files:

`kubectl apply -f C:\CODE\k8s_istio\istio\`

Where `C:\CODE\](C:\CODE\k8s_istio\istio\` -- path to kubernetes YAMLs

## Access to services:

### Port forward:
```
kubectl port-forward svc/service-a-svc 8080:8080
```
http://localhost:8080/get-communication-hello  
http://localhost:8080/get-hello  

Or `service-b` ... c ...

### Through ingress:

http://localhost/service-a/get-communication-hello  
http://localhost/service-b/get-hello  

## Through Istio (istio-gateway.yaml)
Don’t work on Windows, need hack  
`kubectl port-forward svc/istio-ingressgateway -n istio-system 8081:80`

[istio-gateway.yaml.bak](../istio/istio-gateway.yaml.bak)  
[istio-virtualservice.yaml.bak](../istio/istio-virtualservice.yaml.bak)

`http://localhost:8081/service-istio-a/get-hello`  
`http://localhost:8081/service-istio-b/get-hello`  

## Responses:

`http://XXX/get-communication-hello`  
```
[
  {
    "role": "A1",
    "timestamp": "2026-08-06T08:37:41.372949513"
  },
  {
    "response-get-hello": {
      "timestamp": "2026-08-06T08:37:41.494113557",
      "role": "B1"
    },
    "host": "http://service-b-svc:8080"
  },
  {
    "response-get-hello": {
      "timestamp": "2026-08-06T08:37:41.584582119",
      "role": "B1"
    },
    "host": "http://service-b-svc:8080"
  }
]
```

`http://XXX/get-hello`
```
{
  "timestamp": "2026-08-06T09:02:50.657740098",
  "role": "B1"
}
```

## Logs:
```
kubectl get pods
deployment-service-a-784ddbc66-zsz7f    0/1     Error    3 (57s ago)   78s
deployment-service-b-5775c6f556-dsm7n   0/1     Error    3 (57s ago)   78s

kubectl logs deployment-service-a-784ddbc66-zsz7f
```

## Rebuild Docker:

Rebuild java  
`mvn clean package`

Delete Docker image:
``
docker rmi -f skais-microservice-example:latest
cd C:\CODE\k8s_istio\microservice\
docker build -t skais-microservice-example .
``

Clean image from k8s registry  
```
docker exec -it desktop-control-plane crictl rmi docker.io/library/skais-microservice-example:latest
docker exec -it desktop-control-plane crictl images | findstr skais
-- should be 0 !!
```
After that k8s pull it automatically

Restart kubernetes:
```
kubectl delete -f C:\CODE\k8s_istio\istio\
kubectl delete -f C:\CODE\k8s_istio\k8s\
kubectl apply -f C:\CODE\k8s_istio\k8s\
kubectl apply -f C:\CODE\k8s_istio\istio\
```
Where `C:\CODE\` -- path to kubernetes yamls

# ISTIO
See Istio Readme [README.md](../istio/README.md)