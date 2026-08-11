# CrateSystem

Кратовый плагин для Spigot/Paper 1.16.5: краты с анимированным GUI, взвешенные награды, ключи-предметы и интеграция с EconomyPlus.

## Возможности

- Анимированный GUI-розыгрыш с финальным показом награды
- Награды: предметы, деньги (через EconomyPlus), консольные команды
- Взвешенный рандом (`weight`) с фолбэком на первую награду
- Ключи — настраиваемые предметы (материал, имя, лор, свечение, чары)
- Крат-блоки: установка через команду, защита от разрушения и взрывов
- `/crate reload` — перезагрузка конфига без перезапуска сервера
- Сообщения и анимация полностью настраиваются в `crates.yml`

## Команды

| Команда | Описание | Право |
|---|---|---|
| `/crate givekey <player> <crate> [amount]` | Выдать ключи | `crystalox.crate.admin` |
| `/crate setblock <crate>` | Поставить крат на целевой блок | `crystalox.crate.admin` |
| `/crate removeblock` | Убрать крат с целевого блока | `crystalox.crate.admin` |
| `/crate list` | Список кратов | — |
| `/crate open <crate>` | Открыть краты (админ — бесплатно) | `crystalox.crate.use` |
| `/crate reload` | Перезагрузить конфиг | `crystalox.crate.admin` |

## Права

- `crystalox.crate.admin` — админ-команды (default: op)
- `crystalox.crate.use` — использование `/crate open` (default: true)

## Конфиг (`crates.yml`)

```yaml
settings:
  animation-ticks: 5      # скорость анимации
  animation-cycles: 18    # длительность
  messages: { ... }       # все сообщения плагина
crates:
  common:
    display-name: '&7Common Crate'
    key:                  # предмет-ключ
      material: TRIPWIRE_HOOK
      name: '&7Common Key'
      glow: true
    rewards:              # список наград
      - type: ITEM        # ITEM | MONEY | COMMAND
        weight: 40
        item:
          material: IRON_INGOT
          amount: 16
      - type: MONEY
        weight: 25
        amount: 100
      - type: COMMAND
        weight: 9
        command: 'give %player% diamond 1'
```

Плейсхолдеры в командах наград: `%player%` подставляется при выдаче.

## Зависимости

- Paper/Spigot 1.16.5+
- `EconomyPlus` (soft-depend) — для денежных наград

## Сборка

```bash
gradle build
# результат: build/libs/CrateSystem-1.0.0.jar
```
