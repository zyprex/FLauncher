package com.zyprex.flauncher

data class AppArchive(val label: String, val pkgName: String) {
    override fun toString(): String {
        return "${this.pkgName}#${this.label}\n"
    }
}
