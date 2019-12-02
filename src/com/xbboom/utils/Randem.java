package com.xbboom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Randem {
	public static String getRandomChar(int length) {            //��������ַ���   
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
        buffer.append(chr[random.nextInt(36)]);
        }
        return buffer.toString();
        }
	//��ȡn���º������
	public String getDate(String inDate,int num) {
		Calendar c = Calendar.getInstance();//���һ��������ʵ��   
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
        Date date = null;   
        try{   
            date = sdf.parse(inDate);//��ʼ����   
        }catch(Exception e){  

        }   
        c.setTime(date);//��������ʱ��   
        c.add(Calendar.MONTH,num);//���������·�������n����
        String strDate = sdf.format(c.getTime());//�ĵ�����Ҫ��n���º������   
        System.out.println(strDate);
        return strDate;
       
	}
	//�Ƚ��������ڴ�С
	public int compareDate(String date1,String date2) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(date1).compareTo(sdf.parse(date2));
	}
}
