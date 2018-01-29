package cn.gateway.manager.controller;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import cn.gateway.core.common.MessageDTO;
import cn.gateway.manager.common.ResultJson;
import cn.gateway.manager.service.AdminService;
import cn.gateway.manger.pojo.Admin;

@Controller
public class AdminController {
	protected static final Logger log = LogManager.getLogger(AdminController.class);
	@Resource
	private AdminService adminService;

	@ResponseBody
	@RequestMapping("/admin/login")
	public String login(@RequestBody String body) throws Exception {
		Admin vo = JSON.parseObject(body, new TypeReference<MessageDTO<Admin>>() {
		}).getData();
		return ResultJson.toJSONString(this.adminService.login(vo.getName(), vo.getPassword()));
	}
}