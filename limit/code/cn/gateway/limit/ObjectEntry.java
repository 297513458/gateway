package cn.gateway.limit;

import java.util.concurrent.atomic.AtomicInteger;

public class ObjectEntry {
	AtomicInteger errorCount;
	AtomicInteger successCount;
	AtomicInteger totalCount;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("t:").append(successCount.get()).append(" f:").append(errorCount.get()).toString();
		return sb.toString();
	}

	public AtomicInteger getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(AtomicInteger errorCount) {
		this.errorCount = errorCount;
	}

	public AtomicInteger getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(AtomicInteger successCount) {
		this.successCount = successCount;
	}

	public AtomicInteger getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(AtomicInteger totalCount) {
		this.totalCount = totalCount;
	}
}