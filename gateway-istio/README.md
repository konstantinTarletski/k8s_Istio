# Using Istio own `Gateway`

It is already present when yu install ISTIO

### Microservices access links:
A microservice:  
- http://localhost/service-istio-a/get-hello  
- http://localhost/service-istio-a/get-communication-hello  

B microservice:  
- http://localhost/service-istio-b/get-hello  
- http://localhost/service-istio-b/get-communication-hello  


## Switch ON/OF Istio own `Gateway`:

### Switch ON
1. Do "Switching: NGINX → Istio" from here:
   [README_SWITCH_LB.md](../README_SWITCH_LB.md)

2. Apply the Istio gateway and virtual service definitions 
`kubectl apply -f C:\CODE\k8s_istio\gateway-istio`

### Switch OOFF
1. Remove Istio routing definitions from the local namespace  
   `kubectl delete -f C:\CODE\k8s_istio\gateway-istio\ --ignore-not-found`

2. Can switch ISTIO to `ClusterIP` from here: [README_SWITCH_LB.md](../README_SWITCH_LB.md)
   (Not mandatory)






