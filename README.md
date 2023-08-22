# FLauncher

WARN: It's not a tranditional android launcher, read this doc from A to Z !

Features:
- Do actions by fling, multiple touches or hardware connect event actions.
- Do actions without looking at the screen.
- A decent app list.
- Pure text configuration.
- Support app shortcuts (Android Version >= 7.1).
- Simple but efficient enough.
- Use any png image as icon (AppList interface only)
- Rename app label (AppList interface only)
- Work on Android 5.0 above.

# Prompt Bar

Prompt bar is always at screen bottom.

```
+-------------+
|             |
|             |
+-------------+
| prompt bar  |
+-------------+
```

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
7 Work    8 Browser  9 Game
4 Dict    5 Urgent   6 Files
1 System  2 Note     3 Life
          0 Reserved
```

# Panel Configure Specification

1. This app's configurations are self documented.
2. Correct configured line must end with `\n`.

Explain some use cases:

- open android settings by swipe left bottom is `app#1#com.android.settings`
- turn on/off flashlight by triple tap is `cmd#555#torch`
- switch to silent mode by put two finger on screen is `cmd#D#ringer_mode_silent`
- open music when headset connected is `app#HEADSET_IN#com.android.mediacenter`
- call phone number 1234567 by some swipes is `call#123#1234567`
  (call can direct call phone number speed your money while dial not.)

Any action are 1s delay to launch, but you can click prompt bar to run it instantly.

# App List

Sort by long press and drag, delete by swipe left or right.

Click app icon to show the context menu
(on Android 7.1 or above will list available shortcuts).

# App List Configuration

Long press show the context menu. Swipe left or right add app to AppList.

# Thanks

The ideas I build this app are heavily inspired by those launchers:

- [kiss launcher](https://github.com/Neamar/KISS): Search focused app launcher.
- [Pie launcher](https://github.com/markusfisch/PieLauncher): Launcher with a dynamic pie menu.
- [Niagara Launcher](https://github.com/8bitPit/Niagara-Issues): One-hand optimized launcher (Not FOSS).

And many other's work lifted me up, help me out:

- [shortcuts](https://github.com/nongdenchet/Shortcuts): Sample app tutorial for querying app shortcuts.
- [lockoo](https://github.com/ChenCoin/Lockoo): lock the screen

