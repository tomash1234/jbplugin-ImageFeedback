package cz.tobb.imagefeedback

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class IFConfigurable : Configurable {

    private var appComponent: AppSettingComponent? = null

    override fun createComponent(): JComponent {
        appComponent = AppSettingComponent()
        appComponent!!.load(SettingState.getInstance())
        return appComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        return appComponent!!.isModified(SettingState.getInstance())
    }

    override fun apply() {
        appComponent!!.save(SettingState.getInstance())
    }

    override fun getDisplayName(): String {
        return "Image Execution Feedback"
    }

    override fun disposeUIResources() {
        appComponent = null
    }

    override fun reset() {
        val settings = SettingState.getInstance()
        appComponent!!.load(settings)
        appComponent!!.refresh()
    }
}