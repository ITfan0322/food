package com.xbboom.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xbboom.bean.Address_information;
import com.xbboom.bean.Admin;
import com.xbboom.bean.Goods_information;
import com.xbboom.bean.Orders;
import com.xbboom.bean.User_personal_information;
import com.xbboom.bean.Yorder;

@Repository
public interface XbDao {
	public int test();
	
	/**
	 * 商品列表
	 * @return
	 */
	public List<Goods_information>  goodsList(@Param("goods_information")Goods_information goods_information,@Param("row")int row,@Param("column")int column);
	
	//商品统计
	public int countGoods(@Param("goods_information")Goods_information goods_information);
	/**
	 * 添加商品
	 * @return
	 */
	public int goodsAdd(Goods_information goods_information);
	/**
	 * 修改商品
	 * @return
	 */
	public int goodsUpdate(Goods_information goods_information);
	/**
	 * 删除商品
	 * @return
	 */
	public int goodsDelete(int ID);
	
	/**
	 * 查看商品
	 * @return
	 */
	public Map<String,Object> findGoodsById(int id);
	/*************************************************************************************/
	/**
	 * 地址信息列表
	 * @return
	 */
	public List<Address_information> addressList(@Param("openId")String openId,@Param("row")int row,@Param("column")int column);
	/**
	 * 地址添加
	 * @return
	 */
	public int addressAdd(Address_information address_information);
	
	//删除地址
	public int delAddr(int id);
	/**
	 * 地址修改
	 * @return
	 */
	public int addressUpdate(Address_information address_information);
	//更改默认地址
	public int updateAddr(Address_information address_information);
	/**
	 * 地址删除
	 * @return
	 */
	public int addressDelete(int ID);
	
	
	/**************************************************************************************/
	/**
	 * 用户信息
	 * @param ID
	 * @return
	 */
	public User_personal_information userList(String ID);
	/**
	 * 用户添加
	 * @param ID
	 * @return
	 */
	public int userAdd(User_personal_information user_personal_information);
	/**
	 * 用户修改
	 * @param ID
	 * @return
	 */
	public int userUpdate(User_personal_information user_personal_information);
	
	//添加分类
	public int insertType(Map map);
	//查询分类
	public List<Map<String,Object>> findTypes();
	
	//删除分类
	public int delTypes(int id);
	
	//更新分类
	public int updateTypes(Map map);
	
	//添加订单
	public int insertOrder(Orders orders);
	
	//修改订单状态
	public int updateOrderStatus(Orders orders);
	
	//分页查询订单
	public List<Orders> fenyeOrder(Map map);
	
	//统计订单
	public int countOrder(Map map);
	//查询默认地址
	public Map findAddrByD(String openid);
	
	//查询订单
	public List<Orders> findOrderByOpenid(String openid);
	
	//查询订单
	public Orders findOrderByOrderNum(String ordernum);
	
	//添加营养师订单
	public int insertYorder(Yorder yorder);
	
	//修改营养师订单
	public int updateYorder(Yorder yorder);
	
	//修改订单状态
	public int updateYstatus(Yorder yorder);
	
	//分页查询
	public List<Yorder> fenyeYorder(Map map);
	
	//统计
	public int countYorder(Map map);
	
	//查询个人订单
	public List<Yorder> findYorderByOpenid(String openid);
	
	//查看订单
	public Yorder findYorderByOrdernum(String ordernum);
	
	//插入管理员
	public int insertAdmin(Admin admin);
	
	//修改管理员
	public int updateAdmin(Admin admin);
	
	//分页查询管理员
	public List fenyeAdmin(Map map);
	
	//统计管理员
	public int countAdmin(Map map);
	
	//根据id查询管理员
	public Admin findAdminById(int id);
	//根据手机号
	public Admin findAdminByPhone(String phone);
	//管理员登录
	public Admin adminLogin(Admin admin);
	//删除管理员
	public int delAdmin(int id);
	
	//修改商品销量
	public int updateGoodsNum(Map map);
	
	
	//查询公司信息
	public Map findInfo();
	
	//修改公司信息
	public int update(Map map);
	//按周添加商品
	public int insertTypeGood(Map map);
	
	//判断周商品
	public int countTG(Map map);
	
	//按周查询商品
	public List<Map<String, String>> findTypeGood(Map map);
	
	//按周统计
	public int countfindTypeGood(Map map);
	
	//删除周商品
	public int delTypeGood(int id);
	
	//修改周商品状态
	public int updateTypeGood(Map map);
	
	//删除订单
	public int delOrderById(int id);
	
	//根据日期查询订单
	public List<Orders> findAllOrderUseExcel(String dates);
	
	//修改介绍
	public int updateJieshao(Map map);
	
	//查询介绍
	public Map findJisahao();
	
}
