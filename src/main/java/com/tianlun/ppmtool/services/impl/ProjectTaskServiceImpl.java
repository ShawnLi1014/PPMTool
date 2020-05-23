package com.tianlun.ppmtool.services.impl;

import com.tianlun.ppmtool.domain.Backlog;
import com.tianlun.ppmtool.domain.Project;
import com.tianlun.ppmtool.domain.ProjectTask;
import com.tianlun.ppmtool.exceptions.ProjectNotFoundException;
import com.tianlun.ppmtool.repositories.BacklogRepository;
import com.tianlun.ppmtool.repositories.ProjectRepository;
import com.tianlun.ppmtool.repositories.ProjectTaskRepository;
import com.tianlun.ppmtool.services.ProjectTaskService;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskServiceImpl implements ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final BacklogRepository backlogRepository;
    private final ProjectRepository projectRepository;

    public ProjectTaskServiceImpl(ProjectTaskRepository projectTaskRepository, BacklogRepository backlogRepository, ProjectRepository projectRepository) {
        this.projectTaskRepository = projectTaskRepository;
        this.backlogRepository = backlogRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        // PTs to be added to a specific project, project != null, BL exists
        try {
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            // Set the backlog to the project task
            projectTask.setBacklog(backlog);

            // The project Sequence is projectIdentifier-n, like IDPRO-1
            // update the bl sequence
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            // Initial priority when priority is null
            if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }
            // Initial status when status is null
            if (projectTask.getStatus() == null || projectTask.getStatus().equals("") ) {
                // TODO: switch to enum
                projectTask.setStatus("TO_DO");
            }
            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project Not Found");
        }

    }

    @Override
    public Iterable<ProjectTask> findBacklogById(String id) {
        Project project = projectRepository.findByProjectIdentifier(id);
        if (project == null) {
            throw new ProjectNotFoundException("Project with ID: " +id + " does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    @Override
    public ProjectTask findByProjectSequence(String backlog_id, String pt_id) {
        // make sure we are working on a exist backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if (backlog == null) {
            throw new ProjectNotFoundException("Project with ID: " + backlog_id + " does not exist");
        }
        // make sure that our task exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task with ID: " + pt_id + " does not exist");
        }
        // make sure that the backlog/project id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task " + pt_id + " does not exist in project " + backlog_id);
        }
        return projectTask;
    }

    @Override
    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {
        // Find existing project task
        ProjectTask projectTask = findByProjectSequence(backlog_id, pt_id);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    @Override
    public void deleteByProjectSequence(String backlog_id, String pt_id) {
        ProjectTask projectTask = findByProjectSequence(backlog_id, pt_id);
        projectTaskRepository.delete(projectTask);
    }
}
