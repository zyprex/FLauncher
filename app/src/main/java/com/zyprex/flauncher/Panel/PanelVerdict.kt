package com.zyprex.flauncher.Panel

import android.content.Context
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.Starter
import com.zyprex.flauncher.launchApp
import com.zyprex.flauncher.launchAppDetail
import com.zyprex.flauncher.readFile


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
    }

    private var cfg: String = readFile(context, AppIndex.panelFileName)
    private val starter = Starter(context)

    fun actionStart(actionCode: String): Boolean {
        //Log.d("PanelVerdict", actionCode)
        var flag = false
        val results = Regex("^.+#${actionCode}#.*\n", RegexOption.MULTILINE)
            .findAll(cfg)
        results.forEach {
            typeRun(it.value.replace("\n", ""), "#${actionCode}#".toRegex())
            flag = true
        }
        return flag
    }

    private fun typeRun(cfgLine: String, delim: Regex) {
        val arr = cfgLine.split(delim, 2)
        val type = arr[0]
        val param = arr[1]
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
                    "recents" -> starter.recentActivities()
                }
            }
        }
    }
}

