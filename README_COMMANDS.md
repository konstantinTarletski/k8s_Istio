# All commands cheat sheet

## 1. k8s manifests (deploy microservices)
```cmd
kubectl apply -k C:\CODE\k8s_istio\k8s
kubectl delete -k C:\CODE\k8s_istio\k8s
```

### Verify with port-forward
```cmd
kubectl port-forward svc/service-a-svc 8080:8080 -n skais-2-test
```
http://localhost:8080/get-communication-hello  
http://localhost:8080/get-hello  

---

## 2. Gateways (choose one)

### gateway-istio
```cmd
kubectl apply -f C:\CODE\k8s_istio\gateway-istio
kubectl delete -f C:\CODE\k8s_istio\gateway-istio
```

### gateway-k8s-ingress
```cmd
kubectl apply -k C:\CODE\k8s_istio\infrastructure\nginx
kubectl apply -f C:\CODE\k8s_istio\gateway-k8s-ingress
kubectl delete -f C:\CODE\k8s_istio\gateway-k8s-ingress
```

Enable Istio on NGINX ingress namespace:
```cmd
kubectl label namespace ingress-nginx istio-injection=enabled --overwrite
kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx
```
Remove Istio from NGINX:
```cmd
kubectl label namespace ingress-nginx istio-injection-
kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx
```
**A** microservice:  
http://localhost/service-a/get-hello  
http://localhost/service-a/get-communication-hello  

**B** microservice:  
http://localhost/service-b/get-hello  
http://localhost/service-b/get-communication-hello  


### gateway-hybrid-k8s-istio
> TODO: not implemented yet

### LoadBalancer switching (NGINX ↔ Istio)
See [README_SWITCH_LB.md](README_SWITCH_LB.md)

---

## 3. Istio install (choose one mode)

### Ambient
```cmd
kubectl kustomize C:\CODE\k8s_istio\infrastructure\istio-ambient --enable-helm | kubectl apply -f -
kubectl label namespace skais-2-test istio.io/dataplane-mode=ambient --overwrite
```

### Sidecar
```cmd
kubectl kustomize C:\CODE\k8s_istio\infrastructure\istio-sidecar --enable-helm | kubectl apply -f -
kubectl label namespace skais-2-test istio-injection=enabled --overwrite
```

### Istio uninstall
```cmd
kubectl delete namespace istio-system
```

### Switch between modes
See [README_SWITCH_ISTIO.md](README_SWITCH_ISTIO.md)

---

## 4. istio-policies
```cmd
kubectl apply -f C:\CODE\k8s_istio\istio-policies\
kubectl delete -f C:\CODE\k8s_istio\istio-policies\
```

---

## Useful checks
```cmd
kubectl get pods -n skais-2-test
kubectl get pods -n istio-system
kubectl get svc -n istio-system
kubectl get namespace skais-2-test --show-labels
```