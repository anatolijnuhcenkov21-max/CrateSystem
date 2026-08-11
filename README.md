# CrateSystem

A crate and key-opening system for Paper 1.16.5+. CrateSystem lets server owners define fully configurable crates with weighted rewards, place them as physical blocks in the world, and give players glowing keys that unlock an animated opening ceremony.

---

## Introduction

CrateSystem turns any block into a loot crate. Crates are defined in `crates.yml`, each with a display name, a key item, and a list of weighted rewards. Players right-click a crate block while holding the matching key — or run `/crate open` — to consume the key and trigger an animated GUI that reveals the winning reward. Reward types cover items, console commands, and money, with money payouts wired through EconomyPlus when it is installed.

## Features

- **Config-defined crates** — create any number of crates entirely in `crates.yml`, no code changes required
- **Weighted rewards** — each reward carries a weight; the roller picks proportionally, so rare jackpot items are naturally rarer
- **Reward types** — `ITEM` (with name, amount, and enchantments), `COMMAND` (console command with `%player%`), and `MONEY` (EconomyPlus default-currency payout)
- **Animated opening GUI** — a fast cycling preview animation runs for a configurable number of ticks and cycles before settling on the winner
- **Physical crate blocks** — assign a crate to any targeted block; right-clicking it opens the crate, with placements persisted in `crates-data.yml`
- **Keys with glow** — key items support custom names, lore, and the enchanted glow effect
- **Explosion protection** — registered crate blocks are removed from block and entity explosion damage
- **EconomyPlus integration** — money rewards deposit into the player's default-currency balance; skipped gracefully with a warning when EconomyPlus is absent

## Commands

All commands use the `crate` root: `/crate <subcommand>`.

| Command | Description | Permission |
|---|---|---|
| `/crate givekey <player> <crate> [amount]` | Give a player crate keys (default amount 1) | `crystalox.crate.admin` |
| `/crate setblock <crate>` | Turn the block you are looking at into a crate block | `crystalox.crate.admin` |
| `/crate removeblock` | Remove the crate binding from the block you are looking at | `crystalox.crate.admin` |
| `/crate list` | List all defined crates with their reward counts | `crystalox.crate.admin` |
| `/crate open <crate>` | Open a crate GUI; consumes a key from your main hand unless you are admin | `crystalox.crate.use` |
| `/crate reload` | Reload crates and messages from disk | `crystalox.crate.admin` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `crystalox.crate.admin` | Full access to all admin subcommands and keyless opening | op |
| `crystalox.crate.use` | Open crates (requires a matching key for non-admins) | true |

## Configuration

Crates are defined in `crates.yml`, not `config.yml`. This is one complete crate with its rewards:

```yaml
crates:
  common:
    display-name: '&7Common Crate'
    key:
      material: TRIPWIRE_HOOK
      name: '&7Common Key'
      lore:
        - '&7Right-click a Common Crate to open it.'
      glow: true
    rewards:
      - type: ITEM
        weight: 40
        item:
          material: IRON_INGOT
          amount: 16
          name: '&7Iron Ingots'
      - type: ITEM
        weight: 25
        item:
          material: DIAMOND
          amount: 1
          name: '&bDiamond'
      - type: MONEY
        weight: 25
        amount: 100
      - type: COMMAND
        weight: 9
        command: 'give %player% diamond 1'
      - type: ITEM
        weight: 1
        item:
          material: NETHERITE_INGOT
          amount: 1
          name: '&6&lJACKPOT!'
```

The `settings` section at the top of the file tunes the opening animation (`animation-ticks`, `animation-cycles`) and all user-facing messages. Reward weights are relative — a weight of 40 is twice as likely as a weight of 20. Enchantments on item rewards use the same `enchants:` map format as the key and item definitions.

## Build

Requirements: JDK 8+ and a Gradle wrapper.

```bash
./gradlew build
```

The jar is written to `build/libs/CrateSystem-1.0.0.jar`. Drop it into `plugins/` and restart your Paper 1.16.5+ server. EconomyPlus is optional; without it, `MONEY` rewards are skipped and a warning is logged.

## License

MIT License — Copyright (c) 2026 CrystalOx Portfolio. See [LICENSE](LICENSE).
