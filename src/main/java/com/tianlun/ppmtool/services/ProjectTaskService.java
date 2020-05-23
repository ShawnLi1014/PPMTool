package com.tianlun.ppmtool.services;

import com.tianlun.ppmtool.domain.ProjectTask;

import java.util.List;

public interface ProjectTaskService {

    ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask);

    Iterable<ProjectTask> findBacklogById(String backlog_id);

    ProjectTask findByProjectSequence(String backlog_id, String pt_id);

    ProjectTask updateByProjectSequence(ProjectTask task, String backlog_id, String pt_id);

    void deleteByProjectSequence(String backlog_id, String pt_id);
}
