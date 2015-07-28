package org.zalando.catwatch.backend.web.fetch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.catwatch.backend.scheduler.TaskScheduler;

@Controller
public class FetchController {

	@Autowired
	private TaskScheduler taskScheduler;

	@RequestMapping(value = "/fetch", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String fetch() throws Exception {
		taskScheduler.fetchData();
		return "OK";
	}

}
