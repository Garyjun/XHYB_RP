package com.brainsoon.crawler.core.support;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.utils.NumberUtils;

/**
 * 爬取深度定制
 *
 * Created by andlu on 2015/10/24.
 */
public class DepthScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	public static final int INITIAL_CAPACITY = 3;

	private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();

	private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY,
			new Comparator<Request>() {
				@Override
				public int compare(Request o1, Request o2) {
					return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
				}
			});

	private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY,
			new Comparator<Request>() {
				@Override
				public int compare(Request o1, Request o2) {
					return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
				}
			});

	@Override
	public void pushWhenNoDuplicate(Request request, Task task) {
		if (request.getPriority() == 0) {
			noPriorityQueue.add(request);
		} else if (request.getPriority() > 0) {
			priorityQueuePlus.put(request);
		} else {
			priorityQueueMinus.put(request);
		}
	}

	@Override
	public synchronized Request poll(Task task) {
		Request poll = priorityQueuePlus.poll();
		if (poll != null) {
			return poll;
		}
		poll = noPriorityQueue.poll();
		if (poll != null) {
			return poll;
		}
		return priorityQueueMinus.poll();
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return noPriorityQueue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return getDuplicateRemover().getTotalRequestsCount(task);
	}
}
