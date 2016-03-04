package org.zalando.catwatch.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.google.common.base.Joiner.on;
import static java.util.Arrays.stream;

@Component
@ConditionalOnProperty("debug")
public class DebugBean {

	private final Environment env;
	private final ApplicationContext context;

	@Autowired
	public DebugBean(Environment env, ApplicationContext context) {
		this.env = env;
		this.context = context;
	}

	@PostConstruct
	public void postConstruct() {
			System.out.println("================== " + DebugBean.class.getName() + "=================================");
			System.out.println("active  profiles: " + on(",").join(env.getActiveProfiles()));
			System.out.println("default profiles: " + on(",").join(env.getDefaultProfiles()));
			System.out
					.println("spring.database.driverClassName: " + env.getProperty("spring.database.driverClassName"));
			System.out.println("spring.jpa.hibernate.ddl-auto: " + env.getProperty("spring.jpa.hibernate.ddl-auto"));
			System.out.println("all beans:");
			stream(context.getBeanDefinitionNames()).forEach(System.out::println);
			System.out.println("================== " + DebugBean.class.getName() + "=================================");
	}

}
