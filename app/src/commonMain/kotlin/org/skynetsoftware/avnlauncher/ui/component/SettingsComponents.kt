package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(
            modifier = Modifier.height(5.dp),
        )
        Card {
            Column {
                content()
            }
        }
    }
}

@Composable
fun Item(
    title: String,
    subtitle: String = "",
    endContent: @Composable RowScope.() -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    val modifier = Modifier.run {
        if (onClick != null) {
            clickable {
                onClick()
            }
        } else {
            this
        }
    }
    Row(
        modifier = modifier.padding(10.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.onPrimary,
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
        endContent()
    }
}

@Composable
fun RowScope.Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        modifier = Modifier.align(Alignment.CenterVertically),
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun <T> RowScope.Dropdown(
    values: List<T>,
    currentValue: T,
    onValueChanged: (newValue: T) -> Unit,
) {
    var dropdownShown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.align(Alignment.CenterVertically).clickable {
            dropdownShown = true
        },
    ) {
        Text(
            text = currentValue.toString(),
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
        )
        DropdownMenu(
            expanded = dropdownShown,
            onDismissRequest = {
                dropdownShown = false
            },
        ) {
            values.forEach {
                DropdownMenuItem(
                    onClick = {
                        dropdownShown = false
                        onValueChanged(it)
                    },
                ) {
                    Text(
                        text = it.toString(),
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.Input(
    currentValue: String,
    hint: String,
    validateInput: (value: String) -> Boolean,
    onSaveClicked: (value: String) -> Unit,
) {
    var value by remember { mutableStateOf(currentValue) }

    val textStyle = MaterialTheme.typography.body2
    val colors = TextFieldDefaults.outlinedTextFieldColors()
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(true).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val interactionSource = remember { MutableInteractionSource() }
    val shape = MaterialTheme.shapes.small

    Row(
        modifier = Modifier.align(Alignment.CenterVertically),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        @OptIn(ExperimentalMaterialApi::class)
        (
            BasicTextField(
                value = value,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(colors.backgroundColor(true).value, shape)
                    .defaultMinSize(
                        minWidth = 140.dp,
                        minHeight = 42.dp,
                    ),
                onValueChange = {
                    value = it
                },
                cursorBrush = SolidColor(colors.cursorColor(false).value),
                textStyle = mergedTextStyle,
                interactionSource = interactionSource,
                decorationBox = @Composable { innerTextField ->
                    TextFieldDefaults.OutlinedTextFieldDecorationBox(
                        value = value,
                        visualTransformation = VisualTransformation.None,
                        innerTextField = innerTextField,
                        placeholder = {
                            Text(
                                text = hint,
                                style = textStyle,
                            )
                        },
                        singleLine = true,
                        enabled = true,
                        interactionSource = interactionSource,
                        colors = colors,
                        border = {
                            TextFieldDefaults.BorderBox(
                                enabled = true,
                                isError = false,
                                interactionSource,
                                colors,
                                shape,
                            )
                        },
                        contentPadding = PaddingValues(8.dp),
                    )
                },
            )
        )
        if (validateInput(value) && value.isNotBlank() && value != currentValue) {
            Spacer(
                modifier = Modifier.width(10.dp),
            )
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onSaveClicked(value)
                },
            )
        }
    }
}
