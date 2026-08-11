# Creating k8s

If you use local docker env  
Switch `kubectl` to local Docker env.
```
kubectl config use-context docker-desktop
```

Install OpenLens (if needed)  
UI Tool for kubernetes management 
```
C:\Users\dev>winget install openlens
```

## Rebuild Docker:

Rebuild java  
`mvn clean package`

Delete old image and build new:  
```cmd
docker rmi -f skais-microservice-example:latest
cd C:\CODE\k8s_istio\microservice\
docker build -t skais-microservice-example .
```
Chek that image exists:  
```cmd
docker images | findstr skais
skais-microservice-example:latest                         a78890f51ff2        346MB         95.2MB
```
Delete the old image and push the image to the k8s registry:  
```cmd
docker exec -it desktop-control-plane crictl rmi docker.io/library/skais-microservice-example:latest
docker exec -it desktop-control-plane crictl images | findstr skais
-- should be 0 !!
docker save skais-microservice-example:latest | docker exec -i desktop-control-plane ctr -n k8s.io images import -
```

## Create namespace:  
`kubectl create namespace skais-2-test`  
Delete namespace (if needed):  
`kubectl delete namespace skais-2-test`

## Apply all k8s YAML files:
```
kubectl apply -f C:\CODE\k8s_istio\k8s\
```

## Logs:
```
kubectl get pods -n skais-2-test
deployment-service-a-784ddbc66-zsz7f    0/1     Error    3 (57s ago)   78s
deployment-service-b-5775c6f556-dsm7n   0/1     Error    3 (57s ago)   78s

kubectl logs deployment-service-a-784ddbc66-zsz7f
```

## Check that services are up and running:
```
kubectl port-forward svc/service-a-svc 8080:8080 --namespace=skais-2-test
```
http://localhost:8080/get-communication-hello  
http://localhost:8080/get-hello