package cn.gateway.manager.service;

import java.util.List;

import cn.gateway.manager.pojo.Scheduling;
import cn.gateway.pojo.Node;

public interface SchedulingService {
	public List<Node> listDeployTask(String platform);

	public List<Scheduling> queryByClass(Scheduling clazz);

	public List<Node> listRegisteringTask(String platform);

	public Scheduling query(String clazz, String method);

	/**
	 * 修改状态
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean updateStatus(Scheduling entity);

	/**
	 * 修改状态
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean save(Scheduling entity);

	/**
	 * 发布
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean updateDeploy(Scheduling entity);
	public Node getDeployScheduling(String clazz, String method) ;
	/**
	 * 修改同步状态
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean updateSync(Scheduling entity);

	/**
	 * 删除
	 * 
	 * @param entity
	 * @return
	 */
	public Boolean delete(Scheduling entity);
}