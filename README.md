# Microservice
> [!TIP]
> See microservice: [README.md](microservice/README.md)

- Java 25 microservice.
- Spring-boot 4.0.7
- Lombok

Endpoints:
- `/get-hello` -- Responses name and time 
- `/get-communication-hello` -- Responses name, time and "depended" microservices.

# K8S
> [!TIP]
> See k8s Readme: [README.md](k8s/README.md)

[deployment-service-a.yaml](k8s/deployment-service-a.yaml)  
[deployment-service-b.yaml](k8s/deployment-service-b.yaml)  
[ingress.yaml](k8s/ingress.yaml)
[service-account-service-a.yaml](k8s/service-account-service-a.yaml)  
[service-account-service-b.yaml](k8s/service-account-service-b.yaml)  

# ISTIO
> [!TIP]
> See Istio Readme: [README.md](../istio/README.md)

Communication rules `AuthorizationPolicy`:  
[authorization-policy-service-a.yaml](istio/authorization-policy-service-a.yaml)  
Istio `Gateway` Part of Istio "Ingress"  
[istio-gateway.yaml.bak](istio/istio-gateway.yaml.bak)  
Istio `VirtualService` Part of Istio "Ingress"  
[istio-virtualservice.yaml.bak](istio/istio-virtualservice.yaml.bak)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-a.yaml](istio/peer-authentication-service-a.yaml)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-b.yaml](istio/peer-authentication-service-b.yaml)  
Actually **it is part of `K8S`** but it is necessary for `AuthorizationPolicy`  
[service-a-svc.yaml](istio/service-a-svc.yaml)  
Actually **it is part of `K8S`** but it is necessary for `AuthorizationPolicy`  
[service-b-svc.yaml](istio/service-b-svc.yaml)  

# How to run
- Build microservice (Java)
- Apply k8s manifests
- Apply istio manifests