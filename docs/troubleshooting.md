# Troubleshooting

[Setup Guide](DOCS.md)

Goal: fixes for the most common failures.

## Bot runs an old version after code changes

> [!TIP]
> If you notice the bot is running an old version even after code changes, use the `--build` flag or manually remove the old image with `podman image rm apolo-music-bot:latest` before running `up`. We've configured an explicit image name in `docker-compose.yml` to ensure consistency.

## Voice connection fails

Discord requires DAVE (end-to-end encryption) since March 2026. This project pins JDA 6.5.0 with JDave 0.1.8. Do not downgrade below JDA 6.3.2 or voice will fail.

## `/queue` output gets cut off

Discord caps messages at 2000 characters. Long track titles are truncated when building the queue list.

## Container cannot pull base images

> [!NOTE]
> Image references use full registry paths to ensure compatibility with [Podman](https://podman.io/) and environments where `unqualified-search-registries` are not configured (such as [Debian](https://wiki.debian.org/Podman))
