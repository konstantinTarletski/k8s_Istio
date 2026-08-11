# How to run
- Build microservice (Java)
- Apply k8s manifests
- Apply istio manifests
- Apply One of :
- - gateway-global-ingress
- - gateway-istio
- - gateway-hybrid-ingress-istio

# Parts of this project:

## Microservice
> [!TIP]
> See microservice: [README.md](microservice/README.md)

- Java 25 microservice.
- Spring-boot 4.0.7
- Lombok

# K8S
> [!TIP]
> See k8s Readme: [README.md](k8s/README.md)

[deployment-service-a.yaml](k8s/deployment-service-a.yaml)  
[deployment-service-b.yaml](k8s/deployment-service-b.yaml)  
[service-a-svc.yaml](k8s/service-a-svc.yaml)  
[service-b-svc.yaml](k8s/service-b-svc.yaml)  
Actually not needed here, but it is necessary for `AuthorizationPolicy`, `Deployment` have a link to it  
[service-account-service-a.yaml](k8s/service-account-service-a.yaml)  
Actually not needed here, but it is necessary for `AuthorizationPolicy`, `Deployment` have a link to it  
[service-account-service-b.yaml](k8s/service-account-service-b.yaml)

# ISTIO
> [!TIP]
> See Istio Readme: [README.md](../istio/README.md)

Communication rules `AuthorizationPolicy`:  
[authorization-policy-service-a.yaml](istio/authorization-policy-service-a.yaml)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-a.yaml](istio/peer-authentication-service-a.yaml)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-b.yaml](istio/peer-authentication-service-b.yaml)  


# gateway-global-ingress
> [!TIP]
> See Readme: [README.md](gateway-global-ingress/README.md)

[ingress.yaml](gateway-global-ingress/ingress.yaml)

# gateway-hybrid-ingress-istio
> [!TIP]
> See Readme: [README.md](gateway-hybrid-ingress-istio/README.md)

# gateway-istio
> [!TIP]
> See Readme: [README.md](gateway-istio/README.md)

Istio `Gateway` Part of Istio "Ingress"  
[istio-gateway.yaml](gateway-istio/istio-gateway.yaml)  
Istio `VirtualService` Part of Istio "Ingress"  
[istio-virtualservice.yaml](gateway-istio/istio-virtualservice.yaml)
