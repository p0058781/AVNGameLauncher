package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.skynetsoftware.avnlauncher.MR
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.model.iconRes
import org.skynetsoftware.avnlauncher.utils.next

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SortFilter(
    games: List<Game>,
    currentFilter: Filter,
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    currentGamesDisplayMode: GamesDisplayMode,
    updateAvailableIndicatorVisible: Boolean,
    modifier: Modifier = Modifier,
    setFilter: (filter: Filter) -> Unit,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
    setGamesDisplayMode: (gamesDisplayMode: GamesDisplayMode) -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Filter(currentFilter, games.size, updateAvailableIndicatorVisible, setFilter)
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Sort(currentSortOrder, currentSortDirection, setSortOrder, setSortDirection)
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                painter = painterResource(currentGamesDisplayMode.iconRes()),
                contentDescription = null,
                modifier = Modifier.size(16.dp).clickable {
                    setGamesDisplayMode(currentGamesDisplayMode.next())
                },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            )
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
}

@Composable
fun Filter(
    currentFilter: Filter,
    filteredItemsCount: Int,
    updateAvailableIndicatorVisible: Boolean,
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
        if (updateAvailableIndicatorVisible) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red),
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
                    if (updateAvailableIndicatorVisible && it == Filter.GamesWithUpdate) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red),
                        )
                    }
                }
            }
        }
    }
}
