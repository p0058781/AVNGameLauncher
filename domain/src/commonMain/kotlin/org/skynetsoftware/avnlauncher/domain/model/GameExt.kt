package org.skynetsoftware.avnlauncher.domain.model

fun Game.isF95Game(): Boolean = f95ZoneThreadId > 0

fun Game.imageUrlOrig(): String = "$imageUrl?orig"
