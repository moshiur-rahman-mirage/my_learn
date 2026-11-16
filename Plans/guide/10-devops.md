@@ 1 | MODULE | Phase 10 — DevOps: Git, Docker, Kubernetes, CI/CD | Version control, Linux, containers, orchestration, pipelines | SHOULD
# Phase 10: DevOps

The TOR prefers **Docker, Kubernetes, Git and CI/CD**. Your portfolio should have a `Dockerfile`, a `docker-compose.yml` and a CI pipeline, and you should be able to explain every line.

@@ 2 | TOPIC | Git | Daily commands, branching, merge vs rebase, conflicts, workflows | MUST
# Git

Git is a **version control system**: it records snapshots of your project so you can go back, compare, and work in parallel on branches.
**Analogy:** save points in a video game, plus parallel universes (branches) that you can later merge back together.

## Daily workflow
```bash
git clone https://github.com/you/loan-system.git
git checkout -b feature/repayment-api        # create and switch to a new branch
# ... edit files ...
git status                                    # what changed?
git add src/main/java/.../RepaymentService.java
git commit -m "feat(repayment): record repayment and update outstanding"
git pull --rebase origin main                 # get the latest main, replay my commits on top
git push -u origin feature/repayment-api      # publish the branch
# open a Pull Request -> review -> merge
```
**Line by line**
- `checkout -b`: a new branch, so `main` stays stable while you work.
- `git add`: moves changes to the **staging area** (choose what goes into the next commit).
- `git commit -m`: saves a snapshot with a message. Use clear messages, e.g. the **Conventional Commits** style: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`.
- `pull --rebase`: avoids extra merge commits and keeps history linear.
- `push -u`: uploads and sets the upstream, so later a plain `git push` works.

**The three areas:** working directory (your files) → staging area (`git add`) → repository (`git commit`).

## Merge vs rebase
```
main:     A---B---C
feature:       \---D---E

merge:    A---B---C---M        (M = merge commit, history kept as it happened)
               \---D---E/

rebase:   A---B---C---D'---E'  (feature commits replayed on top: a linear history)
```
- **Merge:** safe, keeps the true history, but adds merge commits.
- **Rebase:** a cleaner, linear history, but it **rewrites commits**. **Never rebase commits that others have already pulled** (shared branches).
- **Analogy:** merge is stapling two notebooks together. Rebase is rewriting your notes neatly onto the end of the main notebook.

## Resolving a conflict
```
<<<<<<< HEAD
BigDecimal rate = new BigDecimal("0.12");
=======
BigDecimal rate = new BigDecimal("0.10");
>>>>>>> feature/new-rate
```
- Both branches changed the same line. Edit the file to the correct final version, delete the markers, then `git add` and `git commit` (or `git rebase --continue`).

## Undoing things
```bash
git restore file.txt              # discard unstaged changes to a file
git restore --staged file.txt     # unstage
git commit --amend                # fix the last commit (only if not pushed)
git revert <sha>                  # NEW commit that undoes an old one (safe on shared branches)
git reset --soft HEAD~1           # undo the last commit, keep the changes staged
git stash / git stash pop         # put work aside temporarily
git log --oneline --graph         # view history
git cherry-pick <sha>             # copy one commit onto the current branch
```
- `revert` vs `reset`: **revert is safe for shared history** (it adds a commit). `reset --hard` destroys work and rewrites history.

## Branching strategies
- **GitHub Flow:** `main` is always deployable. Short-lived feature branches go through PRs. Simple, and good for CI/CD.
- **Git Flow:** `main`, `develop`, `feature/*`, `release/*`, `hotfix/*`. For scheduled releases.
- **Trunk-based:** everyone merges small changes to main often, using feature flags.

`.gitignore`: never commit `target/`, `node_modules/`, `.env` or secrets.

@@ 2 | TOPIC | Linux and Servers | Essential commands, processes, logs, permissions, Nginx reverse proxy | SHOULD
# Linux and servers

Most servers run Linux. You should be comfortable on the command line.

```bash
# Files and navigation
pwd; ls -la; cd /var/log
cat app.log | less
tail -f /var/log/loan-api/app.log          # follow the log live
grep -i "error" app.log | tail -n 50       # last 50 error lines
grep -rn "OutOfMemory" /var/log/           # search recursively with line numbers
find / -name "*.jar" -size +50M 2>/dev/null

# Processes and ports
ps aux | grep java                         # is the app running?
top / htop                                 # CPU and memory usage
kill -15 <pid>                             # ask nicely to stop (SIGTERM)
kill -9 <pid>                              # force kill (last resort)
ss -tulpn | grep 8080                      # which process listens on 8080?

# Disk and memory
df -h                                      # disk space
du -sh /var/lib/postgresql                 # folder size
free -h                                    # memory

# Permissions
chmod 750 deploy.sh                        # owner rwx, group r-x, others none
chown app:app /opt/loan-api -R

# Networking
curl -i http://localhost:8080/actuator/health
ping db.internal; nslookup api.pksf.org.bd

# Services (systemd)
sudo systemctl status loan-api
sudo systemctl restart loan-api
journalctl -u loan-api -f                  # service logs
```
**Key explanations**
- `tail -f`: watch new log lines as they are written, the first thing to do when debugging production.
- `|` (pipe): sends one command's output into the next (`grep` filters, `tail` keeps the last lines).
- `2>/dev/null`: hides error messages (stream 2 = stderr).
- `kill -15` lets Spring Boot shut down gracefully (finish requests, close DB connections). `-9` kills it instantly.
- `chmod 750`: each digit is owner / group / others, and r=4 + w=2 + x=1. So 7 = rwx, 5 = r-x, 0 = none.

## Nginx as a reverse proxy

```nginx
server {
    listen 443 ssl;
    server_name app.pksf.org.bd;
    ssl_certificate     /etc/ssl/certs/app.crt;
    ssl_certificate_key /etc/ssl/private/app.key;

    root /usr/share/nginx/html;              # React build files
    location / {
        try_files $uri /index.html;          # SPA: unknown paths -> index.html
    }

    location /api/ {
        proxy_pass http://loan-api:8080;     # forward to Spring Boot
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
**Line by line**
- A **reverse proxy** sits in front of your apps: it terminates HTTPS, serves static files, and forwards API calls. **Analogy:** a receptionist who greets every visitor and walks them to the right office.
- `ssl_certificate`: HTTPS is handled here, so the app itself can speak plain HTTP inside the private network.
- `try_files $uri /index.html`: lets React Router handle URLs like `/members/42` on refresh.
- `proxy_pass`: forwards `/api/...` to the backend.
- `X-Forwarded-*` headers: tell the app the real client IP and the original protocol (set `server.forward-headers-strategy=framework` in Spring).

@@ 2 | MODULE | Docker | Images, containers, Dockerfile, compose | SHOULD
**Docker** packages an app **with everything it needs** (runtime, libraries, config) into an **image** that runs the same everywhere.
**Analogy:** a shipping container. Whatever is inside, every ship, crane and truck handles it the same way. "Works on my machine" becomes "works on every machine".

@@ 3 | TOPIC | Docker Concepts and Commands | Image vs container, layers, volumes, networks, essential commands | SHOULD
# Docker concepts and commands

| Concept | Meaning | Analogy |
|---|---|---|
| **Image** | A read-only template (app + runtime + OS libraries) | A recipe / a class |
| **Container** | A running instance of an image | A cooked dish / an object |
| **Layer** | Each Dockerfile instruction makes a cached layer | Stacked transparent sheets |
| **Registry** | Stores images (Docker Hub, GHCR, Harbor) | An app store |
| **Volume** | Persistent storage outside the container | An external hard drive |
| **Network** | Lets containers talk by name | A private office LAN |

**Container vs VM:** a VM includes a whole guest OS (heavy, minutes to boot). Containers **share the host's kernel** and only isolate processes (light, seconds to start).
**Analogy:** VMs are separate houses, each with its own plumbing. Containers are apartments in one building, sharing the plumbing but each with its own locked door.

```bash
docker build -t loan-api:1.0 .                 # build an image from the Dockerfile in the current folder
docker images                                  # list images
docker run -d --name loan-api -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_PASSWORD=secret \
  loan-api:1.0                                 # run in the background
docker ps                                      # running containers (-a for all)
docker logs -f loan-api                        # follow the logs
docker exec -it loan-api sh                    # open a shell inside the container
docker stop loan-api && docker rm loan-api     # stop and remove
docker run -d --name pg -v pgdata:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=pw -p 5432:5432 postgres:16   # DB with a named volume
docker tag loan-api:1.0 ghcr.io/you/loan-api:1.0
docker push ghcr.io/you/loan-api:1.0           # upload to a registry
docker system prune                            # clean up unused data
```
**Line by line**
- `-t loan-api:1.0`: name:tag. **Avoid `latest` in production**: use explicit versions so you know exactly what's running.
- `-d`: detached (background).
- `-p 8080:8080`: **host port : container port**. Without it, the app isn't reachable from outside.
- `-e`: environment variables. This is how config and secrets get in (Spring maps `SPRING_DATASOURCE_URL` → `spring.datasource.url`).
- `exec -it ... sh`: debug inside a running container.
- `-v pgdata:/var/lib/postgresql/data`: the DB files live in a **volume**. Removing the container doesn't delete your data.
- A container should run **one main process** and be **disposable**: anything important lives in volumes or external services.

@@ 3 | TOPIC | Writing a Dockerfile | Multi-stage build for Spring Boot and React, best practices | SHOULD
# Writing a Dockerfile

## Spring Boot: multi-stage build

```dockerfile
# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline              # cache dependencies in their own layer
COPY src ./src
RUN mvn -q package -DskipTests

# ---------- Stage 2: run ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app  # don't run as root
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```
**Line by line**
- **Multi-stage:** stage 1 has Maven and the JDK (big) to **build**. Stage 2 copies only the JAR into a small **JRE** image. The final image is ~200 MB instead of ~800 MB, and contains no build tools (more secure).
- `FROM ... AS build`: names the stage so that we can copy from it later.
- `WORKDIR /app`: like `cd`, and it creates the folder.
- `COPY pom.xml` **before** `COPY src`: Docker caches layers. If only the code changes, the slow "download dependencies" layer is reused. **Order instructions from least to most frequently changing.**
- `-DskipTests`: tests run in CI before the image is built.
- `adduser` + `USER app`: if the app is hacked, the attacker isn't root.
- `COPY --from=build`: takes the JAR from stage 1.
- `EXPOSE 8080`: documentation only. It doesn't publish the port (`-p` does).
- `MaxRAMPercentage=75`: the JVM sizes its heap from the **container's** memory limit.
- `HEALTHCHECK`: Docker marks the container unhealthy if `/actuator/health` fails.
- `ENTRYPOINT [...]`: the command that runs at start. `sh -c` lets `$JAVA_OPTS` expand.

## React: build, then serve with Nginx
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci                                     # exact versions from package-lock
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```
- `npm ci`: a clean, reproducible install (faster than `npm install` and respects the lockfile).
- The final image is just Nginx + static files, with no Node.js in production.

## .dockerignore
```
target/
node_modules/
.git/
.env
```
- Keeps the build context small and stops secrets from being copied into the image.

**Best practices summary:** small base images (alpine/distroless), multi-stage builds, a non-root user, pinned versions, no secrets in the image (pass them at runtime), one process per container, and scan images (Trivy).

@@ 3 | TOPIC | Docker Compose | Running the full stack locally with one command | SHOULD
# Docker Compose

**Compose** defines several containers (app, DB, Redis, Keycloak) in one YAML file and starts them together.
**Analogy:** a band's setlist. One command and all the musicians start playing together, in the right order.

```yaml
# docker-compose.yml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: loans
      POSTGRES_USER: app
      POSTGRES_PASSWORD: ${DB_PASSWORD}         # from the .env file
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d loans"]
      interval: 5s
      retries: 10

  redis:
    image: redis:7-alpine

  keycloak:
    image: quay.io/keycloak/keycloak:26.0
    command: start-dev --import-realm
    environment:
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: ${KC_ADMIN_PASSWORD}
    volumes:
      - ./keycloak/realm-pksf.json:/opt/keycloak/data/import/realm-pksf.json
    ports:
      - "8180:8080"

  api:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/loans
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_DATA_REDIS_HOST: redis
    depends_on:
      db:
        condition: service_healthy              # wait until Postgres is really ready
      redis:
        condition: service_started
    ports:
      - "8080:8080"

  web:
    build: ./frontend
    ports:
      - "80:80"
    depends_on: [api]

volumes:
  pgdata:
```
```bash
docker compose up -d --build     # build and start everything
docker compose logs -f api       # follow one service's logs
docker compose down              # stop and remove the containers (volumes are kept)
docker compose down -v           # ... and delete the volumes (wipes the DB!)
```
**Line by line**
- `services:`: each key is a container. **The service name is also its hostname** on the shared network, so the API connects to `db:5432`, not `localhost`.
- `${DB_PASSWORD}`: read from a `.env` file next to the compose file (which stays out of Git).
- `volumes: pgdata:/var/lib/...`: the data survives `down`/`up`.
- `healthcheck` + `depends_on: condition: service_healthy`: the API starts only after Postgres accepts connections. Plain `depends_on` only waits for the container to *start*, not to be *ready*.
- `--import-realm`: Keycloak loads a pre-configured realm, so reviewers get a working login instantly.
- `build: ./backend`: build the image from that folder's Dockerfile.

**For the portfolio:** a reviewer should be able to run `git clone` + `docker compose up` and have the whole system working. That impresses interviewers.

@@ 2 | MODULE | Kubernetes | Pods, deployments, services, config, probes, scaling, ingress | SHOULD
**Kubernetes (K8s)** runs and manages containers across many machines: it restarts crashed ones, scales them, rolls out updates and balances traffic.
**Analogy:** if Docker containers are shipping containers, Kubernetes is the **port authority**. It decides which ship (node) carries what, replaces lost containers, and keeps the schedule running.

@@ 3 | TOPIC | Kubernetes Core Concepts | Cluster, node, pod, deployment, service, namespace, kubectl | SHOULD
# Kubernetes core concepts

| Object | What it is | Analogy |
|---|---|---|
| **Cluster** | The control plane + worker nodes | The whole port |
| **Node** | A machine (VM/server) that runs pods | A ship |
| **Pod** | The smallest unit: 1+ containers sharing a network/IP | A container box on the ship |
| **Deployment** | Keeps N identical pods running, handles rolling updates | The manager who keeps "3 cooks on duty" |
| **ReplicaSet** | Created by the Deployment, maintains the pod count | The roster |
| **Service** | A stable IP/DNS name + load balancing over pods | The restaurant's phone number (whichever cook is free answers) |
| **Ingress** | HTTP routing from outside (host/path → service) | The main gate with signposts |
| **ConfigMap / Secret** | Configuration / sensitive configuration | Notice board / locked drawer |
| **Namespace** | A virtual partition (dev, test, prod) | Separate floors in a building |
| **PersistentVolumeClaim** | A request for durable storage | Renting a storage locker |
| **StatefulSet** | Like a Deployment, but with stable identity and storage (databases) | Named reserved seats |

**The control plane:** API server (all commands go through it), etcd (the cluster's database), scheduler (picks a node for each pod), controller manager (keeps reality matching the desired state).

**Declarative model:** you describe the **desired state** ("3 replicas of loan-api:1.4") in YAML, and K8s keeps working until reality matches. Kill a pod and a new one appears.

```bash
kubectl get pods -n loans                     # list pods in a namespace
kubectl get deploy,svc,ingress -n loans
kubectl describe pod loan-api-7c9f-abc -n loans   # events: why is it failing?
kubectl logs -f loan-api-7c9f-abc -n loans    # app logs
kubectl logs --previous <pod>                 # logs of the crashed previous container
kubectl exec -it <pod> -n loans -- sh         # shell inside the pod
kubectl apply -f k8s/                         # create/update from YAML files
kubectl rollout status deploy/loan-api -n loans
kubectl rollout undo deploy/loan-api -n loans # roll back to the previous version
kubectl scale deploy/loan-api --replicas=5 -n loans
kubectl port-forward svc/loan-api 8080:80 -n loans   # reach a service from your laptop
```
**Debugging common pod states**
- `CrashLoopBackOff`: the app starts and crashes repeatedly. Check `logs --previous` (often a bad config or the DB being unreachable).
- `ImagePullBackOff`: wrong image name/tag or missing registry credentials.
- `Pending`: no node has enough CPU/memory, or the PVC can't be bound. Check `describe`.
- `OOMKilled`: the container exceeded its memory limit. Raise the limit or fix the heap settings.

@@ 3 | TOPIC | Kubernetes Manifests | Deployment, Service, ConfigMap, Secret, probes, resources, HPA, Ingress YAML | SHOULD
# Kubernetes manifests explained

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: loan-api-config
  namespace: loans
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/loans"
---
apiVersion: v1
kind: Secret
metadata:
  name: loan-api-secret
  namespace: loans
type: Opaque
stringData:
  SPRING_DATASOURCE_PASSWORD: "change-me"
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: loan-api
  namespace: loans
spec:
  replicas: 3
  selector:
    matchLabels: { app: loan-api }
  strategy:
    type: RollingUpdate
    rollingUpdate: { maxUnavailable: 0, maxSurge: 1 }
  template:
    metadata:
      labels: { app: loan-api }
    spec:
      containers:
        - name: loan-api
          image: ghcr.io/you/loan-api:1.4.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef: { name: loan-api-config }
            - secretRef: { name: loan-api-secret }
          resources:
            requests: { cpu: "250m", memory: "512Mi" }
            limits:   { cpu: "1",    memory: "1Gi" }
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 8080 }
            initialDelaySeconds: 20
            periodSeconds: 10
          livenessProbe:
            httpGet: { path: /actuator/health/liveness, port: 8080 }
            initialDelaySeconds: 40
            periodSeconds: 20
---
apiVersion: v1
kind: Service
metadata:
  name: loan-api
  namespace: loans
spec:
  selector: { app: loan-api }
  ports:
    - port: 80
      targetPort: 8080
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: loan-ingress
  namespace: loans
spec:
  ingressClassName: nginx
  tls:
    - hosts: [api.pksf.org.bd]
      secretName: api-tls
  rules:
    - host: api.pksf.org.bd
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service: { name: loan-api, port: { number: 80 } }
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: loan-api
  namespace: loans
spec:
  scaleTargetRef: { apiVersion: apps/v1, kind: Deployment, name: loan-api }
  minReplicas: 3
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target: { type: Utilization, averageUtilization: 70 }
```

**Line by line**
- `---`: separates several objects in one file.
- **ConfigMap:** non-secret settings as key-value pairs.
- **Secret:** sensitive values. `stringData` lets you write plain text, which K8s stores **base64-encoded, not encrypted** by default. In real setups, enable encryption at rest or use Sealed Secrets / External Secrets / Vault, and never commit real secrets.
- `replicas: 3`: always keep 3 pods running.
- `selector.matchLabels` must match `template.metadata.labels`: that's how the Deployment knows which pods are "its own".
- `RollingUpdate maxUnavailable: 0, maxSurge: 1`: during an update, start one new pod, wait until it is ready, then remove one old pod. **Zero downtime.**
- `envFrom`: injects all ConfigMap/Secret keys as environment variables, which Spring Boot reads automatically.
- `resources.requests`: what the scheduler **reserves** (250m = a quarter of a CPU core). `limits`: the hard ceiling. Going over the memory limit gets the container OOMKilled, and over the CPU limit gets it throttled.
- **readinessProbe:** "Can I receive traffic?" Until it passes, the Service doesn't send requests to this pod (e.g. while it warms up or loses its DB).
- **livenessProbe:** "Am I still alive?" If it fails repeatedly, K8s **restarts** the container (it is stuck). Don't make liveness depend on the DB, or a DB outage will restart every pod.
- **Analogy:** readiness = "the shop sign says OPEN". Liveness = "the shopkeeper is still breathing".
- **Service `ClusterIP`:** an internal, stable address `loan-api.loans.svc.cluster.local`, load balanced across all ready pods with the label `app: loan-api`. Other types: `NodePort` (a port on every node) and `LoadBalancer` (a cloud load balancer).
- `port: 80` → `targetPort: 8080`: callers use port 80, and the traffic goes to the container's 8080.
- **Ingress:** routes external HTTPS for `api.pksf.org.bd` to the service. It needs an ingress controller (e.g. ingress-nginx). `tls.secretName` holds the certificate (often managed by cert-manager).
- **HPA:** if the average CPU goes above 70% of the *requests*, add pods (up to 10). When it drops, scale down (never below 3). It needs metrics-server.

**Helm** packages these YAMLs as templated **charts**, with per-environment values (`values-prod.yaml`).

@@ 2 | TOPIC | CI/CD with GitHub Actions | Pipeline stages, build, test, image push, deploy | SHOULD
# CI/CD with GitHub Actions

- **CI (Continuous Integration):** every push automatically builds and tests the code, so broken code is caught within minutes.
- **CD (Continuous Delivery/Deployment):** every passing build is automatically packaged and deployed (to staging, and to production after approval).

**Analogy:** a factory assembly line with quality checks. Each part is inspected automatically at every station, and only products that pass every check are shipped.

**Typical pipeline:** checkout → build → unit tests → integration tests → code quality/security scan → build the Docker image → push to the registry → deploy to staging → (approval) → deploy to production.

```yaml
# .github/workflows/ci.yml
name: CI/CD

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "21"
          cache: maven

      - name: Build and test (unit + Testcontainers integration)
        run: mvn -B verify
        working-directory: backend

      - name: Frontend lint, test and build
        working-directory: frontend
        run: |
          npm ci
          npm run lint
          npm test -- --run
          npm run build

  docker:
    needs: build-test                          # runs only if the tests passed
    if: github.ref == 'refs/heads/main'        # only on main, not on PRs
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v6
        with:
          context: ./backend
          push: true
          tags: ghcr.io/${{ github.repository }}/loan-api:${{ github.sha }}

  deploy:
    needs: docker
    runs-on: ubuntu-latest
    environment: production                    # can require a manual approval in GitHub settings
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Kubernetes
        env:
          KUBECONFIG_DATA: ${{ secrets.KUBECONFIG }}
        run: |
          echo "$KUBECONFIG_DATA" > kubeconfig
          kubectl --kubeconfig kubeconfig -n loans set image deploy/loan-api \
            loan-api=ghcr.io/${{ github.repository }}/loan-api:${{ github.sha }}
          kubectl --kubeconfig kubeconfig -n loans rollout status deploy/loan-api --timeout=180s
```

**Line by line**
- `on: push / pull_request`: when the pipeline runs. PRs get build + test only, and `main` also gets deployed.
- `jobs`: run in parallel by default. `needs:` creates an order (test → docker → deploy).
- `runs-on: ubuntu-latest`: a fresh virtual machine for each run (Docker is available, so **Testcontainers works**).
- `actions/checkout`: downloads your repository's code.
- `setup-java ... cache: maven`: installs the JDK and caches `~/.m2` for faster builds.
- `mvn -B verify`: batch mode. Compiles, runs unit tests (Surefire) and integration tests (Failsafe).
- `npm ci`, `lint`, `test`, `build`: the frontend quality gate.
- `if: github.ref == 'refs/heads/main'`: only publish images from main.
- `secrets.GITHUB_TOKEN`: an automatic token that can push to GitHub's container registry. **Secrets are never written in YAML.** Store them in the repo settings.
- Tag = `github.sha` (the commit hash): every image maps to an exact commit, so rollback is just redeploying an older tag.
- `environment: production`: GitHub can require a reviewer to approve before this job runs.
- `kubectl set image` + `rollout status`: triggers the rolling update and **fails the pipeline** if the new pods don't become ready, at which point you can `rollout undo`.

**GitOps alternative:** CI only updates the image tag in a Git repo of manifests, and **Argo CD / Flux** syncs the cluster to match Git.

**Deployment strategies:** rolling (the default), **blue-green** (two full environments, switch traffic at once, instant rollback), **canary** (send 5% of traffic to the new version first, then increase).

@@ 2 | TOPIC | Phase 10 — Interview Questions | Quick Q&A revision for DevOps | SHOULD
# Phase 10 interview questions

**Q. merge vs rebase?**
A. Merge combines histories with a merge commit and keeps the true history. Rebase replays commits on a new base, giving a linear history, but it rewrites commits, so never rebase shared branches.

**Q. git revert vs git reset?**
A. Revert creates a new commit that undoes an old one (safe for shared branches). Reset moves the branch pointer and can discard commits (rewrites history).

**Q. Image vs container?**
A. An image is a read-only template. A container is a running instance of an image.

**Q. Container vs virtual machine?**
A. Containers share the host kernel and isolate processes (light, fast). VMs run a full guest OS on a hypervisor (heavier, stronger isolation).

**Q. Why use multi-stage Docker builds?**
A. To build with heavy tools in one stage and copy only the artefact into a small runtime image: smaller, faster and more secure.

**Q. How do containers keep data?**
A. Through volumes (or bind mounts). The container filesystem is ephemeral.

**Q. Pod vs Deployment vs Service?**
A. A pod runs containers. A Deployment manages replicated pods and rolling updates. A Service gives the pods a stable name/IP and load balances across them.

**Q. Liveness vs readiness probe?**
A. Readiness decides whether the pod receives traffic. Liveness decides whether the container should be restarted.

**Q. ConfigMap vs Secret?**
A. A ConfigMap holds non-sensitive config. A Secret holds sensitive data (base64-encoded, and it should be encrypted at rest and access-controlled).

**Q. How does Kubernetes do zero-downtime deployments?**
A. Rolling updates with readiness probes: new pods must be ready before old ones are removed (maxUnavailable 0).

**Q. What is CI/CD?**
A. CI automatically builds and tests every change. CD automatically packages and deploys passing builds to environments, often with approval gates for production.

**Q. How do you roll back a bad deployment?**
A. `kubectl rollout undo`, or redeploy the previous image tag (images tagged by commit SHA). Blue-green deployments allow an instant switch back.

**Q. How do you handle secrets in CI/CD?**
A. Store them in the CI secret store / a vault, inject them at runtime as environment variables, never commit them, and rotate them regularly.
