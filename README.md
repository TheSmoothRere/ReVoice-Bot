# RéVoice Bot

**RéVoice Bot** automatically creates temporary voice channels for users, so server admins never have to worry about running out of voice channels.

It works by cloning a designated **trigger (parent) channel** whenever someone joins it. The newly created channel inherits all the settings and permissions from the trigger channel, so there’s no need to manually configure each generated channel.

## Features

### Main Features

1. Generate new voice channel by cloning **trigger (parent) channel** so you don't have to setting the generated channel one by one.
2. You can use **prefix** to decide what the generated channel will be called.

### Command Feature

| Parameter Name | Required |
| -------------- | -------- |
| `name`         | required |
| `prefix`       | optional |
| `category`     | optional |
- `/create new [name] [prefix] [category]` create new parent channel

| Parameter Name | Required |
| -------------- | -------- |
| `channel`      | required |
| `prefix`       | optional |
- `/create exist [channel] [prefix]` create new parent channel based on existing voice channel

| Parameter Name | Required |
| -------------- | -------- |
| `channel`      | required |
| `prefix`       | required |
- `/update prefix [channel] [prefix]` update parent channel's prefix

## Installation Guide

### Using Docker Compose (Recommended)

1. **Install Docker**
   Make sure you have Docker installed on your machine. For installation instructions, please refer to [Docker Get Started](https://www.docker.com/get-started/).

2. **Clone the Repository**
   ```bash
   git clone https://github.com/TheSmoothRere/ReVoice-Bot.git
   cd ReVoice-Bot
   ```

3. **Configure Environment Variables**
   Create a `.env` file in the root directory of the project and add the following configuration:
   ```env
   # Discord Bot Token
   DISCORD_BOT_TOKEN=your_discord_bot_token_here

   # Database Configuration
   # The hostname 'postgres' matches the service name in docker-compose.yml
   DB_URL=postgres:5432/revoice
   DB_USER=revoice_user
   DB_PASSWORD=secure_password

   # Redis Configuration
   # The hostname 'redis' matches the service name in docker-compose.yml
   REDIS_URL=redis
   REDIS_PORT=6379
   REDIS_USER=
   REDIS_PASSWORD=
   ```

4. **Run the Application**
   Build and start the containers using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```

   The bot should now be running. You can view the logs using:
   ```bash
   docker-compose logs -f revoice-bot
   ```

### Using Docker (Standalone)

If you already have PostgreSQL and Redis running elsewhere, you can run the bot container directly.

1. **Build the Docker Image**
   ```bash
   docker build -t revoice-bot .
   ```

2. **Run the Container**
   Use the `-e` flag to pass your environment variables. Replace the values with your actual configuration.

   ```bash
   docker run -d --name revoice-bot \
     -e DISCORD_BOT_TOKEN="your_discord_bot_token_here" \
     -e DB_URL="your_postgres_host:5432/revoice" \
     -e DB_USER="your_db_user" \
     -e DB_PASSWORD="your_db_password" \
     -e REDIS_URL="your_redis_host" \
     -e REDIS_PORT="6379" \
     -e REDIS_USER="" \
     -e REDIS_PASSWORD="" \
     revoice-bot
   ```

## ⚖️ Legal & Licensing

### License

This project is licensed under the **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)**.

> [\!IMPORTANT]
> This means you are free to share and adapt the material, provided you give appropriate credit and do **not** use the material for commercial purposes.
> [Read the full license here](https://creativecommons.org/licenses/by-nc/4.0/).

### Terms of Service & Privacy

To ensure transparency for your server members and compliance with Discord's Developer Terms, please refer to our official documentation:

  * TERMS_OF_SERVICE.md – Guidelines on how to use the bot responsibly.
  * PRIVACY_POLICY.md – Information on what data we collect (e.g., User IDs for channel ownership) and how it is handled.

