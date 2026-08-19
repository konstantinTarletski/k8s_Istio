# gateway-hybrid-k8s-istio

Hybrid: standard k8s NGINX Ingress as external entry point, Istio Gateway handles internal routing with mTLS.

```
External request (browser)
        |
        v
+---------------------+
|  NGINX Ingress      |  <-- standard k8s Ingress
|  (LoadBalancer)     |      accepts HTTP from outside
+--------+------------+
         |  forwards to `gateway` service in istio-system
         v
+---------------------+
|  Istio Gateway      |  <-- Istio IngressGateway (Envoy)
|  (istio-system)     |      accepts traffic inside cluster
+--------+------------+
         |  VirtualService decides where to route
         v
+---------------------+
|  VirtualService     |  <-- routing by path:
|                     |      /service-a -> service-a-svc
|                     |      /service-b -> service-b-svc
+--------+------------+
         |  mTLS (automatic through mesh)
         v
+---------------------+
|  App Pods           |  <-- services with mTLS + AuthorizationPolicy
+---------------------+
```

## Apply
```cmd
kubectl apply -f C:\CODE\k8s_istio\gateway-hybrid-k8s-istio
```

## Delete
```cmd
kubectl delete -f C:\CODE\k8s_istio\gateway-hybrid-k8s-istio
```

## Prerequisites
- NGINX ingress controller installed (`kubectl apply -k C:\CODE\k8s_istio\infrastructure\nginx`)
- Istio installed (ambient or sidecar) -- both include `gateway` service in `istio-system`

## Files
- **ingress.yaml** -- k8s Ingress, forwards `/service-a/*` and `/service-b/*` to Istio Gateway service
- **istio-gateway.yaml** -- Istio Gateway, listens on port 80 on IngressGateway
- **istio-virtualservice.yaml** -- routes and rewrites paths to app services