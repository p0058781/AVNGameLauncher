package org.skynetsoftware.avnlauncher.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder

@Composable
fun SortFilter(
    games: List<Game>,
    currentFilter: Filter,
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    modifier: Modifier = Modifier,
    setFilter: (filter: Filter) -> Unit,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        Row {
            Filter(currentFilter, games.size, setFilter)
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Sort(currentSortOrder, currentSortDirection, setSortOrder, setSortDirection)
        }
    }
}

@Composable
fun Sort(
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
) {
    var showSortOrderDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.clickable {
            showSortOrderDropdown = true
        },
    ) {
        Text(stringResource(MR.strings.sortLabel))
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            buildString {
                append(currentSortOrder.label)
                append("(")
                append(currentSortDirection.label)
                append(")")
            },
        )
    }
    DropdownMenu(
        expanded = showSortOrderDropdown,
        onDismissRequest = {
            showSortOrderDropdown = false
        },
    ) {
        SortOrder.entries.forEach {
            DropdownMenuItem(
                onClick = {
                    showSortOrderDropdown = false
                    if (currentSortOrder != it) {
                        setSortOrder(it)
                    } else {
                        when (currentSortDirection) {
                            SortDirection.Ascending -> setSortDirection(SortDirection.Descending)
                            SortDirection.Descending -> setSortDirection(SortDirection.Ascending)
                        }
                    }
                },
            ) {
                Text(it.label)
            }
        }
    }
}

@Composable
fun Filter(
    currentFilter: Filter,
    filteredItemsCount: Int,
    setFilter: (filter: Filter) -> Unit,
) {
    var showFilterDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.clickable {
            showFilterDropdown = true
        },
    ) {
        Text(stringResource(MR.strings.filterLabel))
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            buildString {
                append(currentFilter.label)
                append("(")
                append(filteredItemsCount)
                append(")")
            },
        )
    }
    DropdownMenu(
        expanded = showFilterDropdown,
        onDismissRequest = {
            showFilterDropdown = false
        },
    ) {
        Filter.entries.forEach {
            DropdownMenuItem(
                onClick = {
                    showFilterDropdown = false
                    setFilter(it)
                },
            ) {
                Text(it.label)
            }
        }
    }
}
