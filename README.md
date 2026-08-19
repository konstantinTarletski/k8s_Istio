# How to run
- Build microservice (Java)
- Install Istio infrastructure, choose one mode
- - [istio-ambient](infrastructure/istio-ambient) — no sidecar proxies
- - [istio-sidecar](infrastructure/istio-sidecar) — proxy in every pod
- - [Switch between modes](README_SWITCH_ISTIO.md)
- Apply [k8s](k8s) manifests
- Apply [istio-policies](istio-policies)
- Apply one of the gateways
- - [gateway-k8s-ingress](gateway-k8s-ingress)
- - [gateway-istio](gateway-istio)
- - [gateway-hybrid-k8s-istio](gateway-hybrid-k8s-istio) *(not tested yet)*

# If you know what to do, then commands list
[README_COMMANDS.md](README_COMMANDS.md)

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

# ISTIO Policies
> [!TIP]
> See Istio Readme: [README.md](istio-policies/README.md)

Communication rules `AuthorizationPolicy`:  
[authorization-policy-service-a.yaml](istio-policies/authorization-policy-service-a.yaml)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-a.yaml](istio-policies/peer-authentication-service-a.yaml)  
Istio `PeerAuthentication` mTLS rule  
[peer-authentication-service-b.yaml](istio-policies/peer-authentication-service-b.yaml)  


# gateway-k8s-ingress
> [!TIP]
> See Readme: [README.md](gateway-k8s-ingress/README.md)

[ingress.yaml](gateway-k8s-ingress/ingress.yaml)

# gateway-hybrid-k8s-istio *(not tested yet)*
> [!TIP]
> See Readme: [README.md](gateway-hybrid-k8s-istio/README.md)

# gateway-istio
> [!TIP]
> See Readme: [README.md](gateway-istio/README.md)

Istio `Gateway` Part of Istio "Ingress"  
[istio-gateway.yaml](gateway-istio/istio-gateway.yaml)  
Istio `VirtualService` Part of Istio "Ingress"  
[istio-virtualservice.yaml](gateway-istio/istio-virtualservice.yaml)

---

# Future work
- Prometheus + Grafana — metrics and dashboards for Istio traffic
- Test `gateway-hybrid-k8s-istio` setup
- Waypoint proxy for ambient mode (L7 policies)
