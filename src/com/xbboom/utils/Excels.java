package com.xbboom.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.record.formula.functions.Var;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import com.xbboom.bean.Orders;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Excels {
	
//	public static void main(String[] args) {
//		try {
//			testExcel6(List<Orders> list);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	@Test
    public static String testExcel6(List<Orders> list) throws IOException {
        //创建Excel工作薄对象
        HSSFWorkbook workbook=new HSSFWorkbook();
        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet("fan");
        //创建行的单元格，从0开始
        //HSSFRow row = sheet.createRow(0);
        //创建单元格
        //HSSFCell cell=row.createCell(0);
        
        HSSFCellStyle style=workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        //设置值
//        HSSFRow row1 = sheet.createRow(1);
//        HSSFCell cell1=row1.createCell(0);
//        cell.setCellValue("发动机是离开房间倒是");
//        cell1.setCellValue("发动机是离开房间倒是");
        String str="";
        HSSFRow row0 = sheet.createRow(0);
        
        row0.setHeightInPoints(30);
        for (int j = 0; j < 8; j++) {
        	sheet.setColumnWidth(j, 31 * 256);
    		HSSFCell cell=row0.createCell(j);
    		if(j==0) {
    			str = "订单号";
    		}else if(j==1) {
    			str = "食品";
    		}else if(j==2) {
    			str = "价格";
    		}else if(j==3) {
    			str = "下单人";
    		}else if(j==4) {
    			str = "联系电话";
    		}else if(j==5) {
    			str = "地址";
    		}else if(j==6) {
    			str = "备注";
    		}else if(j==7) {
    			str = "下单时间";
    		}
    		cell.setCellStyle(style);
    		 cell.setCellValue(str);
		}
        double price = 0;
        for (int i = 0; i < list.size(); i++) {
        	HSSFRow row = sheet.createRow(i+1);
        	row.setHeightInPoints(30);
        	price +=list.get(i).getPrice();
        	for (int j = 0; j < 8; j++) {
//        		sheet.setColumnWidth(j, 31 * 256);
        		HSSFCell cell=row.createCell(j);
        		if(j==0) {
        			str = list.get(i).getOrdernum();
        		}else if(j==1) {
        			String goods = "";
        			JSONArray array = JSONArray.fromObject(list.get(i).getGoods());
        			System.out.println(array.size());
        			for (int k = 0; k < array.size(); k++) {
        				System.out.println("书库"+array.get(k));
						JSONObject object = JSONObject.fromObject(array.get(k));
						goods+="套餐:"+object.getString("goods_name")+",数量"+object.getInt("num")+",单价"+object.getDouble("price")+";";
					}
        			str = goods;
        		}else if(j==2) {
        			str = list.get(i).getPrice()+"";
        		}else if(j==3) {
        			str = list.get(i).getName();
        		}else if(j==4) {
        			str = list.get(i).getPhone();
        		}else if(j==5) {
        			str = list.get(i).getAddr();
        		}else if(j==6) {
        			str = list.get(i).getBz();
        		}else if(j==7) {
        			str = list.get(i).getDates();
        		}
        		cell.setCellStyle(style);
        		 cell.setCellValue(str);
			}
        	
        	
		}
HSSFRow row1 = sheet.createRow(list.size()+4);
        
        row1.setHeightInPoints(40);
        for (int j = 0; j < 4; j++) {
        	sheet.setColumnWidth(j, 31 * 256);
    		HSSFCell cell=row1.createCell(j);
    		if(j==0) {
    			str = "总收入";
    		}else if(j==1) {
    			str = price+"元";
    		}else if(j==2) {
    			str = "订单量";
    		}else if(j==3) {
    			str = list.size()+"单";
    		}
    		cell.setCellStyle(style);
    		 cell.setCellValue(str);
		}
        //创建单元格样式
//        HSSFCellStyle style=workbook.createCellStyle();
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
//        cell.setCellStyle(style);
//        //设置保留2位小数--使用Excel内嵌的格式
//        HSSFCell cell1 = row.createCell(1);
//        cell1.setCellValue(12.3456789);
//        style=workbook.createCellStyle();
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
//        cell1.setCellStyle(style);
//        //设置货币格式--使用自定义的格式
//        HSSFCell cell2 = row.createCell(2);
//        cell2.setCellValue(12345.6789);
//        style=workbook.createCellStyle();
//        style.setDataFormat(workbook.createDataFormat().getFormat("￥#,##0"));
//        cell2.setCellStyle(style);
//        //设置百分比格式--使用自定义的格式
//        HSSFCell cell3 = row.createCell(3);
//        cell3.setCellValue(0.123456789);
//        style=workbook.createCellStyle();
//        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
//        cell3.setCellStyle(style);
//        //设置中文大写格式--使用自定义的格式
//        HSSFCell cell4 = row.createCell(4);
//        cell4.setCellValue(12345);
//        style=workbook.createCellStyle();
//        style.setDataFormat(workbook.createDataFormat().getFormat("[DbNum2][$-804]0"));
//        cell4.setCellStyle(style);
//        //设置科学计数法格式--使用自定义的格式
//        HSSFCell cell5 = row.createCell(5);
//        cell5.setCellValue(12345);
//        style=workbook.createCellStyle();
//        style.setDataFormat(workbook.createDataFormat().getFormat("0.00E+00"));
//        cell5.setCellStyle(style);
        //文档输出 + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString()
        
        FileOutputStream out = new FileOutputStream("C:/java/apache-tomcat-8.0.51/apache-tomcat-8.0.51/webapps/excel/"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString()+".xls");
        workbook.write(out);
        out.close();
        return  "https://xmj.ccsx.net/excel/"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString()+".xls";
    }

}
