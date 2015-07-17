package org.zalando.catwatch.backend.web.config;

import static java.util.Collections.singletonMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigController {

	@Autowired
	private Environment env;

	@RequestMapping(value = "/config", method = GET, produces = { APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map<String, String> config() {
		String orgaListParam = "organization.list";
		return singletonMap(orgaListParam, env.getProperty(orgaListParam));
	}

}
