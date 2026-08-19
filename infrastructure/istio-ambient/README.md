# Istio Ambient Mode

In ambient mode Istio works **without sidecar containers**.
Traffic is intercepted at the **node level** by `ztunnel` — a shared proxy that runs as a DaemonSet.
Application pods have **no extra containers** and do not need to be restarted when Istio is installed.

mTLS encryption between pods happens transparently through `ztunnel`, without modifying the application.

Namespace label: `istio.io/dataplane-mode=ambient`

## Install / Uninstall / Switch modes

> [!TIP]
> See [README_SWITCH_ISTIO.md](../../README_SWITCH_ISTIO.md)

