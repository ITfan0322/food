package com.xbboom.service.Impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xbboom.bean.Address_information;
import com.xbboom.bean.Admin;
import com.xbboom.bean.Goods_information;
import com.xbboom.bean.Orders;
import com.xbboom.bean.User_personal_information;
import com.xbboom.bean.Yorder;
import com.xbboom.dao.XbDao;
import com.xbboom.service.XbService;

@Service
public class XbServiceImpl implements XbService{

	private XbDao dao;

	 public XbDao getDao() {
	  return dao;
	 }
	 @Resource
	 public void setDao(XbDao dao) {
	  this.dao = dao;
	 }
	 @Override
	 public int test() {
	  // TODO Auto-generated method stub
	  return dao.test();
	 }
	@Override
	public List<Goods_information> goodsList(Goods_information goods_information, int row, int column) {
		// TODO Auto-generated method stub
		
		return dao.goodsList(goods_information,row,column);
	}
	@Override
	public int goodsAdd(Goods_information goods_information) {
		// TODO Auto-generated method stub
		return dao.goodsAdd(goods_information);
	}
	@Override
	public int goodsUpdate(Goods_information goods_information) {
		// TODO Auto-generated method stub
		return dao.goodsUpdate(goods_information);
	}
	@Override
	public int goodsDelete(int ID) {
		// TODO Auto-generated method stub
		return dao.goodsDelete(ID);
	}
	@Override
	public List<Address_information> addressList(String openId,int row, int column) {
		// TODO Auto-generated method stub
		return dao.addressList(openId,row,column);
	}
	@Override
	public int addressAdd(Address_information address_information) {
		// TODO Auto-generated method stub
		return dao.addressAdd(address_information);
	}
	@Override
	public int addressUpdate(Address_information address_information) {
		// TODO Auto-generated method stub
		return dao.addressUpdate(address_information);
	}
	@Override
	public int addressDelete(int ID) {
		// TODO Auto-generated method stub
		return dao.addressDelete(ID);
	}
	@Override
	public User_personal_information userList(String ID) {
		// TODO Auto-generated method stub
		return dao.userList(ID);
	}
	@Override
	public int userAdd(User_personal_information user_personal_information) {
		// TODO Auto-generated method stub
		return dao.userAdd(user_personal_information);
	}
	@Override
	public int userUpdate(User_personal_information user_personal_information) {
		// TODO Auto-generated method stub
		return dao.userUpdate(user_personal_information);
	}
	@Override
	public int countGoods(Goods_information goods_information) {
		// TODO Auto-generated method stub
		return dao.countGoods(goods_information);
	}
	@Override
	public int insertType(Map map) {
		// TODO Auto-generated method stub
		return dao.insertType(map);
	}
	@Override
	public List<Map<String,Object>> findTypes() {
		// TODO Auto-generated method stub
		return dao.findTypes();
	}
	@Override
	public int delTypes(int id) {
		// TODO Auto-generated method stub
		return dao.delTypes(id);
	}
	@Override
	public int updateTypes(Map map) {
		// TODO Auto-generated method stub
		return dao.updateTypes(map);
	}
	@Override
	public int insertOrder(Orders orders) {
		// TODO Auto-generated method stub
		return dao.insertOrder(orders);
	}
	@Override
	public int updateOrderStatus(Orders orders) {
		// TODO Auto-generated method stub
		return dao.updateOrderStatus(orders);
	}
	@Override
	public List<Orders> fenyeOrder(Map map) {
		// TODO Auto-generated method stub
		return dao.fenyeOrder(map);
	}
	@Override
	public int countOrder(Map map) {
		// TODO Auto-generated method stub
		return dao.countOrder(map);
	}
	@Override
	public int updateAddr(Address_information address_information) {
		// TODO Auto-generated method stub
		return dao.updateAddr(address_information);
	}
	@Override
	public int delAddr(int id) {
		// TODO Auto-generated method stub
		return dao.delAddr(id);
	}
	@Override
	public Map findAddrByD(String openid) {
		// TODO Auto-generated method stub
		return dao.findAddrByD(openid);
	}
	@Override
	public List<Orders> findOrderByOpenid(String openid) {
		// TODO Auto-generated method stub
		return dao.findOrderByOpenid(openid);
	}
	@Override
	public Orders findOrderByOrderNum(String ordernum) {
		// TODO Auto-generated method stub
		return dao.findOrderByOrderNum(ordernum);
	}
	@Override
	public int insertYorder(Yorder yorder) {
		// TODO Auto-generated method stub
		return dao.insertYorder(yorder);
	}
	@Override
	public int updateYorder(Yorder yorder) {
		// TODO Auto-generated method stub
		return dao.updateYorder(yorder);
	}
	@Override
	public int updateYstatus(Yorder yorder) {
		// TODO Auto-generated method stub
		return dao.updateYstatus(yorder);
	}
	@Override
	public List<Yorder> fenyeYorder(Map map) {
		// TODO Auto-generated method stub
		return dao.fenyeYorder(map);
	}
	@Override
	public int countYorder(Map map) {
		// TODO Auto-generated method stub
		return dao.countYorder(map);
	}
	@Override
	public List<Yorder> findYorderByOpenid(String openid) {
		// TODO Auto-generated method stub
		return dao.findYorderByOpenid(openid);
	}
	@Override
	public Yorder findYorderByOrdernum(String ordernum) {
		// TODO Auto-generated method stub
		return dao.findYorderByOrdernum(ordernum);
	}
	@Override
	public Map<String,Object> findGoodsById(int id) {
		// TODO Auto-generated method stub
		return dao.findGoodsById(id);
	}
	@Override
	public int insertAdmin(Admin admin) {
		// TODO Auto-generated method stub
		return dao.insertAdmin(admin);
	}
	@Override
	public int updateAdmin(Admin admin) {
		// TODO Auto-generated method stub
		return dao.updateAdmin(admin);
	}
	@Override
	public List fenyeAdmin(Map map) {
		// TODO Auto-generated method stub
		return dao.fenyeAdmin(map);
	}
	@Override
	public int countAdmin(Map map) {
		// TODO Auto-generated method stub
		return dao.countAdmin(map);
	}
	@Override
	public Admin findAdminById(int id) {
		// TODO Auto-generated method stub
		return dao.findAdminById(id);
	}
	@Override
	public int delAdmin(int id) {
		// TODO Auto-generated method stub
		return dao.delAdmin(id);
	}
	@Override
	public Admin adminLogin(Admin admin) {
		// TODO Auto-generated method stub
		return dao.adminLogin(admin);
	}
	@Override
	public Admin findAdminByPhone(String phone) {
		// TODO Auto-generated method stub
		return dao.findAdminByPhone(phone);
	}
	@Override
	public int updateGoodsNum(Map map) {
		// TODO Auto-generated method stub
		return dao.updateGoodsNum(map);
	}
	@Override
	public Map findInfo() {
		// TODO Auto-generated method stub
		return dao.findInfo();
	}
	@Override
	public int update(Map map) {
		// TODO Auto-generated method stub
		return dao.update(map);
	}
	@Override
	public int insertTypeGood(Map map) {
		// TODO Auto-generated method stub
		return dao.insertTypeGood(map);
	}
	@Override
	public List<Map<String, String>> findTypeGood(Map map) {
		// TODO Auto-generated method stub
		return dao.findTypeGood(map);
	}
	@Override
	public int countfindTypeGood(Map map) {
		// TODO Auto-generated method stub
		return dao.countfindTypeGood(map);
	}
	@Override
	public int delTypeGood(int id) {
		// TODO Auto-generated method stub
		return dao.delTypeGood(id);
	}
	@Override
	public int updateTypeGood(Map map) {
		// TODO Auto-generated method stub
		return dao.updateTypeGood(map);
	}
	@Override
	public int countTG(Map map) {
		// TODO Auto-generated method stub
		return dao.countTG(map);
	}
	@Override
	public int delOrderById(int id) {
		// TODO Auto-generated method stub
		return dao.delOrderById(id);
	}
	@Override
	public List<Orders> findAllOrderUseExcel(String dates) {
		// TODO Auto-generated method stub
		return dao.findAllOrderUseExcel(dates);
	}
	@Override
	public int updateJieshao(Map map) {
		// TODO Auto-generated method stub
		return dao.updateJieshao(map);
	}
	@Override
	public Map findJisahao() {
		// TODO Auto-generated method stub
		return dao.findJisahao();
	}


}
