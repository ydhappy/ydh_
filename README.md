# 싸울아비 (Ssaulabirun) MMORPG

A Java 17 MMORPG project built with Netty (server) and Swing (client).

## Modules
- **server** – Netty-based game server with H2 embedded database
- **client** – Swing-based game client with dark theme

## Build
```bash
mvn package -f pom.xml
```

## Run Server
```bash
java -jar server/target/ssaulabirun-server.jar
```

## Run Client
```bash
java -jar client/target/ssaulabirun-client.jar
```

## Features
- 18 character classes including the special 싸울아비 class
- Netty LengthFieldBasedFrameDecoder packet protocol
- H2 embedded database with auto-schema init
- ResourceGenerator: 5 maps (200×200), 50 NPC types, 100 items
- l1j-style server architecture
- Dark-themed Swing client with game canvas, minimap, chat
