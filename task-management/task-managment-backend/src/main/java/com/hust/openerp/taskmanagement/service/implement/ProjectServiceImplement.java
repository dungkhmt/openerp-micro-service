package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.ProjectDTO;
import com.hust.openerp.taskmanagement.dto.form.UpdateProjectForm;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.Project_;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.ProjectMemberRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.ProjectService;
import com.hust.openerp.taskmanagement.specification.ProjectSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectServiceImplement implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberService projectMemberService;

    private final ModelMapper mapper;

    @Override
    public ProjectDTO getProjectById(UUID projectId, String userId) {
        boolean isMember = projectMemberService.checkAddedMemberInProject(userId, projectId);
        if (!isMember) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }
        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));
        // FIXME: add the role to the response (maybe not necessary)
        return mapper.map(project, ProjectDTO.class);
    }

    @Override
    @Transactional
    public ProjectDTO createProject(Project project, String creatorId) {
        if (projectRepository.existsByCode(project.getCode())) {
            throw new ApiException(ErrorCode.PROJECT_CODE_EXIST);
        }

        project.setCreatedUserId(creatorId);
        var projectEntity = projectRepository.save(project);
        var projectMember = ProjectMember.builder()
                .projectId(projectEntity.getId())
                .userId(creatorId)
                .roleId("owner")
                .build();
        projectMemberRepository.save(projectMember);
        var projectDto = mapper.map(projectEntity, ProjectDTO.class);
        projectDto.setRole("owner");
        return projectDto;
    }

    @Override
    public ProjectDTO updateProject(UUID id, UpdateProjectForm project, String updaterId) {
        Project projectToUpdate = projectRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

        // TODO: consider use a separate role for project update
        if (!projectMemberService.checkAddedMemberInProject(updaterId, id)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }

        if (project.getName() != null && !project.getName().equals(""))
            projectToUpdate.setName(project.getName());
        if (project.getDescription() != null && !project.getDescription().equals(""))
            projectToUpdate.setDescription(project.getDescription());
        if (project.getCode() != null && !project.getCode().equals("")) {
        	if (projectRepository.existsByCode(project.getCode())) {
                throw new ApiException(ErrorCode.PROJECT_CODE_EXIST);
            }
            projectToUpdate.setCode(project.getCode());
        }

        var updatedProject = projectRepository.save(projectToUpdate);

        // FIXME: add the role to the response (maybe not necessary)
        return mapper.map(updatedProject, ProjectDTO.class);
    }

    @Override
    public void deleteProjectById(UUID id, String deleterId) {
        var project = projectRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));
        if (!project.getCreatedUserId().equals(deleterId)) {
            throw new ApiException(ErrorCode.NOT_OWNER_OF_PROJECT);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public Page<ProjectDTO> findPaginated(String memberId, Pageable pageable, String searchString) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Project_.CREATED_STAMP).descending());
        }

        if (searchString != null && !searchString.equals("")) {
            // find memberId: and replace value
            if (searchString.contains("memberId:")) {
                searchString = searchString.replace("memberId:", "memberId:" + memberId);
            } else {
                searchString = "( " + searchString + " ) AND memberId:" + memberId;
            }
        } else {
            searchString = "memberId:" + memberId;
        }

        var parser = new CriteriaParser();

        GenericSpecificationsBuilder<Project> builder = new GenericSpecificationsBuilder<>();
        var spec = builder.build(parser.parse(searchString), ProjectSpecification::new);
        return projectRepository.findAll(spec, pageable).map(project -> {
            var dto = mapper.map(project, ProjectDTO.class);
            dto.setRole(project.getMembers().stream()
                    .filter(member -> member.getUserId().equals(memberId)).findFirst()
                    .orElse(ProjectMember.builder().roleId("").build()).getRoleId());
            return dto;
        });
    }

	@Override
	public List<String> getProjectsCode() {
		return projectRepository.findAllProjectCode();
	}
}
