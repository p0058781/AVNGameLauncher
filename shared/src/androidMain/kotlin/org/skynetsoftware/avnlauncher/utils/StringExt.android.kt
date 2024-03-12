package org.skynetsoftware.avnlauncher.utils

actual fun String.format(vararg args: Any?) = String.format(this, *args)