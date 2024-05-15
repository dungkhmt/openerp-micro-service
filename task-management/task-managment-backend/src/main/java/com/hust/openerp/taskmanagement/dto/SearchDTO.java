package com.hust.openerp.taskmanagement.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<SearchProjectDTO> projects;
	private List<SearchTaskDTO> tasks;

	public SearchDTO(List<SearchProjectDTO> projects, List<SearchTaskDTO> tasks) {
		this.projects = projects;
		this.tasks = tasks;
	}

	public SearchDTO() {
	}
}
