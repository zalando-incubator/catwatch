package org.zalando.catwatch.backend.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.zalando.catwatch.backend.util.JavaScriptExecutor.newExecutor;

import org.junit.Test;

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
