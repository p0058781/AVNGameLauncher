package org.skynetsoftware.avnlauncher.filepicker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Suppress("SpreadOperator")
internal object FilePicker {
    suspend fun pickFile(
        pickerType: PickerType,
        initialDirectory: String? = null,
        title: String? = null,
        extensions: Array<String>? = null,
    ): File? =
        withContext(Dispatchers.IO) {
            val jFileChooser = JFileChooser(initialDirectory)
            jFileChooser.fileSelectionMode = when (pickerType) {
                PickerType.File -> JFileChooser.FILES_ONLY
                PickerType.Directory -> JFileChooser.DIRECTORIES_ONLY
            }
            jFileChooser.isMultiSelectionEnabled = false

            if (extensions != null) {
                val filter = FileNameExtensionFilter(null, *extensions)
                jFileChooser.addChoosableFileFilter(filter)
            }

            if (title != null) {
                jFileChooser.dialogTitle = title
            }

            jFileChooser.showOpenDialog(null)
            jFileChooser.selectedFile
        }
}
