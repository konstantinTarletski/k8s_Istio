# All main commands together
## nginx gateway
```cmd
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\": {\"type\": \"ClusterIP\"}}"
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\": {\"type\": \"LoadBalancer\"}}"

kubectl get svc -n ingress-nginx
kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx

!!!! enable ISTIO on ingress
kubectl label namespace ingress-nginx istio-injection=enabled
kubectl label namespace ingress-nginx istio-injection-
```

## istio gateway
```cmd
kubectl patch svc istio-ingressgateway -n istio-system -p "{\"spec\": {\"type\": \"ClusterIP\"}}"
kubectl patch svc istio-ingressgateway -n istio-system -p "{\"spec\": {\"type\": \"LoadBalancer\"}}"

kubectl get svc -n istio-system
kubectl rollout restart deployment/istio-ingressgateway -n istio-system
```

## YAML apply
```cmd
kubectl apply -f C:\CODE\k8s_istio\k8s\
kubectl apply -f C:\CODE\k8s_istio\gateway-global-ingress
kubectl apply -f C:\CODE\k8s_istio\gateway-istio
kubectl apply -f C:\CODE\k8s_istio\istio\
```
## YAML delete
```cmd
kubectl delete -f C:\CODE\k8s_istio\k8s\
kubectl delete -f C:\CODE\k8s_istio\gateway-global-ingress
kubectl delete -f C:\CODE\k8s_istio\gateway-istio
kubectl delete -f C:\CODE\k8s_istio\istio\
```