#!/usr/bin/env bash
# Expose the cluster locally (stays in the foreground, Ctrl+C to stop).
#   scripts/port-forward.sh          start the forwards
#   scripts/port-forward.sh -down    stop leftover InsuFind forwards (e.g. from a closed terminal)
#   UI  -> http://localhost:5173  (same as the Vite dev server, so UI tests' default BASE_URL works)
#   API -> http://localhost:8080  (for integration tests, API_URL)
#   DB  -> localhost:5432         (psql / IDE database client; same port as docker-compose's postgres,
#                                  so stop that one first: docker compose stop postgres)
set -euo pipefail

PORTS=(5173 8080 5432)

# Who is listening on a port (empty if free).
listener() { lsof -nP -iTCP:"$1" -sTCP:LISTEN -Fcp 2>/dev/null | tr '\n' ' '; }

if [[ "${1:-}" == "-down" ]]; then
  # Only kill our own kubectl forwards, never whatever else happens to use these ports
  # (e.g. the Vite dev server on 5173, a local Spring Boot run on 8080 or docker-compose's postgres).
  pkill -f "kubectl port-forward svc/(insufind-|postgres)" && echo "Stopped InsuFind port forwards." || echo "No InsuFind port forwards running."
  sleep 1
  for port in "${PORTS[@]}"; do
    [[ -z "$(listener "$port")" ]] || echo "Port $port is still in use by another process: $(listener "$port")" >&2
  done
  exit 0
fi

for port in "${PORTS[@]}"; do
  if [[ -n "$(listener "$port")" ]]; then
    echo "Port $port is already in use: $(listener "$port")" >&2
    echo "Run scripts/port-forward.sh -down to stop old forwards, or free the port yourself." >&2
    exit 1
  fi
done

cat <<'EOF'
Port forwards (localhost -> cluster service):

  PORT   SERVICE        POD PORT   URL                      USED BY
  5173   insufind-ui    80         http://localhost:5173    browser, UI tests (BASE_URL)
  8080   insufind-app   8080       http://localhost:8080    integration tests (API_URL), /actuator/health
  5432   postgres       5432       localhost:5432           psql / DB client (db insufind, user insufind)

  The UI's nginx also proxies /api to insufind-app, so http://localhost:5173/api/claims works too.
  Ctrl+C to stop.

EOF

trap 'kill $(jobs -p) 2>/dev/null || true' EXIT
kubectl port-forward svc/insufind-ui 5173:80 &
kubectl port-forward svc/insufind-app 8080:8080 &
kubectl port-forward svc/postgres 5432:5432 &
wait
