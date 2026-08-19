# Switching Local LoadBalancer Between NGINX and Istio

In the local Docker Desktop Kubernetes environment, only one ingress controller should normally be configured as `LoadBalancer` at a time.

A `LoadBalancer` Service requests an external LoadBalancer and an `EXTERNAL-IP`. The other ingress controller should remain `ClusterIP`.

If both are configured as `LoadBalancer`, both will try to create an external LoadBalancer, which can cause one of them to remain in `Pending` because the same external ports (`80/443`) cannot be allocated twice.

This allows switching between NGINX and Istio simply by changing the Service type, without reinstalling them or recreating the Kubernetes cluster.

---

# Switching: NGINX → Istio

First disable the NGINX LoadBalancer and then enable the Istio LoadBalancer.

```cmd
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\":{\"type\":\"ClusterIP\"}}"

kubectl patch svc gateway -n istio-system -p "{\"spec\":{\"type\":\"LoadBalancer\"}}"

kubectl get svc -n ingress-nginx
kubectl get svc -n istio-system
```

Expected output:

```text
C:\CODE\k8s_istio>kubectl get svc -n ingress-nginx

NAME                       TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)
ingress-nginx-controller   ClusterIP   10.96.225.96   <none>        80/TCP,443/TCP


C:\CODE\k8s_istio>kubectl get svc -n istio-system

NAME                   TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)
gateway   LoadBalancer   10.96.239.63   172.20.0.4    15021:31040/TCP,80:31940/TCP,443:30128/TCP
```

### What to check

The important values are:

```text
NGINX:
TYPE = ClusterIP
EXTERNAL-IP = <none>

Istio:
TYPE = LoadBalancer
EXTERNAL-IP = 172.20.0.4
```

This means **Istio owns the external LoadBalancer**.

---

# Switching: Istio → NGINX

First disable the Istio LoadBalancer and then enable the NGINX LoadBalancer.

```cmd
kubectl patch svc gateway -n istio-system -p "{\"spec\":{\"type\":\"ClusterIP\"}}"

kubectl patch svc ingress-nginx-controller -n ingress-nginx -p "{\"spec\":{\"type\":\"LoadBalancer\"}}"

kubectl get svc -n istio-system
kubectl get svc -n ingress-nginx
```

Expected output:

```text
C:\CODE\k8s_istio>kubectl get svc -n istio-system

NAME                   TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)
gateway   ClusterIP   10.96.239.63   <none>        15021/TCP,80/TCP,443/TCP


C:\CODE\k8s_istio>kubectl get svc -n ingress-nginx

NAME                       TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)
ingress-nginx-controller   LoadBalancer   10.96.225.96   172.20.0.4    80:32291/TCP,443:30468/TCP
```

### What to check

The important values are:

```text
Istio:
TYPE = ClusterIP
EXTERNAL-IP = <none>

NGINX:
TYPE = LoadBalancer
EXTERNAL-IP = 172.20.0.4
```

This means **NGINX owns the external LoadBalancer**.

---
