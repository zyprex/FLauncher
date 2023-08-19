# FLauncher

WARN: It's not a tranditional android launcher, read this doc from A to Z !

Features:
- Launch apps use tap, press, swipe and fling actions.
- Launch apps without looking at the screen.
- A decent app list.
- Pure text configuration.
- Support app shortcuts (Android Version >= 7.1).
- Simple but efficient enough.
- Use any png image as icon (AppList interface only)
- Rename app name (AppList interface only)
- Work on Android 5.0 above.

# Prompt Bar

Prompt bar is under screen bottom.

Switch between any interface by click or long press prompt bar.

Tap: `->`

Long Press: `=>`

```
+-------------+   +---------------+
|             |<--|               |
| Panel       |-->| AppList       |
+-------------+   +---------------+
  ||  ^                 ||  ^
  \/  |                 \/  |
+-------------+   +---------------+
|             |<==|               |
| PanelConfig |==>| AppListConfig |
+-------------+   +---------------+
```

# Use Panel

0 = Long Press

5 = Single Tap

4 = Swipe Left

3 = Swipe Bottom Right

and so on.

```
7 8 9
4 5 6
1 2 3
  0
```

## Recommand Logic Order

```
7 Work    8 Net      9 Game
4 finder  5 Urgent   6 Docs
1 System  2 Note     3 Life
          0 reserved
```

# Panel Configure Specification

1. This app's configurations are self documented.
2. Correct configured line must end with `\n`.

## Open Apps

Example: `app#88#com.zyprex.flauncher`, launch this app by swipe up and up.

Tips: you can click prompt bar to run app instantly.

## About Dial or Call

**WARN**: Call can direct call phone number while dial not.

# App List

Sort by long press and drag, delete by swipe left or right.

Click app icon to launch app detail.
On Android 7.1 or above will list available shortcuts.

# App List Configuration

Long press and you can see the menu. Swipe left or right add app to AppList.

# Thanks

The ideas I build this app are heavily inspired by those launchers:

- [kiss launcher](https://github.com/Neamar/KISS): Search focused app launcher.
- [Pie launcher](https://github.com/markusfisch/PieLauncher): Launcher with a dynamic pie menu.
- [Niagara Launcher](https://github.com/8bitPit/Niagara-Issues): One-hand optimized launcher (Not FOSS).

And many other's work lifted me up, help me out:

- [shortcuts](https://github.com/nongdenchet/Shortcuts): Sample app tutorial for querying app shortcuts.
- [lockoo](https://github.com/ChenCoin/Lockoo): lock the screen

