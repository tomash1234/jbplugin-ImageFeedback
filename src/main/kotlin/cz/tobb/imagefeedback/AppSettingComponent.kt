package cz.tobb.imagefeedback

import com.intellij.icons.AllIcons
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.ColorPicker
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.ui.util.*
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.table.TableColumn


class AppSettingComponent {


    private val panel: JPanel = JPanel(VerticalLayout(6))
    private var colorSamplePanel = JPanel(BorderLayout())
    private val checkboxShownReturnCode = JBCheckBox("Show return code")
    private val checkboxShowImageProcessing= JBCheckBox("Show image Processing")
    private val checkboxShowImageSuccess = JBCheckBox("Show image Success")
    private val checkboxShowImageFail = JBCheckBox("Show image Fail")
    private val comboBoxLabelAlignment = ComboBox(arrayOf("TOP", "BOTTOM"))
    private val tableModel = FeedbackImageTableModel()
    private val table = JBTable(tableModel)
    private val scrollPane = JBScrollPane(table)

    init {
        initBackgroundColor()
        initCloseInfo()
        initTableButton()
        initTable()
    }

    private fun initCloseInfo(){
        val horPanel = JPanel(HorizontalLayout(8))

        horPanel.add(checkboxShowImageProcessing)
        horPanel.add(checkboxShowImageSuccess)
        horPanel.add(checkboxShowImageFail)

        val returnCodeDispalPanel = JPanel(HorizontalLayout(8))
        returnCodeDispalPanel.add(checkboxShownReturnCode)
        returnCodeDispalPanel.add(JBLabel("Return code position"))
        returnCodeDispalPanel.add(comboBoxLabelAlignment)

        panel.add(returnCodeDispalPanel)
        panel.add(horPanel)
    }

    private fun initTableButton(){
        val buttonAdd = JButton()
        buttonAdd.icon = AllIcons.General.Add
        val buttonRemove = JButton()
        buttonRemove.icon = AllIcons.General.Remove
        val buttonMoveUp = JButton()
        buttonMoveUp.icon = AllIcons.General.ArrowUp
        val buttonMoveDown = JButton()
        buttonMoveDown.icon = AllIcons.General.ArrowDown
        val locPanel = JPanel(HorizontalLayout(0))
        buttonAdd.preferredWidth = 40
        buttonRemove.preferredWidth = 40
        buttonMoveUp.preferredWidth = 40
        buttonMoveDown.preferredWidth = 40

        locPanel.add(buttonAdd)
        locPanel.add(buttonRemove)
        locPanel.add(buttonMoveUp)
        locPanel.add(buttonMoveDown)

        val wholeTablePanel = JPanel(VerticalLayout())
        wholeTablePanel.add(locPanel)
        wholeTablePanel.add(scrollPane)
        panel.add(wholeTablePanel)

        buttonAdd.addActionListener {
            tableModel.addItem(FeedbackImage())
            table.setRowSelectionInterval(tableModel.rowCount - 1, tableModel.rowCount - 1)
            refresh()
        }
        buttonRemove.addActionListener{tableModel.removeItemAt(table.selectedRow)
            refresh()
        }
        buttonMoveUp.addActionListener{
                val ind = tableModel.moveUp(table.selectedRow)
                table.setRowSelectionInterval(ind, ind)
                refresh()
        }
        buttonMoveDown.addActionListener{
            val ind = tableModel.moveDown(table.selectedRow)
            table.setRowSelectionInterval(ind, ind)
            refresh()
        }
    }

    private fun initTable(){
        table.columnModel.getColumn(0).preferredWidth = 200
        val columnType: TableColumn = table.columnModel.getColumn(1)
        columnType.minWidth = 100
        table.columnModel.getColumn(2).minWidth = 50
        columnType.setCellEditor(DefaultCellEditor(JComboBox(FeedBackImageType.values())))
        table.selectionModel.selectionMode = SINGLE_SELECTION
        scrollPane.preferredHeight = 400
        table.emptyText.text = "No feedback images. Add them using Add button"
    }

    private fun initBackgroundColor(sampleSize: Int = 20){
        val backgroundColorPanel = JPanel(HorizontalLayout(4))

        val colorButton = JButton("Change")
        colorButton.addActionListener {
            ColorPicker.showColorPickerPopup(ProjectUtil.getActiveProject(), colorSamplePanel.background) { color: Color, any: Any ->
                colorSamplePanel.background = color
            }
        }
        colorSamplePanel.preferredWidth = sampleSize
        colorSamplePanel.minimumWidth = sampleSize
        colorSamplePanel.maximumWidth = sampleSize
        colorSamplePanel.preferredHeight = sampleSize
        colorSamplePanel.minimumHeight = sampleSize
        colorSamplePanel.maximumHeight = sampleSize
        backgroundColorPanel.add(JBLabel("Background color: "))
        backgroundColorPanel.add(colorSamplePanel)
        backgroundColorPanel.add(colorButton)
        panel.add(backgroundColorPanel)
    }

    fun load(state : SettingState) {
        colorSamplePanel.background = Color(state.backgroundColorInt[0], state.backgroundColorInt[1], state.backgroundColorInt[2])
        checkboxShownReturnCode.isSelected = state.showReturnCodePanel
        comboBoxLabelAlignment.selectedIndex = state.returnCodePosition

        checkboxShowImageProcessing.isSelected = state.showImageProcessing
        checkboxShowImageSuccess.isSelected = state.showImageSuccess
        checkboxShowImageFail.isSelected = state.showImageFail
        tableModel.clear()
        tableModel.addAllItems(state.feedbackImages)
    }

    fun save(state: SettingState) {
        state.backgroundColorInt = arrayOf(colorSamplePanel.background.red, colorSamplePanel.background.green,
                colorSamplePanel.background.blue)
        state.showReturnCodePanel = checkboxShownReturnCode.isSelected
        state.returnCodePosition = comboBoxLabelAlignment.selectedIndex

        state.showImageProcessing = checkboxShowImageProcessing.isSelected
        state.showImageSuccess = checkboxShowImageSuccess.isSelected
        state.showImageFail = checkboxShowImageFail.isSelected
        state.feedbackImages.clear()
        state.feedbackImages.addAll(tableModel.getFeedbackImage())
    }

    fun getPanel(): JPanel {
        return panel
    }

    fun isModified(state: SettingState): Boolean {
        if(state.backgroundColorInt[0] != colorSamplePanel.background.red ||
                state.backgroundColorInt[1] != colorSamplePanel.background.green ||
                state.backgroundColorInt[2] != colorSamplePanel.background.blue){
            return true
        }
        if(state.showReturnCodePanel != checkboxShownReturnCode.isSelected ||
            state.showImageProcessing != checkboxShowImageProcessing.isSelected ||
            state.showImageSuccess != checkboxShowImageSuccess.isSelected ||
            state.showImageFail != checkboxShowImageFail.isSelected){
            return true
        }
        if(state.returnCodePosition != comboBoxLabelAlignment.selectedIndex){
            return true
        }

        if(state.feedbackImages.size != tableModel.rowCount){
            return true
        }
        val tableFeedBackImages = tableModel.getFeedbackImage()
        for(i in 0 until state.feedbackImages.size){
            if(tableFeedBackImages[i].isModified(state.feedbackImages[i])){
                return true
            }
        }
        return false
    }

    fun refresh() {
        SwingUtilities.getWindowAncestor(panel)?.repaint()
    }
}