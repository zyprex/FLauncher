# FLauncher

Features:
- Launch apps use tap, press, swipe and fling actions.
- Launch apps without looking at the screen.
- A decent app list.
- Pure text configuration.
- Support app shortcuts (Android Version >= 7.1).
- Simple but efficient enough.
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

**NOTICE**: correct configured line must end with `\n`!

```
app#{ACTION_CODE}#{AppPackageName}
appinfo#{ACTION_CODE}#{AppPackageName}
dial#{ACTION_CODE}#{PhoneNumber}
call#{ACTION_CODE}#{PhoneNumber}
sms#{ACTION_CODE}#{PhoneNumer}
mail#{ACTION_CODE}#{Address}
geo#{ACTION_CODE}#{Lat,Lng}
url#{ACTION_CODE}#{URL}
query#{ACTION_CODE}#{URL_%s}
sys#{ACTION_CODE}#{settings_alias}
camera#{ACTION_CODE}#torch
statusbar#{ACTION_CODE}#expand
```

## Open Apps

Example: `app#88#com.zyprex.flauncher`, launch this app by swipe up and up.

Tips: you can click prompt bar to run app instantly.

## About Dial or Call

**WARN**: Call can direct call phone number while dial not.

## Settings Alias List

```
settings alias |  intent
"locale" -> Settings.ACTION_APP_LOCALE_SETTINGS
"accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
"deviceinfo" -> Settings.ACTION_DEVICE_INFO_SETTINGS
"display" -> Settings.ACTION_DISPLAY_SETTINGS
"security" -> Settings.ACTION_SECURITY_SETTINGS
"wifi" -> Settings.ACTION_WIFI_SETTINGS
"bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
"date" -> Settings.ACTION_DATE_SETTINGS
"voiceinput" -> Settings.ACTION_VOICE_INPUT_SETTINGS
"ime" -> Settings.ACTION_INPUT_METHOD_SETTINGS
"usrdict" -> Settings.ACTION_USER_DICTIONARY_SETTINGS
"dev" -> Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
"storage" -> Settings.ACTION_INTERNAL_STORAGE_SETTINGS
"home" -> Settings.ACTION_HOME_SETTINGS
"appmgr" -> Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
"nfc" -> Settings.ACTION_NFC_SETTINGS
"quicklaunch" -> Settings.ACTION_QUICK_LAUNCH_SETTINGS
```

## Special Item Configure Samples

Example for toggle camera's flash: `camera#555#torch`.

Example for expand status bar: `statusbar#555#expand`

# Use App List

Sort by long press and drag, delete by swipe left or right.

Click app icon to launch app detail.
On Android 7.1 or above will list available shortcuts.

# App List Configuration

Long press and you can see the menu.

# Thanks

The ideas I build this app are heavily inspired by those launchers:

- [kiss launcher](https://github.com/Neamar/KISS): Search focused app launcher.
- [Pie launcher](https://github.com/markusfisch/PieLauncher): Launcher with a dynamic pie menu.
- [Niagara Launcher](https://github.com/8bitPit/Niagara-Issues): One-hand optimized launcher.

And many other's work lifted me up, help me out:

- [shortcuts](https://github.com/nongdenchet/Shortcuts): Sample app tutorial for querying app shortcuts.

