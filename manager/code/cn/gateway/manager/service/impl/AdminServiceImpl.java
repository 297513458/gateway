package cn.gateway.manager.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.gateway.core.common.KeyUtil;
import cn.gateway.manager.service.AdminService;
import cn.gateway.manager.service.TokenService;
import cn.gateway.manger.dao.AdminDAO;
import cn.gateway.manger.pojo.Admin;
import cn.gateway.manger.pojo.AdminVO;

@Service("adminService")
public class AdminServiceImpl implements AdminService {
	@Resource
	private AdminDAO adminDAO;
	@Resource
	private TokenService tokenService;

	@Override
	public Admin login(String name, String password) {
		Admin vo = new Admin();
		if (password != null) {
			vo.setName(name);
			vo.setPassword(password);
			password = KeyUtil.passwordEncrypt(JSON.toJSONString(vo));
		}
		AdminVO admin = this.adminDAO.queryByNameAndPassword(name, password);
		if (admin != null) {
			admin.setPassword(null);
			tokenService.saveToken(admin);
			return admin;

		} else
			return null;
	}

	@Override
	public Boolean updateStatus(Admin entity) {
		return this.updateStatus(entity);
	}
}