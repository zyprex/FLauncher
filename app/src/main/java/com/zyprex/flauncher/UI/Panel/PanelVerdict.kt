package com.zyprex.flauncher.UI.Panel

import android.content.Context
import android.os.Build
import com.zyprex.flauncher.DT.ActionInfo
import com.zyprex.flauncher.DT.AppIndex
import com.zyprex.flauncher.DT.AppRecents
import com.zyprex.flauncher.EXT.MyAccessibilityService
import com.zyprex.flauncher.EXT.ScrLock
import com.zyprex.flauncher.UTIL.Starter
import com.zyprex.flauncher.UTIL.launchApp
import com.zyprex.flauncher.UTIL.launchAppDetail
import com.zyprex.flauncher.UTIL.readFile


class PanelVerdict(val context: Context) {

    companion object {
        val type = arrayOf(
            "app",
            "appinfo",
            "dial",
            "sms",
            "mail",
            "geo",
            "url",
            "query",
        )
        val  typeInfo = arrayOf(
            "need app package name",
            "need app package name",
            "need phone number",
            "need phone number",
            "need e-mail address: xxx@email.com",
            "need latitude and longitude: -11.11,22.34",
            "need web link: http://....",
            "need search engine format: http://search/q?=%s",
        )
        val actionInfoList = mutableListOf<ActionInfo>()
    }

    init {
        updateActionInfoList()
    }

    private val starter = Starter(context)

    fun updateActionInfoList() {
        actionInfoList.clear()
        actionInfoList.addAll(parseActionInfoList())
    }

    fun parseActionInfoList(): MutableList<ActionInfo> {
        var cfg: String = readFile(context, AppIndex.panelFileName)
        val actionInfos = mutableListOf<ActionInfo>()
        val results = Regex("^.+#.+#.*\n", RegexOption.MULTILINE)
            .findAll(cfg)
        results.forEach {
            val line = it.value.replace("\n", "")
            val sects = line.split("#".toRegex(), 3)
            val actionInfo = ActionInfo(
                sects[0],
                sects[1],
                sects[2],
            )
            actionInfos.add(actionInfo)
        }
        return actionInfos
    }

    fun actionStart(opCode: String): Boolean {
        var flag = false
        for (info in actionInfoList) {
            if (info.opCode == opCode) {
                run(info.type, info.cmd)
                flag = true
            }
        }
        return flag
    }

    fun runNumStrOpCode(opCode: String): Boolean {
        if (Regex("\\d+").matches(opCode)) {
            actionStart(opCode)
            return true
        }
        return false
    }

    private fun run(type: String, param: String) {
        when(type) {
            "app" -> launchApp(context, param)
            "appinfo" -> launchAppDetail(context, param)
            "dial" -> starter.dialPhoneNum(param)
            "call" -> starter.callPhoneNum(param)
            "sms" -> starter.sendSMS(param)
            "mail" -> starter.sendMail(param)
            "geo" -> starter.showMap(param)
            "url" -> starter.openWebPage(param)
            "query" -> starter.searchWeb(param)
            "sys" -> starter.openSystemSettings(param)
            "cmd"  -> {
                when (param) {
                    "torch" -> starter.toggleTorch()
                    "expand_statusbar" -> starter.expandStatusBar()
                    "wallpaper" -> starter.setWallpaper()
                    "ringer_mode_norm" -> starter.setRingerMode("norm")
                    "ringer_mode_silent" -> starter.setRingerMode("silent")
                    "ringer_mode_vibrate" -> starter.setRingerMode("vibrate")
                    "recents" -> AppRecents(context).launcher()
                }
            }
            "access" -> {
                if (param == "lock" && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    ScrLock(context).lock()
                    return
                }
                MyAccessibilityService.useAction(param)
            }
            "shortcut" -> starter.shortcutsOpen(param)
            "menu" -> starter.openMenu(param)
        }
    }
}
