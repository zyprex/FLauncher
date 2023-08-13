package com.zyprex.flauncher.UTIL

import java.lang.StringBuilder

object DocStr {
    private var sb = StringBuilder()

    fun typeAndParam(type: String, param: String) {
        sb.append("$type#*#${param}\n")
    }

    fun typeAndParam(type: String, param: String, info: String) {
        sb.append("$type#*#${param} (${info})\n")
    }

    fun type(s: String) {
        sb.append("$s#*...\n")
    }

    fun param(s: String) {
        sb.append("\t...#$s\n")
    }

    fun param(s: String, info: String) {
        sb.append("\t...#$s (${info})\n")
    }

    fun get(): String {
        if (sb.toString().isEmpty()) {
            typeAndParam("app", "<package_name>")
            typeAndParam("appinfo", "<package_name>")
            typeAndParam("dial", "<phone_number>")
            typeAndParam("call", "<phone_number>")
            typeAndParam("sms", "<phone_number>")
            typeAndParam("mail", "<email_address>")
            typeAndParam("geo", "<geometer>", "lat,lng")
            typeAndParam("url", "<URL>")
            typeAndParam("query", "<URL_%s>", "customized search engine")
            type("sys")
            param("accessibility")
            param("apn")
            param("appmgr")
            param("bluetooth")
            param("data")
            param("data_usage")
            param("date")
            param("dev")
            param("device_info")
            param("display")
            param("home")
            param("ime")
            param("locale")
            param("nfc")
            param("quick_launch")
            param("security")
            param("sound")
            param("storage")
            param("user_dict")
            param("voice_input")
            param("wifi")
            type("cmd")
            param("torch", "toggle flashlight")
            param("expand_statusbar")
            param("wallpaper")
            param("ringer_mode_[norm/silent/vibrate]"
                , "e.g. ringer_mode_norm will set phone to ringing mode")
            param("recents")
            type("access")
            param("notifications")
            param("quick_settings")
            param("lock")
            param("power")
            sb.append("""
                ACTION CODE DENOTATIONS:
                  *  (numbered action code, e.g. your gesture sequences)
                  USB_[IN/OUT] (connect/disconnect to power)
                  BAT_[LOW/OK] (battery is low or full)
                  HEADSET_[IN/OUT] (plug status for headset)
                  BL_HEADSET_[IN/OUT] (plug status for bluetooth headset)
                  DOCK_[CAR/DESK/LE_DESK/HE_DESK] (dock state)
            """.trimIndent())
        }
        return sb.toString()
    }

}