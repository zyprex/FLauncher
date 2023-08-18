package com.zyprex.flauncher.DT

data class AppArchive(var label: String, val pkgName: String) {
    override fun toString(): String = "${this.pkgName}#${this.label}\n"
}
