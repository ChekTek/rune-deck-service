# Rune Deck WebSocket Schema

This document defines the client ↔ server JSON messages for plugin management.

## Message format

All messages are JSON objects with a `messageType` field.

Server plugin responses use a `type` field.

---

## 1) Get plugins

### Client request

```json
{
  "messageType": "getPlugins"
}
```

### Server broadcast response

```json
{
  "type": "PLUGINS",
  "plugins": [
    {
      "id": "net.runelite.client.plugins.agility.AgilityPlugin",
      "name": "Agility",
      "isActive": false
    },
    {
      "id": "com.chektek.RuneDeckPlugin",
      "name": "Rune Deck",
      "isActive": true
    }
  ]
}
```

Notes:

- `id` is the unique plugin identifier used for later toggle requests.
- `name` is a user-friendly label.
- `isActive` is the current runtime state (`true` = active, `false` = inactive).

---

## 2) Toggle plugin

### Client request (toggle)

```json
{
  "messageType": "togglePlugin",
  "pluginId": "net.runelite.client.plugins.agility.AgilityPlugin",
  "isActive": true
}
```

- Set `isActive` to `true` to enable/start a plugin.
- Set `isActive` to `false` to stop/disable a plugin.

### Server broadcast after toggle

After processing `togglePlugin`, RuneLite emits a plugin state change and the server broadcasts only the changed plugin.

### Server broadcast on manual/plugin-panel changes

When a plugin is enabled/disabled through RuneLite (for example via the plugin panel), the server broadcasts:

```json
{
  "type": "PLUGIN_CHANGED",
  "plugin": {
    "id": "net.runelite.client.plugins.agility.AgilityPlugin",
    "name": "Agility",
    "isActive": true
  }
}
```

---

## Existing utility message

### Clear cache request

```json
{
  "messageType": "clearCache"
}
```
