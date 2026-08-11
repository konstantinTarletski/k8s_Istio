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

Apply all k8s YAML files:

`kubectl apply -f C:\CODE\k8s_istio\istio\`

## GATEWAY
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
> See: [README.md](../gateway-global-ingress/README.md)

### 2. Using Istio own `Gateway`

>[!WARNING]
> **Better solution**  
> You can use "OLD" existing Ingress and in parallel add new Istio Gateway.  
> But you need to create all Ingress rules manually again. !!!  
> See: [README.md](../gateway-istio/README.md)

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
### How to check 2
1. Do switch ON Ingress from here:[README.md](../gateway-global-ingress/README.md)
2. Do switch OFF ISTIO in Ingress from here: [README.md](../gateway-global-ingress/README.md)
3. Try to access: http://localhost/service-b/get-communication-hello  
You will see error `502 Bad Gateway`

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
See **Failed to reach host: 403 Forbidden: \"RBAC: access denied\"**  

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

## External VM Integration (In Simple Terms)

### 1. The Simple Way (Via NGINX Ingress)
* **How it works:** The external VM connects to Kubernetes just like a regular internet user—via the public IP/DNS of the NGINX Ingress [🔗].
* **Pros:** Very easy to configure; no extra software needed on the VM.
* **Cons:** No end-to-end mTLS encryption to the Pods, and the VM cannot use internal Kubernetes DNS names (it cannot call `http://service-a-svc:8080`) [🔗].

### 2. The Istio Way (Via WorkloadEntry)
* **How it works:** You install an Envoy proxy directly inside the VM and give it a Kubernetes passport (`ServiceAccount`) [🔗].
* **Pros:** The VM becomes a full member of the Service Mesh. It gets **STRICT mTLS** security, respects `AuthorizationPolicy`, and can natively call internal cluster DNS names [🔗].
* **Cons:** Higher setup complexity; requires maintaining the Envoy proxy on the VM.
