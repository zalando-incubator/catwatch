package org.zalando.catwatch.backend.repo.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.temporal.ChronoUnit.DAYS;

public class BuilderUtil {

	private static LinkedList<String> projectNames;

	private static AtomicInteger latestId = new AtomicInteger(1);

	public static int random(int start, int end) {
		return (int) Math.round(Math.random() * (end - start - 1)) + start;
	}

	public static Date randomDate() {
		return Date.from(Instant.now().minus(random(1, 356), DAYS));
	}

	public static String randomLanguage() {
		List<String> langs = Arrays.asList("Java", "JS", "HTML5", "CSS", "Python", "C++", "Go", "Scala", "Groovy",
				"C#", "Clojure", "VB", "ObjectiveC");
		return langs.get(random(0, langs.size()));
	}

	public static synchronized String randomProjectName() {

		if (projectNames == null) {
			projectNames = new LinkedList<>();

			try {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(BuilderUtil.class.getResourceAsStream("/projectNameExamples.txt")));
				String line;
				line = in.readLine();
				while (line != null) {
					projectNames.add(line);
					line = in.readLine(); // try to read another line
				}
			} catch (IOException e) {
				throw new RuntimeException("never to happen", e);
			}

			Collections.shuffle(projectNames);
		}

		String projectName = projectNames.poll();
		projectNames.add(projectName);

		return projectName;
	}

	public static long freshId() {
		return latestId.incrementAndGet();
	}

}
