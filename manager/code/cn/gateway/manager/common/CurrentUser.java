package cn.gateway.manager.common;

import cn.gateway.manager.pojo.AdminVO;
/**
 * 当前用户信息
 * 
 * @author Administrator
 *
 */
public class CurrentUser {
	private static final ThreadLocal<AdminVO> CURRENTTUSER = new ThreadLocal<AdminVO>();

	public static void set(AdminVO user) {
		CURRENTTUSER.set(user);
	}

	public static void remove() {
		CURRENTTUSER.remove();
	}

	public static AdminVO get() {
		return CURRENTTUSER.get();
	}
}