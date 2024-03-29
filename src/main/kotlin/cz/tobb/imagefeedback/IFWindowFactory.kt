package cz.tobb.imagefeedback

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class IFWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(proj: Project, toolWindow: ToolWindow) {
        val panel = IFComponent()
        toolWindow.title = "Image Feedback"
        IFExecutionListener.IFComponent = panel
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
        toolWindow.stripeTitle = "Image Feedback"
    }
}