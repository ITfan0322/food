package com.xbboom.utils;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.Sides;

public class PrintImg {
	public static void main(String[] args) {
		try {
			Print("","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void Print(String str,String path) throws Exception {
		File file = new File("G:/20190910152436.jpg");
//		String printerName = "POS58 Printer";//打印机名称
		String printerName = "POS58 Printer";//打印机名称
		System.out.println("打印 "+str+".JPG");
		JPGPrint(file, printerName);
	}

	// 传入文件和打印机名称
	public static void JPGPrint(File file, String printerName) throws PrintException {
		if (file == null) {
			System.err.println("缺少打印文件");
		}
		InputStream fis = null;
		try {
			// 设置打印格式，如果未确定类型，可选择autosense
			DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
			// 设置打印参数
			PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
			MediaPrintableArea area = new MediaPrintableArea(0, 0, 250, 250, MediaPrintableArea.MM);
			aset.add(area);
			aset.add(new Copies(1)); // 份数
			// aset.add(MediaSize.ISO.A4); //纸张
			// aset.add(Finishings.STAPLE);//装订
			aset.add(Sides.ONE_SIDED);// 单双面
//			aset.add(Sides.DUPLEX);// 单双面
			// 定位打印服务
			PrintService printService = null;
			if (printerName != null) {
				// 获得本台电脑连接的所有打印机
				PrintService[] printServices = PrinterJob.lookupPrintServices();
				if (printServices == null || printServices.length == 0) {
					System.out.print("打印失败，未找到可用打印机，请检查。");
					return;
				}
				// 匹配指定打印机
				for (int i = 0; i < printServices.length; i++) {
					System.out.println(printServices[i].getName());
					if (printServices[i].getName().contains(printerName)) {
						printService = printServices[i];
						break;
					}
				}
				if (printService == null) {
					System.out.print("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
					return;
				}
			}
			fis = new FileInputStream(file); //构造待打印的文件流
			Doc doc = new SimpleDoc(fis, flavor, null);
			DocPrintJob job = printService.createPrintJob(); // 创建打印作业
			job.print(doc, aset);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			// 关闭打印的文件流
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
