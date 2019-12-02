package com.xbboom.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Test {
	public static void main(String[] args) throws SchedulerException, InterruptedException {
	        String host = "https://wuliu.market.alicloudapi.com";
	        String path = "/kdi";
	        String method = "GET";
	        System.out.println("请先替换成自己的AppCode");
	        String appcode = "8d832c12bfeb4873b2236d2902ed2308";  // !!!替换填写自己的AppCode 在买家中心查看
	        Map<String, String> headers = new HashMap<String, String>();
	        headers.put("Authorization", "APPCODE " + appcode); //格式为:Authorization:APPCODE 83359fd73fe11248385f570e3c139xxx
	        Map<String, String> querys = new HashMap<String, String>();
	        querys.put("no", "YT4147885232945");// !!! 请求参数
	        //querys.put("type", "zto");// !!! 请求参数
	        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip
	        try {
		    	/**
		    	* 重要提示如下:
		    	* HttpUtils请从
		    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
	                * 或者直接下载：
	                * http://code.fegine.com/HttpUtils.zip
		    	* 下载
		    	*
		    	* 相应的依赖请参照
		    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
	                * 相关jar包（非pom）直接下载：
	                * http://code.fegine.com/aliyun-jar.zip
		    	*/
	                HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
		    	//System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
	                //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
		    	//获取response的body
	          System.out.println(EntityUtils.toString(response.getEntity())); //输出json
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	}
}
