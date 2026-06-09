# RuneFit RuneLite Plugin

The RuneFit plugin creates short-lived, single-use codes that link the OSRS character currently logged into RuneLite with a [RuneFit](https://github.com/sergeibeidel/runefit) account.

## Linking A Character

1. Enable the RuneFit plugin in RuneLite.
2. Log into the OSRS character you want to link.
3. Open the RuneFit side panel and click **Generate Link Code**.
4. Copy the displayed code into RuneFit's OSRS Characters page within ten minutes.

Generating a code sends the current character name, RuneLite account hash, account type, and your IP address to RuneFit. The plugin sends data only after you click the button. It does not store RuneFit credentials or a long-lived token.

RuneLite linking is practical evidence that someone accessed a character. It is not cryptographic proof of account ownership because RuneLite and Plugin Hub plugins are open source and client requests can be reproduced.

## Development

Requirements:

- Java 11 or newer
- A local RuneFit server for end-to-end testing

Run tests:

```bash
./gradlew test
```

Run the RuneLite development client:

```bash
./gradlew run
```

For local RuneFit development, set the plugin's API endpoint to:

```text
http://127.0.0.1:8000/api/runelite/link-codes
```

Non-local endpoints must use HTTPS.

The plugin defaults to the local endpoint. Set and review the production HTTPS endpoint before publishing the plugin through the RuneLite Plugin Hub.

## Plugin Hub Disclosure

This plugin sends your current character name, account hash, and IP address to RuneFit when you explicitly generate a link code.
