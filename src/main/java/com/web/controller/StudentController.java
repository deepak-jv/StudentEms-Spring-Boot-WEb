package com.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.web.dao.CourseDao;
import com.web.dao.StudentDao;
import com.web.exception.StudentException;
import com.web.model.Courses;
import com.web.model.Student;
import com.web.model.StudentErorrResponse;

@RestController
public class StudentController {
	@Autowired
	StudentDao studentdao;
	@Autowired
	CourseDao courseDao;

//------------------*************	STUDENT SCRIPT FOR API   *****************---------------------

	@PostMapping("api/v2/token=12345989769683/students/add")
	public Student addStudent(HttpServletRequest request) {
		String cours = request.getParameter("course");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		int courseId = Integer.parseInt(request.getParameter("courseId"));

		Courses course = new Courses();
		course.setCourse(cours);

		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);

		List<Integer> course_id = new ArrayList<Integer>();
		List<String> course_name = new ArrayList<String>();

		Iterable<Courses> findAll = courseDao.findAll();
		findAll.forEach(e -> {
			course_id.add(e.getId());
			course_name.add(e.getCourse());
		});

		if (course_id.isEmpty()) {
			course.setId(0);
			student.addCourse(course);
		} else {

			List<String> student_names = new ArrayList<String>();
			Iterable<Student> findAll2 = studentdao.findAll();
			findAll2.forEach(f -> {
				student_names.add(f.getFirstName());
			});

			if (student_names.contains(firstName)) {
				String errorMsg = "Student " + firstName + " Already exists";
				throw new StudentException(errorMsg);
			} else {

				if (course_id.contains(courseId)) {
					Optional<Courses> findById = courseDao.findById(courseId);
					Courses courses = findById.get();
					if (courses.getCourse().equals(course.getCourse())) {
						student.addCourse(courses);
					} else {
						String errorMsg = "course id and name not matched.";
						throw new StudentException(errorMsg);
					}
				}

				else if (course_name.contains(cours)) {
					Courses findByCourse = courseDao.findByCourse(cours);
					if (findByCourse.getId() == courseId) {
						student.addCourse(findByCourse);
					} else {
						String errorMsg = "course id and name not matched.";
						throw new StudentException(errorMsg);
					}

				}

				else {

					course.setId(0);
					student.addCourse(course);
				}
			}
		}

		return studentdao.save(student);
	}

	@GetMapping("api/v2/token=12345989769683/students")
	public Iterable<Student> getall(HttpServletRequest request) {

		Iterable<Student> findAll = studentdao.findAll();
		return findAll;
	}

	@GetMapping("api/v2/token=12345989769683/students/{id}")
	public Optional<Student> findById(@PathVariable int id) {
		List<Integer> ids = new ArrayList<Integer>();
		Iterable<Student> students = studentdao.findAll();
		students.forEach(s -> {
			ids.add(s.getId());
		});

		if (ids.contains(id)) {
			Optional<Student> student = studentdao.findById(id);
			return student;
		} else {

			String errorMsg = "Student not found with given id: " + id;
			throw new StudentException(errorMsg);
		}

	}

	@GetMapping("api/v2/token=12345989769683/students/sort/{setoff}/{pagesize}/{field}")
	public Iterable<Student> getBySorting(@PathVariable String field, @PathVariable int setoff,
			@PathVariable int pagesize) {

		Pageable sorted = PageRequest.of(setoff, pagesize, Sort.by(Sort.Direction.DESC, field));
		Iterable<Student> studentSorted = studentdao.findAll(sorted);
		return studentSorted;
	}

	@GetMapping("api/v2/token=12345989769683/students/byname/{firstName}")
	public Student getstudentByName(@PathVariable String firstName) {

		List<String> students = new ArrayList<String>();
		Iterable<Student> findAll = studentdao.findAll();
		findAll.forEach(f -> {
			students.add(f.getFirstName());
		});

		if (students.contains(firstName)) {
			Student student = studentdao.findByfirstName(firstName);
			return student;
		} else {
			String errorMsg = "Student name " + firstName + " not found";
			throw new StudentException(errorMsg);
		}

	}

	@PutMapping("api/v2/token=12345989769683/students/{id1}")
	public Student update(@PathVariable int id1, @RequestParam int id, String firstName, String lastName, String course,
			int courseId) {

		Courses course1 = new Courses();
		course1.setCourse(course);

		Optional<Student> findById = studentdao.findById(id);
		Student student = findById.get();
		student.setFirstName(firstName);
		student.setLastName(lastName);

		List<Integer> course_id = new ArrayList<Integer>();
		List<String> course_name = new ArrayList<String>();

		Iterable<Courses> findAll = courseDao.findAll();
		findAll.forEach(e -> {
			course_id.add(e.getId());
			course_name.add(e.getCourse());
		});

		List<Integer> course_ids = new ArrayList<Integer>();
		Iterable<Courses> coursess = courseDao.findAll();
		coursess.forEach(c -> {
			course_ids.add(c.getId());
		});

		if (course_ids.contains(courseId)) {

			Optional<Courses> findById1 = courseDao.findById(courseId);
			Courses courses = findById1.get();

			if (course_name.contains(course) && course_id.contains(courseId)) {
				Optional<Courses> cours = courseDao.findById(courseId);
				Courses coursesById = cours.get();

				List<String> allUserCourses = new ArrayList<String>();
				List<Courses> usercourses = student.getCourse();
				usercourses.forEach(u -> {
					allUserCourses.add(u.getCourse());
				});

				if (allUserCourses.contains(course)) {

					String errorMsg = course + " course is already associated with this user.";
					throw new StudentException(errorMsg);
				} else {

					if (courses.getCourse().equals(coursesById.getCourse())) {
						student.addCourse(courses);
					} else {
						String errorMsg = " course id and name not matched.";
						throw new StudentException(errorMsg);
					}
				}
			}

			else if (course_name.contains(course)) {
				Courses findByCourse = courseDao.findByCourse(course);

				if (student.getCourse().equals(course)) {
					String errorMsg = course + " course is already associated with this user.";
					throw new StudentException(errorMsg);
				} else {
					if (findByCourse.getId() == courseId) {
						student.addCourse(findByCourse);
					} else {
						String errorMsg = " course id and name not matched.";
						throw new StudentException(errorMsg);
					}
				}

			} else if (course_id.contains(courseId)) {
				Optional<Courses> findById2 = courseDao.findById(courseId);
				Courses coursesByid = findById2.get();
				if (student.getCourse().equals(course)) {
					String errorMsg = course + " course is already associated with this user.";
					throw new StudentException(errorMsg);
				} else {
					if (coursesByid.getCourse().equals(course)) {
						student.addCourse(coursesByid);
					} else {
						String errorMsg = " course id and name not matched.";
						throw new StudentException(errorMsg);
					}
				}
			}

			else {
				String errorMsg = " course id and name not matched.";
				throw new StudentException(errorMsg);
			}
		} else {
			String errorMsg = "course Id not present";
			throw new StudentException(errorMsg);
		}

		return studentdao.save(student);

	}

	@GetMapping("api/v2/token=12345989769683/students/delete/{id}")
	public String delete(@PathVariable int id) {

		List<Integer> ids = new ArrayList<Integer>();
		Iterable<Student> students = studentdao.findAll();
		students.forEach(s -> {
			ids.add(s.getId());
		});

		if (ids.contains(id)) {

			studentdao.deleteById(id);
			return "deleted successfully";
		} else {

			String errorMsg = "Student not found with given id: " + id;
			throw new StudentException(errorMsg);
		}

	}

	@ExceptionHandler
	public ResponseEntity<StudentErorrResponse> handelException(StudentException exc) {
		StudentErorrResponse error = new StudentErorrResponse();
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMessage(exc.getMessage());
		error.setTimestamp(System.currentTimeMillis());

		return new ResponseEntity<StudentErorrResponse>(error, HttpStatus.NOT_FOUND);
	}

}
