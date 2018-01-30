package cn.gateway.manager.service;

import cn.gateway.manager.pojo.Admin;

public interface AdminService {

	/**
	 * 登录
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public Admin login(String name, String password);

	/**
	 * 修改状态
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean updateStatus(Admin entity);

}