package com.zyprex.flauncher.Panel

import android.content.Context
import com.zyprex.flauncher.AppIndex
import com.zyprex.flauncher.callPhoneNum
import com.zyprex.flauncher.launchApp
import com.zyprex.flauncher.launchAppDetail
import com.zyprex.flauncher.openWebPage
import com.zyprex.flauncher.dialPhoneNum
import com.zyprex.flauncher.openSystemSettings
import com.zyprex.flauncher.readFile
import com.zyprex.flauncher.searchWeb
import com.zyprex.flauncher.sendMail
import com.zyprex.flauncher.sendSMS
import com.zyprex.flauncher.showMap
import com.zyprex.flauncher.toggleTorch

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
            "dial" -> dialPhoneNum(context, param)
            "call" -> callPhoneNum(context, param)
            "sms" -> sendSMS(context, param)
            "mail" -> sendMail(context, param)
            "geo" -> showMap(context, param)
            "url" -> openWebPage(context, param)
            "query" -> searchWeb(context, param)
            "sys" -> openSystemSettings(context, param)
            "camera"  -> {
                if (param == "torch") {
                    toggleTorch(context)
                }
            }
        }
    }
}

