package cn.gateway.watcher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;;

public class LoggerEvent extends ApplicationContextEvent {

	public LoggerEvent(ApplicationContext source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
