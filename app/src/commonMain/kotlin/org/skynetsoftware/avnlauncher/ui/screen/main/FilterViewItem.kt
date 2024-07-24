package org.skynetsoftware.avnlauncher.ui.screen.main

import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.model.StringValue

sealed class FilterViewItem(
    val label: StringValue,
) {
    class FilterItem(val filter: Filter, label: StringValue) : FilterViewItem(label)

    class FilterGroup(label: StringValue) : FilterViewItem(label)
}
