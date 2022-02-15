package com.web.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.web.model.Student;

public interface StudentDao extends CrudRepository<Student, Integer> {

	public Iterable<Student> findAll(Pageable sorted);
	public Student findByfirstName(String name);
	
}
