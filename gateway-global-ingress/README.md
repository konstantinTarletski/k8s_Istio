# Modify the existing Ingress GLOBAL service

To run this, you need to apply this Ingress controller once:  
`kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml`

### Microservices access links:
A microservice:  
- http://localhost/service-a/get-hello
- http://localhost/service-a/get-communication-hello

B microservice:  
- http://localhost/service-b/get-hello
- http://localhost/service-b/get-communication-hello

## Switch ON/OFF Ingress:

### Switch ON

1. Do "Switching: Istio → NGINX" from here:
[README_SWITCH_LB.md](../README_SWITCH_LB.md)

2. Apply Ingress :  
   `kubectl apply -f C:\CODE\k8s_istio\gateway-global-ingress`

Restart if needed:
`kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx`

### Switch OFF

1. Delete Ingress :  
      `kubectl delete -f C:\CODE\k8s_istio\gateway-global-ingress`
2. Can switch NGINX to `ClusterIP` from here: [README_SWITCH_LB.md](../README_SWITCH_LB.md)
   (Not mandatory)

## Switch ON/OFF ISTIO in Ingress:
**Switch System (!!! ALL k8S !!!) Ingress to Istio (not good from my side)**

>[!TIP]
>Add annotation (not mandatory) `nginx.ingress.kubernetes.io/service-upstream: "true"` is a pure Kubernetes thing:

Without ISTIO:
- WITHOUT ANNOTATION (Default):
  NGINX is the Load Balancer. Uses advanced algorithms (least_conn, ip_hash),
  re-uses TCP connections (keep-alive), and talks directly to Pod IPs.
- WITH service-upstream: "true":
  kube-proxy (iptables) is the Load Balancer. Dumb random routing at Linux kernel level.
  NGINX acts only as a proxy to the Service ClusterIP. No keep-alive to Pods.

With ISTIO
Add to our ingress :  
`Ingress.metadata.annotations`:  
`nginx.ingress.kubernetes.io/service-upstream: "true"`  
Set to `true` enables to "use" Istio `VirtualService`:
- Traffic split, for example, 80/20
- Retries, for example, `attempts: 3`

### Switch ON

1. **!!! Modifying `ingress-nginx` namespace !!!**  
`kubectl label namespace ingress-nginx istio-injection=enabled`

Restart if needed:
`kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx`

### Switch OFF

1. `kubectl label namespace ingress-nginx istio-injection-`  
   `-` -- deletes label

Restart if needed:
`kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx`
