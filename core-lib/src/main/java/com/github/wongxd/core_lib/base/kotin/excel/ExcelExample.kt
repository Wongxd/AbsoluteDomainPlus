//package com.wongxd.partymanage.base.kotin.excel
//
//import jxl.Workbook
//import jxl.write.Label
//import jxl.write.WritableImage
//import java.io.File
//
///**
// * Created by wxd1 on 2017/6/28.
// */
//
//class ExcelExample {
//    fun createExcel() {
//        try {
//            // 创建或打开Excel文件
//            val book = Workbook.createWorkbook(File(
//                    "mnt/sdcard/test.xls"))
//            // 生成名为“第一页”的工作表,参数0表示这是第一页
//            val sheet1 = book.createSheet("第一页", 0)
//            val sheet2 = book.createSheet("第三页", 2)
//            // 在Label对象的构造函数中,元格位置是第一列第一行(0,0)以及单元格内容为test
//            val label = Label(0, 0, "test")
//            // 将定义好的单元格添加到工作表中
//            sheet1.addCell(label)
//            /*
//    * 生成一个保存数字的单元格.必须使用Number的完整包路径,否则有语法歧义
//    */
//            val number = jxl.write.Number(1, 0, 555.12541)
//            sheet2.addCell(number)
//            // 写入数据并关闭文件
//            book.write()
//            book.close()
//        } catch (e: Exception) {
//            println(e)
//        }
//
//    }
//
//    /**
//     * jxl暂时不提供修改已经存在的数据表,这里通过一个小办法来达到这个目的,不适合大型数据更新! 这里是通过覆盖原文件来更新的.
//
//     * @param filePath
//     */
//    fun updateExcel(filePath: String) {
//        try {
//            val rwb = Workbook.getWorkbook(File(filePath))
//            val wwb = Workbook.createWorkbook(File(
//                    "d:/new.xls"), rwb)// copy
//            val ws = wwb.getSheet(0)
//            val wc = ws.getWritableCell(0, 0)
//            // 判断单元格的类型,做出相应的转换
//            val label = wc as Label
//            label.string = "The value has been modified"
//            wwb.write()
//            wwb.close()
//            rwb.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    companion object {
//        fun writeExcel(filePath: String) {
//            try {
//                // 创建工作薄
//                val wwb = Workbook.createWorkbook(File(filePath))
//                // 创建工作表
//                val ws = wwb.createSheet("Sheet1", 0)
//                // 添加标签文本
//                // Random rnd = new Random((new Date()).getTime());
//                // int forNumber = rnd.nextInt(100);
//                // Label label = new Label(0, 0, "test");
//                // for (int i = 0; i < 3; i++) {
//                // ws.addCell(label);
//                // ws.addCell(new jxl.write.Number(rnd.nextInt(50), rnd
//                // .nextInt(50), rnd.nextInt(1000)));
//                // }
//                // 添加图片(注意此处jxl暂时只支持png格式的图片)
//                // 0,1分别代表x,y 2,5代表宽和高占的单元格数
//                ws.addImage(WritableImage(5.0, 5.0, 2.0, 5.0, File(
//                        "mnt/sdcard/nb.png")))
//                wwb.write()
//                wwb.close()
//            } catch (e: Exception) {
//                println(e.toString())
//            }
//
//        }
//    }
//}
//
