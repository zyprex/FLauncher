# FLauncher

CAUTION: It's an experimental android launcher, read this doc from A to Z before start!

Features:
- Do actions by tap, press, fling, multiple touches or system event changed.
- Do actions without looking at the screen.
- A decent app list.
- Pure text configuration.
- Support app shortcuts (Android Version >= 7.1).
- Simple but efficient enough.
- Use any png image as icon (AppList interface only)
- Rename app label (AppList interface only)
- Work on Android 5.0 above.

Defects:
- No plan to support widgets.
- No plan to support app icon pack.

The shortcuts are not installed as tranditional way:

1. On Android Version Lower than 8.0, it's only add to PanelConfig as String.
2. On Android Verision 8.0 or above, it's also add shortcut on AppList context menu as pinned shortcuts.

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
  (call can call phone number directly while dial not.)

Any action are 1s delay to launch, but you can click prompt bar to run it instantly.

The `menu` type let you create a group launchable items show in a dialog list,
for example:

```
app#xyz#com.android.settings
app#uvw#com.android.mediacenter
menu#00#xyz,Settings;uvw,Media Center;

```

# App List

Sort by long press and drag, delete by swipe left or right.

Click app icon to show the context menu
(on Android 7.1 or above will list available shortcuts).

**Click top item easily**:

Use menu option `Edit App List`, add lines at the beginning of texts.

```
/#
/#
/#
```

Those lines cannot be executed so it's suitable as empty placeholder.

**Use opcode as app list item**:

Use menu option `Edit App List`, add lines at the beginning of texts.

```
00#Do zero zero
```

When you click this item, the effects are equal to Long press twice on the panel.

Only the number opcodes will take effects.

# App List Configuration

Long press show the context menu. Swipe left or right add app to AppList.

# Download This App

Download released apk:
- [FLauncher](app/release)

# Credits

The ideas I build this app are heavily inspired by those launchers:

- [kiss launcher](https://github.com/Neamar/KISS):
  - Pros: Search focused app launcher.
  - Cons: Simple UI but complicated configurations.
- [Pie launcher](https://github.com/markusfisch/PieLauncher):
  - Pros: Launcher with a dynamic pie menu.
  - Cons: Lack of configurations.
- [Niagara Launcher](https://github.com/8bitPit/Niagara-Issues):
  - Pros: One-hand optimized launcher.
  - Cons: Not FOSS, app list too crowd.
- [Discreet Launcher](https://github.com/falzonv/discreet-launcher):
  - Pros: Access favor apps from notifications, clean home.
  - Cons: Grid app layout everywhere.

And many other's work lifted me up, help me out:

- [shortcuts](https://github.com/nongdenchet/Shortcuts): Sample app tutorial for querying app shortcuts.
- [lockoo](https://github.com/ChenCoin/Lockoo): lock the screen

