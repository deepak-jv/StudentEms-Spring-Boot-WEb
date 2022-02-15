package com.web.dao;

import org.springframework.data.repository.CrudRepository;

import com.web.model.Courses;

public interface CourseDao extends CrudRepository<Courses, Integer> {
		
	public Courses findByCourse(String name);
	}
