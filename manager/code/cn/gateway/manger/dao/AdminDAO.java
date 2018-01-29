package cn.gateway.manger.dao;

import org.springframework.stereotype.Repository;

import cn.gateway.core.page.SuperDAO;
import cn.gateway.manger.pojo.Admin;
import cn.gateway.manger.pojo.AdminVO;

@Repository("adminDAO")
public class AdminDAO extends SuperDAO<Admin, String> {

	@Override
	protected String namespace() {
		return "mybatis.xml.AdminMapper";
	}

	public AdminVO queryByNameAndPassword(String name, String password) {
		AdminVO vo = new AdminVO();
		vo.setName(name);
		vo.setPassword(password);
		return this.getSqlSession().selectOne(this.tip("selectByNameAndPassword"), vo);
	}

	public int updateStatus(Admin entity) {
		if (entity != null && entity.getStatus() == null) {
			entity.setStatus(0);
			return this.getSqlSession().update(this.tip("updateStatus"), entity);
		} else
			return 0;
	}
}