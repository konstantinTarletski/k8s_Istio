# All main commands together
## nginx
```cmd
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\": {\"type\": \"ClusterIP\"}}"
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\": {\"type\": \"LoadBalancer\"}}"

kubectl get svc -n ingress-nginx
kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx

!!!! enable ISTIO
kubectl label namespace ingress-nginx istio-injection=enabled
kubectl label namespace ingress-nginx istio-injection-
```

## istio
```cmd
kubectl patch svc istio-ingressgateway -n istio-system -p "{\"spec\": {\"type\": \"ClusterIP\"}}"
kubectl patch svc istio-ingressgateway -n istio-system -p "{\"spec\": {\"type\": \"LoadBalancer\"}}"

kubectl get svc -n istio-system
kubectl rollout restart deployment/istio-ingressgateway -n istio-system
```

## k8s apply
```cmd
kubectl apply -f C:\CODE\k8s_istio\k8s\
kubectl apply -f C:\CODE\k8s_istio\gateway-global-ingress
kubectl apply -f C:\CODE\k8s_istio\gateway-istio
kubectl apply -f C:\CODE\k8s_istio\istio\
```
## k8s delete
```cmd
kubectl delete -f C:\CODE\k8s_istio\k8s\
kubectl delete -f C:\CODE\k8s_istio\gateway-global-ingress
kubectl delete -f C:\CODE\k8s_istio\gateway-istio
kubectl delete -f C:\CODE\k8s_istio\istio\
```