package org.zalando.catwatch.backend.web.admin;

import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDto {

	public List<Contributor> contributors = new ArrayList<>();

	public List<Project> projects = new ArrayList<>();

	public List<Statistics> statistics = new ArrayList<>();
}