package cn.gateway.limit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Test {

	public static void main(String[] args) throws Exception {
		ExecutorService es = Executors.newFixedThreadPool(100);
		Semaphore s = new Semaphore(100);
		List<String> us = new ArrayList<>();
		for (int t = 0; t < 100; t++) {
			us.add("http://www.sb" + t + ".com");
		}
		while (true) {
			boolean bs = new Random().nextInt(100) > 4;
			for (String sts : us) {
				DomainLimitManage.enable(sts);
				DomainLimitManage.notify(sts, bs);
			}
		}
	}
}