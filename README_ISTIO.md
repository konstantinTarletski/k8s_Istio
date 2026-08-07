
Apply all k8s YAML files:

`kubectl apply -f C:\CODE\k8s_istio\k8s\`

Where `C:\CODE\](C:\CODE\k8s_istio\k8s\` -- path to kubernetes YAMLs

## Access to services:

## Through Istio (istio-gateway.yaml)
Don’t work on Windows, need hack  
`kubectl port-forward svc/istio-ingressgateway -n istio-system 8081:80`

[istio-gateway.yaml.bak](k8s/istio-gateway.yaml.bak)  
[istio-virtualservice.yaml.bak](k8s/istio-virtualservice.yaml.bak)

`http://localhost:8081/service-istio-a/get-hello`  
`http://localhost:8081/service-istio-b/get-hello`  

## Responses:

See: [README.md](README.md)  

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
> **Bad solution**  
> Not a good solution because It affects all k8S services.

Add to our ingress :  
`metadata.annotations`:  
`nginx.ingress.kubernetes.io/service-upstream: "true"`  
**!!! Modifying `ingress-nginx` namespace !!!**  
`kubectl label namespace ingress-nginx istio-injection=enabled`

Restart Ingress  
`kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx`  
(!!!!! DOWNTIME !!!!!!)  
because of 1 replica  
in prod should be OK


### 2. Using Istio own `Gateway`

>[!WARNING]
> **Better solution**  
> You can use "OLD" existing Ingress and in parallel add new Istio Gateway.  
> But you need to create all Ingress rules manually again. !!!  
> See:  
> [istio-gateway.yaml.bak](k8s/istio-gateway.yaml.bak)  
> [istio-virtualservice.yaml.bak](k8s/istio-virtualservice.yaml.bak)  
> **And after enabling mTLS "OLD" Ingress will not work anymore because it cannot encrypt traffic**

It means that links:  
http://localhost/service-a/get-hello  
http://localhost:8081/service-istio-a/get-hello
Will work together, at the same time.  
**But only before mTLS will be enabled.**  

After enabling mTLS, oly this will work:  
http://localhost/service-a/get-hello

### 3. Hybrid mode (Istio Gateway + Original Ingress)

> [!TIP]
> **Recommended.**  
> We will not switch Ingress (`namespace ingress-nginx`) to Istio.  
> But redirect Ingress traffic to Istio Gateway.  
> And Istio Gateway will do the encryption and all other "Istio things"

### TODO Describe how to start it

## mTLS (PeerAuthentication)

Any of Istio services can access it, even if other is not `STRICT`.  
It just must have Istio sidecar.
```
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: strict-mtls-service-a
  namespace: skais-2-test
spec:
  selector:
    matchLabels:
      app: label-service-a
      # Target for mTLS enforcement. Must exactly match the pod label:
      # Deployment.spec.template.metadata.labels.app --> "label-service-a"
  mtls:
    mode: STRICT
```
### How to check
Get pod names:
```
C:\CODE\k8s_istio\k8s>kubectl get pods -n skais-2-test
NAME                                    READY   STATUS    RESTARTS        AGE
deployment-service-a-b49448f6c-5smr9    2/2     Running   2 (3h52m ago)   22h
deployment-service-b-6f6fb5b4b6-9xhqh   2/2     Running   2 (3h52m ago)   22h
```
Try to access from `istio-proxy` (sidecar) (it will not encrypt traffic) to pod 
```
C:\CODE\k8s_istio\k8s>kubectl exec -it deployment-service-b-6f6fb5b4b6-9xhqh -n skais-2-test -c istio-proxy -- curl -i http://service-a-svc:8080/get-hello
curl: (56) Recv failure: Connection reset by peer
command terminated with exit code 56
```
**Get error: `Connection reset by peer`, this means that mTLS is enabled.**

Now delete `PeerAuthentication` rules:
```
kubectl delete peerauthentication strict-mtls-service-a -n skais-2-test
kubectl delete peerauthentication strict-mtls-service-b -n skais-2-test
```
Trying to access from `istio-proxy` (sidecar) to pod:
```
C:\CODE\k8s_istio\k8s>kubectl exec -it deployment-service-b-6f6fb5b4b6-9xhqh -n skais-2-test -c istio-proxy -- curl -i http://service-a-svc:8080/get-hello
HTTP/1.1 200 OK
content-type: application/json
content-length: 57
date: Fri, 07 Aug 2026 12:51:16 GMT
x-envoy-upstream-service-time: 4
server: istio-envoy
x-envoy-decorator-operation: service-a-svc.skais-2-test.svc.cluster.local:8080/*

{"role":"A1","timestamp":"2026-08-07T12:51:16.158092354"}
```
**Get response :`{"role":"A1","timestamp":"2026-08-07T12:51:16.158092354"}`, this means that mTLS is disabled.**

Trying to access from `port-forward`
`kubectl port-forward pod/deployment-service-b-6f6fb5b4b6-9xhqh -n skais-2-test 8082:8080`

http://localhost:8082/get-hello  
**OK** because `port-forward` is go directly to the JAVA application.
```
{
  "timestamp": "2026-08-07T12:59:12.787270192",
  "role": "B1"
}
```

## Access rules (AuthorizationPolicy)

```
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: block-b-to-a
  namespace: skais-2-test
spec:
  selector:
    matchLabels:
      app: label-service-a 
      # Target of protection. Must !!!exactly!!! match the pod label:
      # Deployment.spec.template.metadata.labels.app --> "label-service-a"
  action: DENY
  rules:
    - from:
        - source:
            principals: ["cluster.local/ns/skais-2-test/sa/service-b-sa"]
```
`cluster.local` -- cluster name
`ns/skais-2-test` -- namespace
`sa/service-b-sa` -- service account name (`Deployment.spec.template.spec.serviceAccountName --> "service-b-sa"`)

This means:

`Deployment.spec.template.spec.serviceAccountName === ServiceAccount.metadata.name`  

In addition `Deployment` should have own `ServiceAccount`.  
**Using default is bad practice (will affect all pods!!!)**  

So, create own `ServiceAccount` for `Deployment`:
```
apiVersion: v1
kind: ServiceAccount
metadata:
  name: service-b-sa
  namespace: skais-2-test
```
### How to check
Restricted access `DENY`:
http://localhost/service-b/get-communication-hello
```
[
  {
    "timestamp": "2026-08-07T10:16:02.775001086",
    "role": "B1"
  },
  {
    "response-get-hello": {
      "error": "Failed to reach host: 403 Forbidden: \"RBAC: access denied\""
    },
    "host": "http://service-a-svc:8080"
  }
]
```
Allowed access (no any `AuthorizationPolicy`):
http://localhost/service-a/get-communication-hello
```
[
  {
    "role": "A1",
    "timestamp": "2026-08-07T10:16:15.697369048"
  },
  {
    "host": "http://service-b-svc:8080",
    "response-get-hello": {
      "timestamp": "2026-08-07T10:16:15.780893831",
      "role": "B1"
    }
  },
  {
    "host": "http://service-b-svc:8080",
    "response-get-hello": {
      "timestamp": "2026-08-07T10:16:15.897189938",
      "role": "B1"
    }
  }
]
```

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