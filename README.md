# RuneFit RuneLite Plugin

The RuneFit plugin creates short-lived, single-use codes that link the OSRS character currently logged into RuneLite with a [RuneFit](https://github.com/sergeibeidel/runefit) account.

## Linking A Character

1. Enable the RuneFit plugin in RuneLite.
2. Log into the OSRS character you want to link.
3. Open the RuneFit side panel and click **Generate Link Code**.
4. Copy the displayed code into RuneFit's OSRS Characters page within ten minutes.

Generating a code sends the current character name, RuneLite account hash, and account type to RuneFit. As with any direct HTTPS request, RuneFit's server can see the connection's source IP address. The plugin does not include the IP address in its request body, and the RuneFit application does not persist it. It is used only as a short-lived, hashed rate-limit key. Hosting infrastructure may retain normal access logs according to its configuration.

The plugin sends data only after you click the button. It does not store RuneFit credentials or a long-lived token.

RuneLite linking is practical evidence that someone accessed a character. It is not cryptographic proof of account ownership because RuneLite and Plugin Hub plugins are open source and client requests can be reproduced.

## Installing Before Plugin Hub Publication

RuneLite does not support installing an unpublished plugin into the normal release client. Until RuneFit is accepted into the Plugin Hub, testers must run a separate RuneLite development client from this repository.

Requirements:

- Git
- Java 11 or newer

Windows PowerShell:

```powershell
git clone https://github.com/sergeibeidel/runefit-runelite-plugin.git
cd runefit-runelite-plugin
.\gradlew.bat run
```

macOS or Linux:

```bash
git clone https://github.com/sergeibeidel/runefit-runelite-plugin.git
cd runefit-runelite-plugin
./gradlew run
```

The command launches a separate development-mode RuneLite client with RuneFit already loaded. Do not download or copy unofficial plugin JAR files into RuneLite.

If using a Jagex Account, follow RuneLite's [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts) development-client instructions.

The plugin currently defaults to a local RuneFit API. Testers must run RuneFit locally, or configure the plugin with the finalized production HTTPS API endpoint once it is available.

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

When you explicitly generate a link code, this plugin sends your current character name, account hash, and account type to RuneFit. RuneFit's server also sees your source IP as normal network metadata, but the plugin does not include it in the request body.
