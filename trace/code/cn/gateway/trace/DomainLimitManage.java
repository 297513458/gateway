package cn.gateway.limit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.gateway.logger.Logger;
import cn.gateway.logger.LoggerFactory;

public class DomainLimitManage {
	private static Logger log = LoggerFactory.getLogger(DomainLimitManage.class);

	public static void notify(String url, boolean code) {
		String command = command(url);
		if (command == null || command.trim().length() == 0)
			return;
		LimitManage.notify(command, code);
	}

	public static boolean enable(String url) {
		String command = command(url);
		if (command == null || command.trim().length() == 0)
			return false;
		boolean result = LimitManage.enable(command);
		if (log.isInfoEnabled()) {
			LimitCommand comd = LimitManage.getLimitCommand(command);
			StringBuilder sb = new StringBuilder();
			if (result) {
				sb.append("allow:");
			} else {
				sb.append("forbid:");
			}
			sb.append(command);
			if (comd != null) {
				sb.append("\tstatus:").append(comd.getCounter().getStatus());
				sb.append("\trules:").append(comd.getCounter().getCount()).append("/")
						.append(comd.getCounter().getSecond());
			}
			sb.append("\trequest:").append(url);
			if (result) {
				log.info(sb.toString());
			}
		}
		return result;
	}

	static Pattern p = Pattern.compile("(?<=(http|https|ftp)://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)",
			Pattern.CASE_INSENSITIVE);

	public static String command(String url) {
		if (url == null || url.trim().length() == 0) {
			return null;
		}
		Matcher matcher = p.matcher(url);
		if (matcher.find())
			return matcher.group();
		else
			return null;
	}

}