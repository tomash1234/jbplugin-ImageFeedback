package cz.tobb.imagefeedback

import javax.swing.table.AbstractTableModel


class FeedbackImageTableModel() : AbstractTableModel() {

    private val feedbackImages : ArrayList<FeedbackImage> = arrayListOf()
    private val columnNames = arrayOf("URL", "Type", "Height [px]")
    private val columnClass = arrayOf<Class<*>>(
        String::class.java, FeedBackImageType::class.java, Integer::class.java
    )

    override fun getRowCount(): Int {
        return feedbackImages.size
    }

    override fun getColumnCount(): Int {
       return columnNames.size
    }

    fun addItem(item : FeedbackImage) {
        feedbackImages.add(item)
    }

    fun addAllItems(items : List<FeedbackImage>) {
        feedbackImages.addAll(items)
    }

    fun removeItem(item : FeedbackImage){
        feedbackImages.remove(item)
    }

    fun removeItemAt(selectedRow: Int) {
        if(selectedRow == -1){
            return
        }
        feedbackImages.removeAt(selectedRow)
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val item = feedbackImages[rowIndex]
        when(columnIndex){
            0 -> item.path = aValue.toString()
            1 -> item.type = aValue as FeedBackImageType
            2 -> {
                val value = aValue as Int
                if(value <= 0 || value >= 2048){
                    return
                }
                item.height = value
            }
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val item = feedbackImages[rowIndex]
        return when(columnIndex){
            0 -> item.path
            1 -> item.type
            2 -> item.height
            else -> ""
        }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return columnClass[columnIndex]
    }

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    fun moveUp(selectedRow: Int) : Int{
        if(selectedRow == -1 ||selectedRow == 0){
            return selectedRow
        }
        feedbackImages.add(selectedRow - 1, feedbackImages.removeAt(selectedRow))
        return selectedRow - 1
    }

    fun moveDown(selectedRow: Int) : Int{
        if(selectedRow == -1 || selectedRow >= feedbackImages.size - 1){
            return selectedRow
        }
        feedbackImages.add(selectedRow + 1, feedbackImages.removeAt(selectedRow))
        return selectedRow + 1
    }

    fun getFeedbackImage(): List<FeedbackImage> {
        return ArrayList<FeedbackImage>(feedbackImages)
    }

    fun clear() {
        feedbackImages.clear()
    }
}