package com.xbboom.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.xbboom.bean.Address_information;
import com.xbboom.bean.Admin;
import com.xbboom.bean.Goods_information;
import com.xbboom.bean.Orders;
import com.xbboom.bean.User_personal_information;
import com.xbboom.bean.Yorder;
import com.xbboom.service.XbService;
import com.xbboom.utils.Excels;
import com.xbboom.utils.FileUploadUtils;
import com.xbboom.utils.GetOpenid;
import com.xbboom.utils.HttpUtil;
import com.xbboom.utils.PayCommonUtil;
import com.xbboom.utils.SalesTicket;
import com.xbboom.utils.SmsDemo;
import com.xbboom.utils.XMLUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class XbController {
	private XbService s;

	public XbService getS() {
		return s;
	}

	private String url = "https://xmj.ccsx.net";

	@Resource
	public void setS(XbService s) {
		this.s = s;
	}

	// 获取微信信息
	@ResponseBody
	@RequestMapping("getUserInfo")
	public void getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String js_code = request.getParameter("js_code");
		String encryptedData = request.getParameter("encryptedData");
		String iv = request.getParameter("iv");
		//System.out.println("encryptedData:"+encryptedData);
		String sessionkey = new GetOpenid().get(js_code);
		//System.out.println("sessionkey:"+sessionkey);
		JSONObject obj = new GetOpenid().getUserInfo(encryptedData, sessionkey, iv);
		System.out.println("-----------------------------");
		//System.out.println("openid=" + obj.get("openId"));
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	// 微信下单支付
	@ResponseBody
	@RequestMapping("doOrder")
	public void doOrder(int st,HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 得到openid
		String openid = request.getParameter("openid");
		System.out.println(request.getParameter("price"));
		int fee = 0;
		// 得到小程序传过来的价格，注意这里的价格必须为整数，1代表1分，所以传过来的值必须*100；
		if (null != request.getParameter("price")) {
			fee = Integer.parseInt(request.getParameter("price"));
		}
		String body = request.getParameter("body");
		System.out.println(body);
		// 订单编号
		String notify="";
		if(st == 0) {
			notify = "notify";
		}else {
			notify = "notify2";
		}
		String TT_id = request.getParameter("dingdanhao");
		// 订单标题
		String title = "订餐";
		// 时间戳
		String times = System.currentTimeMillis() + "";
		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		packageParams.put("appid", "wx72b0282699ae4078");
		packageParams.put("mch_id", "1552363201");
		packageParams.put("nonce_str", times);// 时间戳
		packageParams.put("body", body);// 支付主体
		packageParams.put("out_trade_no", TT_id);// 商户订单号
		packageParams.put("total_fee", fee);// 价格
		packageParams.put("notify_url", "https://xmj.ccsx.net/food/"+notify);// 支付返回地址
		packageParams.put("trade_type", "JSAPI");// 这个api有，固定的
		packageParams.put("openid", openid);// openid
		// 获取sign
		String sign = PayCommonUtil.createSign("UTF-8", packageParams, "peicanting1234567891234567891234");// 最后这个是自己设置的32位密钥
		packageParams.put("sign", sign);
		// 转成XML
		String requestXML = PayCommonUtil.getRequestXml(packageParams);
		System.out.println(requestXML);
		// 得到含有prepay_id的XML
		String resXml = HttpUtil.postData("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXML);
		System.out.println(resXml);
		// 解析XML存入Map
		Map map = XMLUtil.doXMLParse(resXml);
		System.out.println(map);
		// String return_code = (String) map.get("return_code");
		// 得到prepay_id
		String prepay_id = (String) map.get("prepay_id");
		SortedMap<Object, Object> packageP = new TreeMap<Object, Object>();
		packageP.put("appId", "wx72b0282699ae4078");// ！！！注意，这里是appId,上面是appid，真怀疑写这个东西的人。。。
		packageP.put("nonceStr", times);// 时间戳
		packageP.put("package", "prepay_id=" + prepay_id);// 必须把package写成
															// "prepay_id="+prepay_id这种形式
		packageP.put("signType", "MD5");// paySign加密
		packageP.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
		// 得到paySign
		String paySign = PayCommonUtil.createSign("UTF-8", packageP, "peicanting1234567891234567891234");
		packageP.put("paySign", paySign);
		// 将packageP数据返回给小程序
		Gson gson = new Gson();
		String json = gson.toJson(packageP);
		PrintWriter pw = response.getWriter();
		System.out.println(json);
		pw.write(json);
		pw.close();
	}
	/**
	 * 退款
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	/*
	 * @RequestMapping("refound")
	 * 
	 * @ResponseBody public void refound(String out_trade_no,String total_fee,String
	 * refund_fee, HttpServletRequest request, HttpServletResponse response) throws
	 * IOException { request.setCharacterEncoding("utf-8");
	 * response.setCharacterEncoding("utf-8"); String out_refund_no =
	 * System.currentTimeMillis()+""; String nonce_str =
	 * System.currentTimeMillis()/1000+"";
	 * 
	 * Map map=new WXPayRefundUtil().wechatRefund(out_refund_no, out_trade_no,
	 * total_fee, refund_fee, nonce_str); int a = 0;
	 * if("OK".equals(map.get("return_msg"))) { Orders orders = new Orders();
	 * orders.setOrdernum(out_trade_no); orders.setState("5"); a =
	 * services.updateState(orders); } Gson gson = new Gson(); String json =
	 * gson.toJson(a); PrintWriter pw = response.getWriter(); pw.write(json);
	 * pw.close(); }
	 */

	/**
	 * 付款回调
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("notify")
	@ResponseBody
	public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resXml = "";
		Map<String, String> backxml = new HashMap<String, String>();

		InputStream inStream;
		try {
			inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			System.out.println("微信支付----付款成功----");
			outSteam.close();
			inStream.close();
			String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
			System.out.println("微信支付----result----=" + result);
			Map<Object, Object> map = XMLUtil.doXMLParse(result);

			if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
				System.out.println("微信支付----返回成功");
				// if (verifyWeixinNotify(map)) {
				// 订单处理 操作 orderconroller 的回写操作?
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
				System.out.println("微信支付----验证签名成功");
				// backxml.put("return_code", "<![CDATA[SUCCESS]]>");
				// backxml.put("return_msg", "<![CDATA[OK]]>");
				// // 告诉微信服务器，我收到信息了，不要在调用回调action了
				// strbackxml = pay.ArrayToXml(backxml);
				// response.getWriter().write(strbackxml);
				// logger.error("微信支付 ~~~~~~~~~~~~~~~~执行完毕？backxml=" +
				// strbackxml);

				// ====================================================================
				// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
				

				// 处理业务 -修改订单支付状态
				System.out.println("微信支付回调：修改的订单=" + map.get("out_trade_no"));
				Orders orders = new Orders();
				orders.setOrdernum(map.get("out_trade_no")+"");
				orders.setStatus(1);
				s.updateOrderStatus(orders);
				int b = 0;
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
				while (b==0) {
					
					Orders os = s.findOrderByOrderNum(orders.getOrdernum());
					System.out.println(os.getGoods());
					JSONArray array = JSONArray.fromObject(os.getGoods());
					for (int i = 0; i < array.size(); i++) {
						JSONObject object = JSONObject.fromObject(array.get(i));
						System.out.println(object.get("goods_id"));
						System.out.println(object.get("num"));
						Map<String,Object> amap = s.findGoodsById(Integer.parseInt(object.get("goods_id")+""));
						System.out.println(amap.get("NUM"));
						Map maps = new HashMap<>();
						maps.put("id", object.get("goods_id"));
						maps.put("num", Integer.parseInt(amap.get("NUM")+"")+Integer.parseInt(object.get("num")+""));
						maps.put("nums", Integer.parseInt(amap.get("NUM")+""));
						int a =s.updateGoodsNum(maps);
						System.out.println(a);
						if(a>0) {
							b++;
						}
					}
				}
				// String status = services.findOrderStatus(map.get("out_trade_no").toString());
				/*
				 * if("0".equals(status)||Integer.parseInt(status)==0) { Orders orders= new
				 * Orders(); orders.setOrdernum(map.get("out_trade_no").toString());
				 * orders.setState("1"); int a = services.updateState(orders); // int editres =
				 * Wechat_Order.execute("UPDATE wechat_order SET paystatus =? WHERE orderno=?",
				 * // new Object[] { EnumPayStatus.Paybackok.getValue(), map.get("out_trade_no")
				 * }); if (a > 0) { System.out.println("微信支付回调：修改订单支付状态成功"); } else {
				 * System.out.println("微信支付回调：修改订单支付状态失败"); } }
				 */

				// }
				// ------------------------------
				// 处理业务完毕
				// ------------------------------
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}

	}
	/**
	 * 付款回调
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("notify2")
	@ResponseBody
	public void notify2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resXml = "";
		Map<String, String> backxml = new HashMap<String, String>();

		InputStream inStream;
		try {
			inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			System.out.println("微信支付----付款成功----");
			outSteam.close();
			inStream.close();
			String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
			System.out.println("微信支付----result----=" + result);
			Map<Object, Object> map = XMLUtil.doXMLParse(result);

			if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
				System.out.println("微信支付----返回成功");
				// if (verifyWeixinNotify(map)) {
				// 订单处理 操作 orderconroller 的回写操作?
				System.out.println("微信支付----验证签名成功");
				// backxml.put("return_code", "<![CDATA[SUCCESS]]>");
				// backxml.put("return_msg", "<![CDATA[OK]]>");
				// // 告诉微信服务器，我收到信息了，不要在调用回调action了
				// strbackxml = pay.ArrayToXml(backxml);
				// response.getWriter().write(strbackxml);
				// logger.error("微信支付 ~~~~~~~~~~~~~~~~执行完毕？backxml=" +
				// strbackxml);

				// ====================================================================
				// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

				// 处理业务 -修改订单支付状态
				System.out.println("微信支付回调：修改的订单=" + map.get("out_trade_no"));
				Yorder yorder = new Yorder();
				yorder.setOrdernum(map.get("out_trade_no")+"");
				yorder.setStatus("2");
				s.updateYstatus(yorder);
				int b = 0;
//				while (b==0) {
//					
//					Orders os = s.findOrderByOrderNum(orders.getOrdernum());
//					System.out.println(os.getGoods());
//					JSONArray array = JSONArray.fromObject(os.getGoods());
//					for (int i = 0; i < array.size(); i++) {
//						JSONObject object = JSONObject.fromObject(array.get(i));
//						System.out.println(object.get("goods_id"));
//						System.out.println(object.get("num"));
//						Map<String,Object> amap = s.findGoodsById(Integer.parseInt(object.get("goods_id")+""));
//						System.out.println(amap.get("NUM"));
//						Map maps = new HashMap<>();
//						maps.put("id", object.get("goods_id"));
//						maps.put("num", Integer.parseInt(amap.get("NUM")+"")+Integer.parseInt(object.get("num")+""));
//						maps.put("nums", Integer.parseInt(amap.get("NUM")+""));
//						int a =s.updateGoodsNum(maps);
//						System.out.println(a);
//						if(a>0) {
//							b++;
//						}
//					}
//				}
				// String status = services.findOrderStatus(map.get("out_trade_no").toString());
				/*
				 * if("0".equals(status)||Integer.parseInt(status)==0) { Orders orders= new
				 * Orders(); orders.setOrdernum(map.get("out_trade_no").toString());
				 * orders.setState("1"); int a = services.updateState(orders); // int editres =
				 * Wechat_Order.execute("UPDATE wechat_order SET paystatus =? WHERE orderno=?",
				 * // new Object[] { EnumPayStatus.Paybackok.getValue(), map.get("out_trade_no")
				 * }); if (a > 0) { System.out.println("微信支付回调：修改订单支付状态成功"); } else {
				 * System.out.println("微信支付回调：修改订单支付状态失败"); } }
				 */

				// }
				// ------------------------------
				// 处理业务完毕
				// ------------------------------
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}

	}
	/**
	 * 测试 2019年7月15日11:55:52 范春强
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("test")
	@ResponseBody
	public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.test();
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	/**
	 * 测试 2019年7月15日11:55:52 范春强
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("findGoodsById")
	@ResponseBody
	public void findGoodsById(int id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map map = s.findGoodsById(id);
		System.out.println(map);
		Gson gson = new Gson();
		String json = gson.toJson(map);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	/**
	 * 测试 2019年7月15日11:55:52 范春强
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sendSms")
	@ResponseBody
	public void sendSms(String phone,String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		SmsDemo.sendSms(phone, name);
		//System.out.println();
//		Gson gson = new Gson();
//		String json = gson.toJson(map);
//		PrintWriter pw = response.getWriter();
//		pw.write(json);
//		pw.close();
	}
	/**
	 * 测试 2019年7月15日11:55:52 范春强
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sendSms2")
	@ResponseBody
	public void sendSms2(String phone,String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		SmsDemo.sendSms2(phone, name);
		//System.out.println();
//		Gson gson = new Gson();
//		String json = gson.toJson(map);
//		PrintWriter pw = response.getWriter();
//		pw.write(json);
//		pw.close();
	}
	
	/**
	 * 商品列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("goodsList")
	@ResponseBody
	public void goodsList(HttpServletRequest request, HttpServletResponse response, int row, int column,
			Goods_information goods_information) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		List<Goods_information> a = s.goodsList(goods_information, (row - 1) * column, column);
		int page = 0;
		int count = s.countGoods(goods_information);
		if (count % column == 0) {
			page = count / column;
		} else {
			page = count / column + 1;
		}
		Map map = new HashMap<>();
		map.put("page", page);
		map.put("count", count);
		map.put("list", a);
		System.out.println(map);
		Gson gson = new Gson();
		String json = gson.toJson(map);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 商品添加
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("goodsAdd")
	@ResponseBody
	public void goodsAdd(HttpServletRequest request, HttpServletResponse response, Goods_information goods_information)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		goods_information.setCreate_Time(new Date());
		int a = s.goodsAdd(goods_information);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 图片上传
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("uploadImg")
	@ResponseBody
	public void uploadImg(MultipartFile file, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String img = url + new FileUploadUtils().uploadImgFile(request, file);
		System.out.println(img);
		Gson gson = new Gson();
		String json = gson.toJson(img);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 商品修改
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("goodsUpdate")
	@ResponseBody
	public void goodsUpdate(HttpServletRequest request, HttpServletResponse response,
			Goods_information goods_information) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.goodsUpdate(goods_information);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 商品删除
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("goodsDelete")
	@ResponseBody
	public void goodsDelete(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.goodsDelete(id);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/********************************************/

	/**
	 * 地址信息列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("addressList")
	@ResponseBody
	public void addressList(HttpServletRequest request, HttpServletResponse response, int row, int column,
			String openId) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		List<Address_information> a = s.addressList(openId, row * column, column);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 地址添加
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("addressAdd")
	@ResponseBody
	public void addressAdd(HttpServletRequest request, HttpServletResponse response,
			Address_information address_information) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		address_information.setCreateTime(new Date());
		s.updateAddr(address_information);
		int a = s.addressAdd(address_information);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 地址修改
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("addressUpdate")
	@ResponseBody
	public void addressUpdate(HttpServletRequest request, HttpServletResponse response,
			Address_information address_information) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println(address_information);
		int a = s.addressUpdate(address_information);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 地址修改
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("addressDelete")
	@ResponseBody
	public void addressDelete(HttpServletRequest request, HttpServletResponse response, int id,String openid) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Address_information address_information = new Address_information();
		address_information.setUserId(openid);
		address_information.setDefaultD("2");
		s.updateAddr(address_information);
		int a = s.addressDelete(id);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	/**
	 * 删除地址
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delAddr")
	@ResponseBody
	public void delAddr(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.delAddr(id);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	/**************************************************************/
	/**
	 * 用户个人信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("userList")
	@ResponseBody
	public void userList(HttpServletRequest request, HttpServletResponse response, String id) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		User_personal_information a = s.userList(id);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 用户添加
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("userAdd")
	@ResponseBody
	public void userAdd(HttpServletRequest request, HttpServletResponse response,
			User_personal_information user_personal_information) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		User_personal_information tion = s.userList(user_personal_information.getId());
		int a= 0;
		if(tion == null) {
			user_personal_information.setCreateTime(new Date());
			a= s.userAdd(user_personal_information);
		}else {
			a=2;
		}
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 用户修改
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("userUpdate")
	@ResponseBody
	public void userUpdate(HttpServletRequest request, HttpServletResponse response,
			User_personal_information user_personal_information) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.userUpdate(user_personal_information);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 添加
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("insertType")
	@ResponseBody
	public void insertType(HttpServletRequest request, HttpServletResponse response, String types) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		List<Map<String, Object>> list = s.findTypes();
		Map map = new HashMap<>();
		System.out.println("kkkkkkkkkkkkk"+list.size());
		if (list.size() == 0) {
			map.put("types", types);
			map.put("px", 1);
			
		} else {
			map.put("types", types);
			map.put("px", Integer.parseInt(list.get(list.size()-1).get("px") + "") + 1);
		}
		System.out.println("111111111111111111111111111111");
		System.out.println(map);
		int a = s.insertType(map);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("delTypes")
	@ResponseBody
	public void delTypes(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int a = s.delTypes(id);
		System.out.println(a);
		Gson gson = new Gson();
		String json = gson.toJson(a);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("findTypes")
	@ResponseBody
	public void findTypes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		List<Map<String, Object>> list = s.findTypes();
		System.out.println(list);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	// 分页查询订单
	@RequestMapping("fenyeOrder")
	@ResponseBody
	public void fenyeOrder(int start, int end, int types,String dates, int status, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map map = new HashMap();
		map.put("start", (start - 1) * end);
		map.put("end", end);
		map.put("types", types);
		map.put("status", status);
		map.put("dates", "%"+dates+"%");
		List list = s.fenyeOrder(map);
		int a = s.countOrder(map);
		int page = 0;
		if (a % end == 0) {
			page = a / end;
		} else {
			page = a / end + 1;
		}
		Map maps = new HashMap<>();
		maps.put("page", page);
		maps.put("count", a);
		maps.put("list", list);
		System.out.println(maps);
		Gson gson = new Gson();
		String json = gson.toJson(maps);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("insertOrder")
	@ResponseBody
	public void insertOrder(Orders orders, HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		String ordernum = System.currentTimeMillis()+"";
		orders.setOrdernum(ordernum);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orders.setDates(format.format(new Date()));
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println(orders);
		int list = s.insertOrder(orders);
		System.out.println(ordernum);
		if(list==0) {
			ordernum="fail";
		}
		Gson gson = new Gson();
		String json = gson.toJson(ordernum);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}

	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("updateOrderStatus")
	@ResponseBody
	public void updateOrderStatus(Orders orders, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		
		int list = s.updateOrderStatus(orders);
		
		int b = 0;
//		if(orders.getTypes()!=1) {
//		while (b==0) {
//			
//			Orders os = s.findOrderByOrderNum(orders.getOrdernum());
//			System.out.println(os.getGoods());
//			JSONArray array = JSONArray.fromObject(os.getGoods());
//			for (int i = 0; i < array.size(); i++) {
//				JSONObject object = JSONObject.fromObject(array.get(i));
//				System.out.println(object.get("goods_id"));
//				System.out.println(object.get("num"));
//				Map<String,Object> map = s.findGoodsById(Integer.parseInt(object.get("goods_id")+""));
//				System.out.println(map.get("NUM"));
//				Map maps = new HashMap<>();
//				maps.put("id", object.get("goods_id"));
//				maps.put("num", Integer.parseInt(map.get("NUM")+"")+Integer.parseInt(object.get("num")+""));
//				maps.put("nums", Integer.parseInt(map.get("NUM")+""));
//				int a =s.updateGoodsNum(maps);
//				System.out.println(a);
//				if(a>0) {
//					b++;
//				}
//			}
//		}
//		
//		}
		System.out.println(list);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	/**
	 * 查询默认地址
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("findAddrByD")
	@ResponseBody
	public void findAddrByD(String openid, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map information = s.findAddrByD(openid);
		System.out.println(information);
		Gson gson = new Gson();
		String json = gson.toJson(information);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	/**
	 * 查询订单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("findOrderByOpenid")
	@ResponseBody
	public void findOrderByOpenid(String openid, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		List<Orders> list = s.findOrderByOpenid(openid);
		System.out.println(list);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	/**
	 * 查询订单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("findOrderByOrderNum")
	@ResponseBody
	public void findOrderByOrderNum(String ordernum, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Orders order = s.findOrderByOrderNum(ordernum);
		System.out.println(order);
		Gson gson = new Gson();
		String json = gson.toJson(order);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	/**
	 * 添加营养师订单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("insertYorder")
	@ResponseBody
	public void insertYorder(Yorder orders, HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		String ordernum = System.currentTimeMillis()+"";
		orders.setOrdernum(ordernum);
		orders.setDates(new Date());
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println(orders);
		int list = s.insertYorder(orders);
		System.out.println(ordernum);
		if(list==0) {
			ordernum="fail";
		}
		Gson gson = new Gson();
		String json = gson.toJson(ordernum);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("updateYorder")
	@ResponseBody
	public void updateYorder(Yorder orders, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int list = s.updateYorder(orders);
		if(list>0) {
			SmsDemo.sendSms(orders.getAddrphone(), orders.getAddrname());
		}
		System.out.println(list);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("updateYstatus")
	@ResponseBody
	public void updateYstatus(Yorder orders, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * public void goodsList(HttpServletRequest request, HttpServletResponse
		 * response) throws Exception {
		 */
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		int list = s.updateYstatus(orders);
		System.out.println(list);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		PrintWriter pw = response.getWriter();
		pw.write(json);
		pw.close();
	}
	
	// 分页查询订单
		@RequestMapping("fenyeYorder")
		@ResponseBody
		public void fenyeYorder(int start, int end,int status, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap();
			map.put("start", (start - 1) * end);
			map.put("end", end);
			map.put("status", status);
			List list = s.fenyeYorder(map);
			int a = s.countYorder(map);
			int page = 0;
			if (a % end == 0) {
				page = a / end;
			} else {
				page = a / end + 1;
			}
			Map maps = new HashMap<>();
			maps.put("page", page);
			maps.put("count", a);
			maps.put("list", list);
			System.out.println(maps);
			Gson gson = new Gson();
			String json = gson.toJson(maps);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询订单
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findYorderByOpenid")
		@ResponseBody
		public void findYorderByOpenid(String openid, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			List<Yorder> list = s.findYorderByOpenid(openid);
			System.out.println(list);
			Gson gson = new Gson();
			String json = gson.toJson(list);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询订单
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findYorderByOrdernum")
		@ResponseBody
		public void findYorderByOrdernum(String ordernum, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Yorder order = s.findYorderByOrdernum(ordernum);
			System.out.println(order);
			Gson gson = new Gson();
			String json = gson.toJson(order);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findAdminById")
		@ResponseBody
		public void findAdminById(int id, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Admin admin = s.findAdminById(id);
			System.out.println(admin);
			Gson gson = new Gson();
			String json = gson.toJson(admin);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findAdminByPhone")
		@ResponseBody
		public void findAdminByPhone(String phone, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Admin admin = s.findAdminByPhone(phone);
			System.out.println(admin);
			Gson gson = new Gson();
			String json = gson.toJson(admin);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("adminLogin")
		@ResponseBody
		public void adminLogin(Admin a, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Admin admin = s.adminLogin(a);
			System.out.println(admin);
			Gson gson = new Gson();
			String json = gson.toJson(admin);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("insertAdmin")
		@ResponseBody
		public void insertAdmin(Admin admin, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Admin admis = s.findAdminByPhone(admin.getPhone());
			int a =0;
			if(admis==null) {
				
				a= s.insertAdmin(admin);
			}else {
				a=2;
			}
			System.out.println(a);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("updateAdmin")
		@ResponseBody
		public void updateAdmin(Admin admin, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			int a = s.updateAdmin(admin);
			System.out.println(a);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询管理员
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("delAdmin")
		@ResponseBody
		public void delAdmin(int id, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			int a = s.delAdmin(id);
			System.out.println(a);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		@RequestMapping("fenyeAdmin")
		@ResponseBody
		public void fenyeAdmin(int start, int end, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap();
			map.put("start", (start - 1) * end);
			map.put("end", end);
			List list = s.fenyeAdmin(map);
			int a = s.countAdmin(map);
			int page = 0;
			if (a % end == 0) {
				page = a / end;
			} else {
				page = a / end + 1;
			}
			Map maps = new HashMap<>();
			maps.put("page", page);
			maps.put("count", a);
			maps.put("list", list);
			System.out.println(maps);
			Gson gson = new Gson();
			String json = gson.toJson(maps);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 查询公司信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findInfo")
		@ResponseBody
		public void findInfo(HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = s.findInfo();
			Gson gson = new Gson();
			String json = gson.toJson(map);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		/**
		 * 修改
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("update")
		@ResponseBody
		public void update(String addr,String times,String tel,String gonggao,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap<>();
			map.put("addr", addr);
			map.put("times", times);
			map.put("tel", tel);
			map.put("gonggao", gonggao);
			int a = s.update(map);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 添加周商品
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("insertTypeGood")
		@ResponseBody
		public void insertTypeGood(int tid,int gid,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap<>();
			map.put("tid", tid);
			map.put("gid", gid);
			int b = s.countTG(map);
			int a=0;
			if(b>0) {
				a=2;
			}else {
				a = s.insertTypeGood(map);
			}
			
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		@RequestMapping("findTypeGood")
		@ResponseBody
		public void findTypeGood(int start, int end,int status,int tid,HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap();
			map.put("start", (start - 1) * end);
			map.put("end", end);
			map.put("status", status);
			map.put("tid", tid);
			System.out.println(map);
			List list = s.findTypeGood(map);
			int a = s.countfindTypeGood(map);
			int page = 0;
			if (a % end == 0) {
				page = a / end;
			} else {
				page = a / end + 1;
			}
			Map maps = new HashMap<>();
			maps.put("page", page);
			maps.put("count", a);
			maps.put("list", list);
			System.out.println(maps);
			Gson gson = new Gson();
			String json = gson.toJson(maps);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 验证周商品
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("countTG")
		@ResponseBody
		public void countTG(int tid,int gid,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap<>();
			map.put("tid", tid);
			map.put("gid", gid);
			int a = s.countTG(map);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 删除周商品
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("delTypeGood")
		@ResponseBody
		public void delTypeGood(int id,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
//			Map map = new HashMap<>();
//			map.put("tid", tid);
//			map.put("gid", gid);
			int a = s.delTypeGood(id);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 上下架周商品
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("updateTypeGood")
		@ResponseBody
		public void updateTypeGood(int id,int status,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Map map = new HashMap<>();
			map.put("id", id);
			map.put("status", status);
			int a = s.updateTypeGood(map);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 删除订单
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("delOrderById")
		@ResponseBody
		public void delOrderById(int id,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			int a = s.delOrderById(id);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		/**
		 * 生成excel
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("downloadExcel")
		@ResponseBody
		public void downloadExcel(String dates, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			dates = "%"+dates+"%";
			System.out.println(dates);
			List<Orders> list = s.findAllOrderUseExcel(dates);
			System.out.println(list);
			String code="200";
			Map map = new HashMap<>();
			if(list.size()==0) {
				code="201";
				map.put("code", code);
				map.put("data", "nocode");
			}else {
				String urls=Excels.testExcel6(list);
				code="200";
				map.put("code", code);
				map.put("data", urls);
			}
			Gson gson = new Gson();
			String json = gson.toJson(map);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}

		/**
		 * 打印订单
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("printOrderById")
		@ResponseBody
		public void printOrderById(String ordernum,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			//int a = s.delOrderById(id);
			Orders orders = s.findOrderByOrderNum(ordernum);
			System.out.println(orders);
			SalesTicket ticket= new SalesTicket(orders);
			//System.out.println(ticket);
			Gson gson = new Gson();
			String json = gson.toJson(1);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		
		/**
		 * 修改介绍
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("updateJieshao")
		@ResponseBody
		public void updateJieshao(String name,String img,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			//int a = s.delOrderById(id);
			Map map = new HashMap<>();
			map.put("name", name);
			map.put("img", img);
			int a = s.updateJieshao(map);
			//System.out.println(ticket);
			Gson gson = new Gson();
			String json = gson.toJson(a);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
		
		
		/**
		 * 查询介绍
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping("findJisahao")
		@ResponseBody
		public void findJisahao(HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			/*
			 * public void goodsList(HttpServletRequest request, HttpServletResponse
			 * response) throws Exception {
			 */
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			//int a = s.delOrderById(id);
			Map map = new HashMap<>();
			map=s.findJisahao();
			//System.out.println(ticket);
			Gson gson = new Gson();
			String json = gson.toJson(map);
			PrintWriter pw = response.getWriter();
			pw.write(json);
			pw.close();
		}
}
