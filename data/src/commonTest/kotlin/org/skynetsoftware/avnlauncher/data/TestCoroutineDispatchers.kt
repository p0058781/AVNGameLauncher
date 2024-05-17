package org.skynetsoftware.avnlauncher.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineDispatchers : CoroutineDispatchers {
    override val main = UnconfinedTestDispatcher()
    override val io = UnconfinedTestDispatcher()
    override val default = UnconfinedTestDispatcher()
}
