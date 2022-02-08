package com.grappenmaker.asmexport

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import me.coley.recaf.Recaf
import me.coley.recaf.control.gui.GuiController
import me.coley.recaf.plugin.api.ContextMenuInjectorPlugin
import me.coley.recaf.ui.ContextBuilder
import me.coley.recaf.ui.controls.text.BytecodeContextHandling
import me.coley.recaf.ui.controls.text.EditorPane
import me.coley.recaf.ui.controls.text.model.Languages
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import org.plugface.core.annotations.Plugin
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

@Plugin(name = "ASMExportPlugin")
@Suppress("unused")
class ASMExportPlugin : ContextMenuInjectorPlugin {
    override fun forClass(builder: ContextBuilder, menu: ContextMenu, name: String) {
        menu.items.add(MenuItem("Disassemble with ASM").also {
            it.setOnAction {
                showWindow(
                    "Disassembly (with ow2 ASM)",
                    getOutput(Recaf.getCurrentWorkspace().getClassReader(name), Textifier()),
                    "bytecode"
                )
            }
        })

        menu.items.add(MenuItem("ASMify class").also {
            it.setOnAction {
                showWindow(
                    "ASMifier output",
                    getOutput(Recaf.getCurrentWorkspace().getClassReader(name), ASMifier()),
                    "java"
                )
            }
        })
    }

    override fun getVersion() = "1.0"
    override fun getDescription() = "Plugin to analyze code as ASM code or as ASM disassembly"
}

private fun getOutput(reader: ClassReader, printer: Printer): String {
    val out = ByteArrayOutputStream()
    reader.accept(TraceClassVisitor(null, printer, PrintWriter(out)), 0)
    return out.toByteArray().decodeToString()
}

private fun getGUIGontroller() = Recaf.getController() as? GuiController
    ?: error("How can there be no gui controller, when the ContextMenuInjector was called?")

private fun getEditorPane(text: String, language: String) =
    EditorPane<Nothing, BytecodeContextHandling>(
        getGUIGontroller(),
        Languages.find(language),
        ::BytecodeContextHandling
    ).also {
        it.setWrapText(getGUIGontroller().config().display().forceWordWrap)
        it.text = text
        it.scrollToTop()
    }

private fun showWindow(name: String, text: String, language: String) =
    getGUIGontroller().windows().window(name, getEditorPane(text, language), 600, 600).show()