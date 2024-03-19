package org.skynetsoftware.avnlauncher.ui.viewmodel

import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition

expect val viewModelsKoinModule: Module

@Composable
expect inline fun <reified T : ViewModel> viewModel(noinline parameters: ParametersDefinition? = null): T
