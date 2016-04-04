package org.zalando.catwatch.backend.util;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.zalando.catwatch.backend.util.JavaScriptExecutor.newExecutor;

public class JavaScriptExecutorTest {

	@Test
	public void testExecutor() throws Exception {
		assertThat(
				newExecutor("var c = '!'; result.value = a + ' ' + b + c") //
						.bind("a", "Hello") //
						.bind("b", "world") //
						.execute(), //
				equalTo("Hello world!"));
	}

}
