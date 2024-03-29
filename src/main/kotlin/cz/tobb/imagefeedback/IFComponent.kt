package cz.tobb.imagefeedback

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.util.maximumHeight
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import com.intellij.util.ui.JBUI
import org.jdesktop.swingx.StackLayout
import java.awt.*
import java.net.URL
import javax.swing.*
import javax.swing.JPanel
import java.util.Random

class IFComponent : JPanel(){
    private val label : JLabel = JLabel()
    private var imageLabel : JLabel = JLabel()
    private val header = JPanel()
    private val random = Random()
    private val imageCache = mutableMapOf<String, Image>()

    init {
        val settings = SettingState.getInstance()
        setLayout(StackLayout())
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))

        initHeader(settings.returnCodePosition)
        add(initImage())
        add(header)
    }

    private fun initHeader(returnCodePos : Int){
        header.layout = BoxLayout(header, BoxLayout.Y_AXIS)
        header.isOpaque = false
        val labelBackground = JPanel()
        label.font = label.font.deriveFont(13.0f)
        label.border = JBUI.Borders.empty(1)
        label.foreground = Color.WHITE
        labelBackground.maximumHeight = 40
        labelBackground.background = Color(0, 0, 0, 180)
        labelBackground.add(label)

        if(returnCodePos == 0){
            header.add(labelBackground)
            header.add(Box.createVerticalGlue())
        }else{
            header.add(Box.createVerticalGlue())
            header.add(labelBackground)
        }
        header.isVisible = false
    }

    private fun initImage(): JPanel {
        val imagePanel = JPanel()
        imagePanel.layout = BoxLayout(imagePanel, BoxLayout.X_AXIS)
        imagePanel.add(Box.createHorizontalGlue())
        imagePanel.add(imageLabel)
        imagePanel.add(Box.createHorizontalGlue())
        imageLabel.isVisible = false
        return imagePanel
    }

    private fun obtainImage(path : String): Image? {
        if(imageCache.containsKey(path)){
            return imageCache[path]
        }
        try {
            val url: URL = URL(path)
            val image = Toolkit.getDefaultToolkit().getImage(url)
            imageCache[path] = image
            return image
        }catch (exc: Exception){
            return null
        }
    }

    private fun showImage(feedbackImage : FeedbackImage) {
        val image: Image? = obtainImage(feedbackImage.path)
        if(image == null){
            imageLabel.icon = null
            imageLabel.text = "Could not show image \"${feedbackImage.path}\""
            imageLabel.isVisible = true
            return
        }
        val icSize = ImageIcon(image)
        val width = (feedbackImage.height * (1.0f * icSize.iconWidth / icSize.iconHeight)).toInt()
        val scaledImage = image.getScaledInstance(width, feedbackImage.height, Image.SCALE_DEFAULT)
        imageLabel.icon = ImageIcon(scaledImage)
        imageLabel.isVisible = true
        imageLabel.preferredWidth = width
        imageLabel.preferredHeight = feedbackImage.height
        imageLabel.text = ""
    }

    private fun generalUpdate(code : Int){
        val settings = SettingState.getInstance()
        header.isVisible = settings.showReturnCodePanel
        background = Color(settings.backgroundColorInt[0], settings.backgroundColorInt[1],
            settings.backgroundColorInt[2])
        if(settings.returnCodePosition == 0){
            if(header.getComponent(0) !is JPanel){
                header.remove(0)
                header.add(Box.createVerticalGlue())
            }
        }else{
            if(header.getComponent(0) is JPanel){
                header.remove(1)
                header.add(Box.createVerticalGlue(), 0)
            }
        }
        label.text = "Return code: $code"
        label.foreground = JBColor.WHITE
    }

    private fun selectImageToShow(type : FeedBackImageType): FeedbackImage? {
        val settings = SettingState.getInstance()
        val allImageOfType = settings.feedbackImages.filter { item -> item.type == type }
        if(allImageOfType.isEmpty()){
            return null
        }
        return allImageOfType[random.nextInt(allImageOfType.size)]
    }

    fun update(code : Int){
        val settings = SettingState.getInstance()
        generalUpdate(code)
        if(code == 0 && settings.showImageSuccess){
            selectImageToShow(FeedBackImageType.SUCCESS)?.let { showImage(it) }
        }else if(code != 0 && settings.showImageFail){
            selectImageToShow(FeedBackImageType.FAIL)?.let { showImage(it) }
        }else{
            imageLabel.isVisible = false
        }
        label.repaint()
    }

    fun processing(){
        generalUpdate(-1)
        val settings = SettingState.getInstance()
        if(settings.showImageProcessing){
            selectImageToShow(FeedBackImageType.PROCESSING)?.let { showImage(it) }
        }else{
            imageLabel.isVisible = false
        }
        label.text = "Processing"
    }
}