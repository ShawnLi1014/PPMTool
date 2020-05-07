package com.tianlun.ppmtool.services;

import com.tianlun.ppmtool.domain.Project;

public interface ProjectService {

    Project saveOrUpdateProject(Project project);

    Project findProjectByIdentifier(String projectId);

    Iterable<Project> findAllProject();

    void deleteProjectByIdentifier(String projectId);

}
