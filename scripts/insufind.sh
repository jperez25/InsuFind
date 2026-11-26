#!/usr/bin/env bash
# Start or tear down the local stack: colima VM -> minikube -> database -> app -> ui.
#
#   scripts/insufind.sh [-all] [-vm] [-minikube] [-db] [-app] [-ui] [-down]
#
# No service flag (or -all) means every service. Flags combine: `-db -app`.
# -down reverses the action, in reverse order (app, db, minikube, vm):
#   -ui   down  removes the UI from the cluster
#   -app  down  removes the app (API) from the cluster
#   -db   down  removes Postgres (demo data is reseeded by Flyway on the next start)
#   -minikube down  stops minikube (images stay cached)
#   -vm   down  stops the colima VM
# Env: CPUS (default 4), MEMORY in GB (default 8).
set -euo pipefail
SCRIPT="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"
cd "$(dirname "$SCRIPT")/.."

CPUS="${CPUS:-4}"
MEMORY="${MEMORY:-8}"

vm=false; minikube=false; db=false; app=false; ui=false; down=false
usage() { sed -n '2,13p' "$SCRIPT" | sed 's/^# \{0,1\}//'; exit "${1:-2}"; }

for arg in "$@"; do
  case "$arg" in
    -all)      vm=true; minikube=true; db=true; app=true; ui=true ;;
    -vm)       vm=true ;;
    -minikube) minikube=true ;;
    -db)       db=true ;;
    -app)      app=true ;;
    -ui)       ui=true ;;
    -down)     down=true ;;
    -h|-help|--help) usage 0 ;;
    *) echo "Unknown flag: $arg" >&2; usage ;;
  esac
done
if ! $vm && ! $minikube && ! $db && ! $app && ! $ui; then vm=true; minikube=true; db=true; app=true; ui=true; fi

need_vm()       { colima status >/dev/null 2>&1 || { echo "colima is not running (use -vm)" >&2; exit 1; }; }
need_minikube() { minikube status >/dev/null 2>&1 || { echo "minikube is not running (use -minikube)" >&2; exit 1; }; }

up_vm() {
  colima status >/dev/null 2>&1 || colima start --cpu "$CPUS" --memory "$MEMORY"
}

up_minikube() {
  need_vm
  # minikube gets 1 GB less than colima: the VM itself needs some memory.
  minikube status >/dev/null 2>&1 \
    || minikube start --driver=docker --container-runtime=docker --cpus="$CPUS" --memory="$(((MEMORY - 1) * 1024))"
}

up_db() {
  need_minikube
  kubectl apply -f deploy/k8s/postgres.yaml
  kubectl rollout status deployment/postgres --timeout=180s
}

# Build an image inside minikube's docker (no registry; manifests use imagePullPolicy: Never),
# apply its manifest, restart it to pick up the new image and wait for it.
deploy() {  # $1 = app|ui
  need_minikube
  eval "$(minikube docker-env)"
  if [[ "$1" == app ]]; then
    docker build -f insufind-app/Dockerfile -t insufind-app:latest .
  else
    docker build -t insufind-ui:latest insufind-ui
  fi
  kubectl apply -f "deploy/k8s/$1.yaml"
  kubectl rollout restart "deployment/insufind-$1"
  kubectl rollout status "deployment/insufind-$1" --timeout=180s
}

up_app() { deploy app; }
up_ui()  { deploy ui; echo "Run scripts/port-forward.sh to reach the UI on http://localhost:5173"; }

down_ui()       { need_minikube; kubectl delete -f deploy/k8s/ui.yaml --ignore-not-found; }
down_app()      { need_minikube; kubectl delete -f deploy/k8s/app.yaml --ignore-not-found; }
down_db()       { need_minikube; kubectl delete -f deploy/k8s/postgres.yaml --ignore-not-found; }
down_minikube() { ! minikube status >/dev/null 2>&1 || minikube stop; }
down_vm()       { ! colima status >/dev/null 2>&1 || colima stop; }

if $down; then
  ! $ui       || down_ui
  ! $app      || down_app
  ! $db       || down_db
  ! $minikube || down_minikube
  ! $vm       || down_vm
else
  ! $vm       || up_vm
  ! $minikube || up_minikube
  ! $db       || up_db
  ! $app      || up_app
  ! $ui       || up_ui
fi
