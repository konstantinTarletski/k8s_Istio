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

`kubectl apply -f C:\CODE\k8s_istio\k8s\`

Where `C:\CODE\](C:\CODE\k8s_istio\k8s\` -- path to kubernetes YAMLs

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

[istio-gateway.yaml.bak](k8s/istio-gateway.yaml.bak)  
[istio-virtualservice.yaml.bak](k8s/istio-virtualservice.yaml.bak)

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
kubectl delete -f C:\CODE\k8s_istio\k8s\
kubectl apply -f C:\CODE\k8s_istio\k8s\
```
Where `C:\CODE\` -- path to kubernetes yamls

# ISTIO

## Istio CLI install Windows PS
```
winget install -e --id Istio.Istio
```

Check if Istio can be installed to a cluster  
`istioctl x precheck`

### Install Istio

```
istioctl install --set profile=demo -y
kubectl get pods -n istio-system
Output:
NAME                                   READY   STATUS    RESTARTS   AGE
istio-egressgateway-6f4fc6cdb9-xtl78   1/1     Running   0          52s
istio-ingressgateway-75858699f-zmq7d   1/1     Running   0          52s
istiod-67c645856-lfqmr                 1/1     Running   0          62s
```

`profile=demo` -- Aggressive logging, not for PROD

## Adding sidecars

>[!NOTE]
> * Adding just label, nothing more, but it ENABLES Istio
> * Everything should work, because no rules.

### All namespace
```
kubectl label namespace skais-2-test istio-injection=enabled
```
Delete label:
```
kubectl label namespace skais-2-test istio-injection-

```
`-` --- deletes label

### Adding sidecars to a specific service
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: service-b-deployment
  namespace: skais-2-test
spec:
  replicas: 2
  selector:
    matchLabels:
      app: service-b
  template:
    metadata:
      # HERE !!!!!!!!
      annotations:
        sidecar.istio.io/inject: "true" 
```

#### Rollout
All
```
kubectl rollout restart deployment -n skais-2-test
```
Specific
```
kubectl rollout restart deployment/deployment-service-a -n skais-2-test
```
Check sidecars: pods have 2 containers, one is Istio !!

## Ingress
>[!NOTE]
> **There are 3 ways**
> - Modify the existing Ingress GLOBAL service
> - Using Istio own `Gateway`
> - Hybrid mode (Istio Gateway + Original Ingress)

### 1. Modify the existing Ingress GLOBAL service
**Switch System (!!! ALL k8S !!!) Ingress to Istio (not good from my side)**  

>[!WARNING]
> Not a good solution because It affects all k8S services.

Add to our ingress :  
`metadata.annotations`:  
`nginx.ingress.kubernetes.io/service-upstream: "true"`  
**!!! Modifying `ingress-nginx` namespace !!!**  
`kubectl label namespace ingress-nginx istio-injection=enabled`

Restart Ingress (!!!!! DOWNTIME !!!!!!)
`kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx`  
because of 1 replica
in prod should be OK


### 2. Using Istio own `Gateway`

>[!WARNING]
> Better.
> You can use "OLD" existing Ingress and in parallel add new Istio Gateway.  
> But you need to create all Ingress rules manually again. !!!  
> See:  
> [istio-gateway.yaml.bak](k8s/istio-gateway.yaml.bak)  
> [istio-virtualservice.yaml.bak](k8s/istio-virtualservice.yaml.bak)  
> **And after enabling mTLS "OLD" Ingress will not work anymore because it can nto ebcrypt trafic** 

### 3. Hybrid mode (Istio Gateway + Original Ingress)

> [!TIP]
> **Recommended.**  
> We will not switch Ingress (`namespace ingress-nginx`) to Istio.  
> But redirect Ingress traffic to Istio Gateway.  
> And Istio Gateway will do the encryption and all other "Istio things"

### TODO Describe how to start it

-----------
### Garbage for the future use 
Just for my own use  
**Not part of the README**  


C:\Users\dev>kubectl get pods -n skais-2-test
NAME                                    READY   STATUS    RESTARTS   AGE
deployment-service-a-69fb8dc6cb-wbrmf   2/2     Running   0          143m
deployment-service-b-686bfb6cf7-bk2xv   2/2     Running   0          143m

C:\Users\dev>istioctl proxy-config cluster deployment-service-a-69fb8dc6cb-wbrmf -n skais-2-test --fqdn service-b-svc.skais-2-test.svc.cluster.local
SERVICE FQDN                                     PORT     SUBSET     DIRECTION     TYPE     DESTINATION RULE
service-b-svc.skais-2-test.svc.cluster.local     8080     -          outbound      EDS

EDS (Endpoint Discovery Service).