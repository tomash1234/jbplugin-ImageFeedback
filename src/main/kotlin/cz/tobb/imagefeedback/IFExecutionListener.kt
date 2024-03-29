package cz.tobb.imagefeedback

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme


class IFExecutionListener : ExecutionListener {

    companion object {
        var IFComponent : IFComponent? = null
    }

    init {
        val connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener { editorColorsScheme: EditorColorsScheme? -> })
    }

    override fun processTerminated(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler, exitCode: Int) {
        IFComponent?.update(exitCode)
    }

    override fun processStarting(executorId: String, env: ExecutionEnvironment) {
        super.processStarting(executorId, env)
        IFComponent?.processing()
    }
}