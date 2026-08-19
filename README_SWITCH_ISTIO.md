# Switching Istio Between Sidecar and Ambient Modes

Both modes cannot run at the same time. To switch, the old Istio installation must be fully removed first.

Microservices in `skais-2-test` namespace are preserved during the switch — only the `istio-system` namespace is deleted and recreated.

After switching, deployments must be restarted so that pods pick up the new mesh configuration (sidecar injected or removed).

---

# Switching: Sidecar → Ambient

First remove the old Istio and sidecar label, then install ambient.

```cmd
kubectl delete namespace istio-system

kubectl label namespace skais-2-test istio-injection-

kubectl kustomize C:\CODE\k8s_istio\infrastructure\istio-ambient --enable-helm | kubectl apply -f -

kubectl rollout restart deployment -n skais-2-test
```

### What to check

```cmd
kubectl get namespace skais-2-test --show-labels
kubectl get pods -n istio-system
kubectl get pods -n skais-2-test
```

Expected labels on `skais-2-test`:

```text
istio.io/dataplane-mode=ambient
```

No sidecar containers in application pods (each pod has only 1 container).

---

# Switching: Ambient → Sidecar

First remove the old Istio and ambient label, then install sidecar.

```cmd
kubectl delete namespace istio-system

kubectl label namespace skais-2-test istio.io/dataplane-mode-

kubectl kustomize C:\CODE\k8s_istio\infrastructure\istio-sidecar --enable-helm | kubectl apply -f -

kubectl rollout restart deployment -n skais-2-test
```

### What to check

```cmd
kubectl get namespace skais-2-test --show-labels
kubectl get pods -n istio-system
kubectl get pods -n skais-2-test
```

Expected labels on `skais-2-test`:

```text
istio-injection=enabled
```

Each application pod should have 2 containers (app + `istio-proxy` sidecar).

---
