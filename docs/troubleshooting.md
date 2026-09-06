# Troubleshooting

[Index](index.md)

Goal: fixes for the most common failures.

## Bot runs an old version after code changes

Rebuild the image:

```bash
podman compose up --build -d
```

Or remove the stale image first:

```bash
podman image rm apolo-music-bot:latest
```

## Voice connection fails

Discord requires DAVE (end-to-end encryption) since March 2026. This project pins JDA 6.5.0 with JDave 0.1.8. Do not downgrade below JDA 6.3.2 or voice will fail.

## `/queue` output gets cut off

Discord caps messages at 2000 characters. Long track titles are truncated when building the queue list.

## Container cannot pull base images

`Dockerfile` uses full registry paths (e.g. `docker.io/eclipse-temurin:25-jdk`) for Podman and distros without `unqualified-search-registries` configured. No action needed unless you rewrite the image references.
