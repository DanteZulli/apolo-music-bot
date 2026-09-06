# 3. Run

[Index](DOCS.md) | Prev: [Environment](02-environment.md) | Next: [Commands](04-commands.md)

Goal: start the bot. Pick one option.

Prerequisites: phases 1-2 done (bot invited, token configured).

## Option A: Local (Gradle)

Requires Java 25.

```bash
./gradlew build
./gradlew bootRun
```

Useful variants:

```bash
./gradlew test   # run tests
./gradlew clean  # clean build
```

## Option B: Container (Docker / Podman)

```bash
# Build and start in background
docker compose up --build -d

# Using Podman
podman compose up --build -d
```

Image references use full registry paths so they work on [Podman](https://podman.io/) and environments without `unqualified-search-registries` (e.g. [Debian](https://wiki.debian.org/Podman)).

Stuck? See [Troubleshooting](06-troubleshooting.md).
