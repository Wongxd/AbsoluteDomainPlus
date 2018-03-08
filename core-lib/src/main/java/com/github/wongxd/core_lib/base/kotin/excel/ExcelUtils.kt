//package com.wongxd.partymanage.base.kotin.excel
//
//import android.os.Environment
//import jxl.Cell
//import jxl.Workbook
//import jxl.WorkbookSettings
//import jxl.write.*
//import java.io.File
//import java.io.IOException
//import java.io.InputStream
//import java.util.*
//
//object ExcelUtils {
//    var arial14font: WritableFont? = null
//
//    var arial14format: WritableCellFormat? = null
//    var arial10font: WritableFont? = null
//    var arial10format: WritableCellFormat? = null
//    var arial12font: WritableFont? = null
//    var arial12format: WritableCellFormat? = null
//
//    val UTF8_ENCODING = "UTF-8"
//    val GBK_ENCODING = "GBK"
//
//    /**
//     * init three type of font for  write the excel things
//     */
//    fun format() {
//        try {
//            /**
//             * arial14font
//             */
//            arial14font = WritableFont(WritableFont.ARIAL, 14,
//                    WritableFont.BOLD)
//            arial14font!!.colour = jxl.format.Colour.LIGHT_BLUE
//            arial14format = WritableCellFormat(arial14font)
//            arial14format!!.alignment = jxl.format.Alignment.CENTRE
//            arial14format!!.setBorder(jxl.format.Border.ALL,
//                    jxl.format.BorderLineStyle.THIN)
//            arial14format!!.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW)
//
//            /**
//             * arial10font
//             */
//            arial10font = WritableFont(WritableFont.ARIAL, 10,
//                    WritableFont.BOLD)
//            arial10format = WritableCellFormat(arial10font)
//            arial10format!!.alignment = jxl.format.Alignment.CENTRE
//            arial10format!!.setBorder(jxl.format.Border.ALL,
//                    jxl.format.BorderLineStyle.THIN)
//            arial10format!!.setBackground(jxl.format.Colour.LIGHT_BLUE)
//
//            /**
//             * arial12font
//             */
//            arial12font = WritableFont(WritableFont.ARIAL, 12)
//            arial12format = WritableCellFormat(arial12font)
//            arial12format!!.setBorder(jxl.format.Border.ALL,
//                    jxl.format.BorderLineStyle.THIN)
//        } catch (e: WriteException) {
//
//            e.printStackTrace()
//        }
//
//    }
//
//
//    /**
//     * @param tableList list a list of string[]. each string[] for one row
//     * *
//     * @param colName   String[] for colName
//     * *
//     * @param fileName  fileName without type( like .xls)
//     */
//    fun writeTableListToExcel(tableList: List<Array<String>>?, colName: Array<String>, fileName: String): Boolean {
//        var flag = false
//        if (tableList != null && tableList.size > 0) {
//            format()
//            var writebook: WritableWorkbook? = null
//            val `in`: InputStream? = null
//            try {
//                val setEncode = WorkbookSettings()
//                setEncode.encoding = UTF8_ENCODING
//                var dirs: File? = null
//                //如果sd卡存在并且没有被移除
//                if (Environment.MEDIA_MOUNTED == Environment
//                        .getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
//                    dirs = Environment.getExternalStorageDirectory()
//                } else {
//                    dirs = Environment.getDataDirectory()
//                }
//
//                val dir = File(dirs, "WExcel")
//                if (!dir.exists()) dir.mkdirs()
//                val xlsFile = File(dir, fileName + ".xls")
//                val sheet: WritableSheet
//                if (xlsFile.exists()) xlsFile.delete()
//                writebook = Workbook.createWorkbook(xlsFile)
//                sheet = writebook!!.createSheet("wSheet1", 0)
//                // add the colName
//                for (col in colName.indices) {
//                    sheet.addCell(Label(col, 0, colName[col], arial10format))
//                }
//
//                //add each row and col
//                for (r in tableList.indices) {
//                    val row = tableList[r]
//                    for (c in row.indices) {
//                        sheet.addCell(Label(c, r + 1, row[c], arial12format))
//                    }
//                }
//                writebook.write()
//                flag = true
//            } catch (e: Exception) {
//                e.printStackTrace()
//                flag = false
//            } finally {
//                if (writebook != null) {
//                    try {
//                        writebook.close()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                }
//                if (`in` != null) {
//                    try {
//                        `in`.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                }
//            }
//
//        }
//        return flag
//    }
//
//    /**
//     * return a list of string[].
//     * one string[]  is one row
//
//     * @param f the xls(excel) file to read
//     * *
//     * @return
//     */
//    fun readFromExcel(f: File): List<Array<String?>> {
//        val billList = ArrayList<Array<String?>>()
//        try {
//            var course: Workbook? = null
//            course = Workbook.getWorkbook(f)
//            val sheet = course!!.getSheet(0)
//
//            var cell: Cell? = null
//            for (i in 0..sheet.rows - 1) {
//                val rowLength = sheet.getRow(i).size
//                val oneRow = arrayOfNulls<String>(rowLength)
//                //                Logger.e("第 " + i + " 行， 共" + rowLength + " 列");
//                for (j in 0..rowLength - 1) {
//                    cell = sheet.getRow(i)[j]
//                    oneRow[j] = cell?.contents
//                }
//                billList.add(oneRow)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return billList
//    }
//
//
//    fun getValueByRef(cls: Class<*>, fieldName: String): Any {
//        var fieldName = fieldName
//        var value: Any? = null
//        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1).toRegex(), fieldName
//                .substring(0, 1).toUpperCase())
//        val getMethodName = "get" + fieldName
//        try {
//            val method = cls.getMethod(getMethodName)
//            value = method.invoke(cls)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return value!!
//    }
//}