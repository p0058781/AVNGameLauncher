package org.skynetsoftware.avnlauncher.model

import org.skynetsoftware.avnlauncher.domain.model.Game

fun Game.isF95Game(): Boolean = f95ZoneThreadId > 0
