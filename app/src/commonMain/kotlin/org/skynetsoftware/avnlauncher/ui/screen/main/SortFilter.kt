package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.filterLabel
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationSortDirection
import org.skynetsoftware.avnlauncher.app.generated.resources.sortLabel
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.model.hoverExplanation
import org.skynetsoftware.avnlauncher.domain.model.iconRes
import org.skynetsoftware.avnlauncher.mode.StringValue
import org.skynetsoftware.avnlauncher.ui.component.HoverExplanation
import org.skynetsoftware.avnlauncher.utils.collectIsHoveredAsStateDelayed

@Composable
fun SortFilter(
    games: List<Game>,
    currentFilter: Filter,
    filters: List<FilterViewItem>,
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
            Filter(currentFilter, filters, games.size, updateAvailableIndicatorVisible, setFilter)
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            Spacer(modifier = Modifier.width(5.dp))
            Sort(currentSortOrder, currentSortDirection, setSortOrder, setSortDirection)
            Spacer(modifier = Modifier.width(5.dp))
            Text("|")
            GamesDisplayMode.entries.forEach {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsStateDelayed()

                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(it.iconRes()),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).clickable {
                        setGamesDisplayMode(it)
                    }.hoverable(interactionSource),
                    colorFilter = ColorFilter.tint(
                        if (it == currentGamesDisplayMode) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSurface
                        },
                    ),
                )
                if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
                    HoverExplanation(stringResource(it.hoverExplanation()))
                }
            }
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

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsStateDelayed()

    Row(
        modifier = Modifier.clickable {
            showSortOrderDropdown = true
        }.hoverable(interactionSource),
    ) {
        Text(stringResource(Res.string.sortLabel))
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
        if (isHovered && LocalWindowControl.current?.windowFocused?.value == true) {
            HoverExplanation(stringResource(Res.string.hoverExplanationSortDirection))
        }
    }
}

@Composable
fun Filter(
    currentFilter: Filter,
    filters: List<FilterViewItem>,
    filteredItemsCount: Int,
    updateAvailableIndicatorVisible: Boolean,
    setFilter: (filter: Filter) -> Unit,
) {
    println(currentFilter)
    var showFilterDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.clickable {
            showFilterDropdown = true
        },
    ) {
        Text(stringResource(Res.string.filterLabel))
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            buildString {
                val stringValue = filters.find { it is FilterViewItem.FilterItem && it.filter == currentFilter }?.label
                when (stringValue) {
                    is StringValue.String -> append(stringValue.string)
                    is StringValue.StringResource -> append(stringResource(stringValue.stringResource))
                    else -> {}
                }
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
            filters.forEach {
                when (it) {
                    is FilterViewItem.FilterGroup -> {
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = when (it.label) {
                                is StringValue.String -> it.label.string
                                is StringValue.StringResource -> stringResource(it.label.stringResource)
                            },
                            style = MaterialTheme.typography.body2,
                        )
                    }
                    is FilterViewItem.FilterItem -> {
                        DropdownMenuItem(
                            onClick = {
                                showFilterDropdown = false
                                setFilter(it.filter)
                            },
                        ) {
                            Text(
                                text = when (it.label) {
                                    is StringValue.String -> it.label.string
                                    is StringValue.StringResource -> stringResource(it.label.stringResource)
                                },
                            )
                            if (updateAvailableIndicatorVisible && it.filter == Filter.GamesWithUpdate) {
                                Box(
                                    modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
