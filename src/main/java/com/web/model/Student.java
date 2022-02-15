package com.web.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;




@Entity
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String firstName;
	private String lastName;
//	@OneToMany(cascade=CascadeType.ALL,mappedBy = "student")
	@ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinTable(name = "student_course",joinColumns = @JoinColumn(name="student_id"),
	inverseJoinColumns = @JoinColumn(name="course_id"))
	private List<Courses> course;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Courses> getCourse() {
		return course;
	}

	public void addCourse(Courses tempCourse) {

		if (course == null) {
			course = new ArrayList<Courses>();
		}
		course.add(tempCourse);
	}
	
	
}
