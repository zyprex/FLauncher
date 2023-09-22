package com.zyprex.flauncher.UTIL

import java.lang.StringBuilder

object DocStr {
    fun get(): String = """OPCODE RECIPE DENOTATIONS:

➢ app#*#<package_name>
➢ appinfo#*#<package_name>
➢ dial#*#<phone_number>
➢ call#*#<phone_number>
➢ sms#*#<phone_number>
➢ mail#*#<email_address>
➢ geo#*#<location> (lat,lng)
➢ url#*#<URL>
➢ query#*#<URL_%s> (customized search engine)
➢ sys#*
   ..#accessibility
   ..#apn
   ..#appmgr
   ..#battery
   ..#bluetooth
   ..#data
   ..#data_usage
   ..#date
   ..#dev
   ..#device_info
   ..#display
   ..#home
   ..#ime
   ..#locale
   ..#nfc
   ..#quick_launch
   ..#security
   ..#sound
   ..#storage
   ..#user_dict
   ..#voice_input
   ..#wifi
➢ cmd#*
   ..#torch (toggle flashlight)
   ..#expand_statusbar
   ..#wallpaper
   ..#ringer_mode_[norm/silent/vibrate]
       (e.g. ringer_mode_norm will set phone to ringing mode)
   ..#recents
➢ access#* (use accessibility service)
   ..#notifications
   ..#quick_settings
   ..#lock
   ..#power
   ..#recents
   ..#back
   ..#home
➢ menu#*#**,<name1>;**,<name2>;...

OPCODE DENOTATIONS:

Gestures Type:

➢ * (numbered opcode, e.g. your gesture sequences)

➢ ** (any opcode, e.g. your defined gesture sequences)

Event Type: 

➢ USB_[IN/OUT] (connect/disconnect to power)
➢ BAT_[LOW/OK] (battery is low or full)
➢ HEADSET_[IN/OUT] (plug status for headset)
➢ BL_HEADSET_[IN/OUT] (plug status for bluetooth headset)
➢ DOCK_[CAR/DESK/LE_DESK/HE_DESK] (dock state)
➢ AIRPLANE_MODE_[ON/OFF] (airplane mode)
➢ NETWORK_[ON/OFF] (network status)

Multiple Finger Touches Type:

➢ D duet (2)
➢ T trio (3)
➢ Q quartet (4)
➢ P pentagon (5)
➢ H hexagon (6)
➢ S septilateral (7)
➢ O octagon (8)
➢ N nonagon (9)
➢ F full fingers (10)

Theoretically, Android system can handle 256 fingers touch; actually, it's depends on device hardware support.

"""
}
