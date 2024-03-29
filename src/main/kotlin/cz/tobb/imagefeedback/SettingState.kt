package cz.tobb.imagefeedback

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

enum class FeedBackImageType {
    PROCESSING, SUCCESS, FAIL
}

class FeedbackImage(var path: String="", var height: Int=240, var type : FeedBackImageType = FeedBackImageType.PROCESSING){
    override fun toString(): String {
        return "$path ($height px) [$type]"
    }

    fun isModified(feedbackImage: FeedbackImage): Boolean {
        return path != feedbackImage.path || height != feedbackImage.height || type != feedbackImage.type
    }
}

@State(
        name = "cz.tobb.celebration.SettingState",
        storages = [Storage("ReturnCodeImageFeedBack.xml")]
)
class SettingState : PersistentStateComponent<SettingState> {

    companion object {
        fun getInstance(): SettingState {
            return ApplicationManager.getApplication().getService(SettingState::class.java)
        }
    }

    var backgroundColorInt = arrayOf(0, 0, 0)
    var showReturnCodePanel : Boolean = true
    var returnCodePosition: Int = 0

    var feedbackImages : ArrayList<FeedbackImage> = arrayListOf()
    var showImageProcessing : Boolean = true
    var showImageSuccess : Boolean = true
    var showImageFail : Boolean = true

    override fun getState(): SettingState {
        return this
    }

    override fun loadState(state: SettingState) {
        XmlSerializerUtil.copyBean(state, this);
    }
}