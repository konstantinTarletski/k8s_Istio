# Istio Sidecar Mode

In sidecar mode Istio injects an **`istio-proxy` container into every pod**.
Each pod gets its own Envoy proxy that handles all inbound and outbound traffic.
Pods must be **restarted** after enabling sidecar injection so the proxy container is added.

mTLS encryption between pods is handled by the sidecar proxy inside each pod.

Namespace label: `istio-injection=enabled`

## Install / Uninstall / Switch modes

> [!TIP]
> See [README_SWITCH_ISTIO.md](../../README_SWITCH_ISTIO.md)
