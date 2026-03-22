# RéVoice Bot

**RéVoice Bot** automatically creates temporary voice channels for users, so server admins never have to worry about running out of voice channels.

It works by cloning a designated **trigger (parent) channel** whenever someone joins it. The newly created channel inherits all the settings and permissions from the trigger channel, so there’s no need to manually configure each generated channel.

### Installation Guide

Using Docker-Compose (recomended)
1. Make sure you have Docker installed on your machine. For installation guide, please refer to this page [Docker Get Started](https://www.docker.com/get-started/)
