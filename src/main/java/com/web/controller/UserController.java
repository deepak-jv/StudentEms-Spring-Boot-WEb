package com.web.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.web.dao.CourseDao;
import com.web.dao.StudentDao;
import com.web.dao.UserDao;
import com.web.exception.StudentException;
import com.web.exception.UserException;
import com.web.model.Courses;
import com.web.model.Student;
import com.web.model.User;
import com.web.model.UserErorrResponse;

@RestController
public class UserController {
	@Autowired
	UserDao userDao;
	@Autowired
	StudentDao studentdao;
	@Autowired
	CourseDao coursedao;

//----******************** WEB/USER INTERFACE SCRIPT ****************************-------------------------

	@GetMapping("login")
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}

	@GetMapping("register")
	public ModelAndView register() {
		ModelAndView mav = new ModelAndView("register");
		return mav;
	}

	@PostMapping("login")
	public ModelAndView registerDone(@RequestParam String name, String email, String password, String contact) {

		User user = new User();
		user.setName(name);
		user.setContact(contact);
		user.setEmail(email);
		user.setPassword(password);
		User usr = userDao.save(user);
		ModelAndView mav = new ModelAndView("redirect:/login");
		return mav;
	}

	@PostMapping("loggedin")
	public ModelAndView loggedin(Model model, @RequestParam String name, String password) {
		List<String> users = new ArrayList<String>();
		Iterable<User> findAll = userDao.findAll();
		findAll.forEach(f -> {
			users.add(f.getName());
			users.add(f.getPassword());
		});
		if (users.contains(name) && users.contains(password)) {
			Iterable<Student> userss = studentdao.findAll();

			model.addAttribute("users", userss);
			ModelAndView mav = new ModelAndView("redirect:/student_list");

			return mav;
		} else {
			String errorMessage = "invalid credentials";
			throw new UserException(errorMessage);
		}
	}

	@GetMapping("student_list")
	public ModelAndView Students_List(Model model) {

		Iterable<Student> userss = studentdao.findAll();
		model.addAttribute("users", userss);
		ModelAndView mav = new ModelAndView("student_list");
		return mav;

	}

	@GetMapping("delete/{id}")
	public ModelAndView delete(@PathVariable int id) {

		studentdao.deleteById(id);
		ModelAndView mav = new ModelAndView("redirect:/student_list");
		return mav;
	}

	@PostMapping("add")
	public ModelAndView addStudentDone(@RequestParam String firstName, String lastName, String course, int courseId) {

		Courses course1 = new Courses();
		course1.setCourse(course);

		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);

		List<Integer> course_id = new ArrayList<Integer>();
		List<String> course_name = new ArrayList<String>();

		Iterable<Courses> findAll = coursedao.findAll();
		findAll.forEach(e -> {
			course_id.add(e.getId());
			course_name.add(e.getCourse());
		});

		if (course_id.isEmpty()) {
			course1.setId(0);
			student.addCourse(course1);
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
					Optional<Courses> findById = coursedao.findById(courseId);
					Courses courses = findById.get();
					if (courses.getCourse().equals(course1.getCourse())) {
						student.addCourse(courses);
					} else {
						String errorMsg = "course id and name not matched.";
						throw new StudentException(errorMsg);
					}
				}

				else if (course_name.contains(course)) {
					Courses findByCourse = coursedao.findByCourse(course);
					if (findByCourse.getId() == courseId) {
						student.addCourse(findByCourse);
					} else {
						String errorMsg = "course id and name not matched.";
						throw new StudentException(errorMsg);
					}

				}

				else {

					course1.setId(0);
					student.addCourse(course1);
				}
			}
		}

		studentdao.save(student);
		ModelAndView mav = new ModelAndView("redirect:/student_list");
		mav.addObject("msg", "Student successfully added");
		return mav;
	}

	@GetMapping("home")
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView();
		return mav;
	}

	@GetMapping("add_student")
	public ModelAndView addStudent() {
		ModelAndView mav = new ModelAndView("add_student");
		return mav;
	}

	@GetMapping("logout")
	public ModelAndView logout() {

		ModelAndView mav = new ModelAndView("redirect:/login");
		return mav;
	}

//	---------------**************API SCRIPTS FOR USER***********************---------------------

	@PostMapping("/user/add")
	public String addUser(@RequestParam String name, String contact, String email, String password)
			throws SQLException {

		List<String> list = new ArrayList<String>();
		List<User> users = (List<User>) userDao.findAll();

		for (User user : users) {
			list.add(user.getName());
			list.add(user.getPassword());
		}

		if (list.contains(name) || list.contains(password)) {
			System.out.println(list);
			String errorMessage = "User or Password already exists please try again ...!";
			throw new UserException(errorMessage);

		}

		else {
			User user = new User();
			user.setName(name);
			user.setContact(contact);
			user.setEmail(email);
			user.setPassword(password);
			userDao.save(user);
			return name + " Registered Successfully";
		}
	}

//	@PutMapping("/user/{id1}")
//	public String updateUser(@PathVariable int id1, @RequestParam int id, String name, String contact, String email,
//			String password) throws SQLException {
//		Optional<User> findById = userDao.findById(id);
//		User user = findById.get();
//		user.setId(id);
//		user.setName(name);
//		user.setContact(contact);
//		user.setEmail(email);
//		user.setPassword(password);
//		userDao.save(user);
//
//		return "updated successfully";

//	}

	@PostMapping("user/login")
	public List<String> login(Model model, @RequestParam String name, String password) throws SQLException {

		List<String> name_list = new ArrayList<String>();
		List<User> users = (List<User>) userDao.findAll();
		for (User user1 : users) {
			name_list.add(user1.getName());
		}

		if (name_list.contains(name)) {
			User user = userDao.findByName(name);
			String user_name = user.getName();
			String user_password = user.getPassword();

			if (user_name.equals(name) && password.equals(user_password)) {

				List<String> urls = new ArrayList<String>();

				urls.add("To add Student data =  /api/v2/token=12345989769683/students/add");
				urls.add("To get all student data = /api/v2/token=12345989769683/students");
				urls.add(
						"To get all student data with pagination and Sorting = /api/v2/token=12345989769683/students/sort/{setoff}/{pagesize}/{field}");
				urls.add("To get student by his id = /api/v2/token:88517723sdfafe242259917076202/{id}");
				urls.add("To update student by his id = /api/v2/token=12345989769683/students/{id1}");
				urls.add("To get single by his first name =/api/v2/token=12345989769683/students/byname/{firstName}");
				urls.add("To delete Student by his id =  /api/v2/token=12345989769683/students/delete/{id}");

				return urls;
			} else {
				String errorMessage = "invalid credentials";
				throw new UserException(errorMessage);

			}
		} else {
			String errorMessage = "This name is not registered please register first";
			throw new UserException(errorMessage);
		}

	}

	@ExceptionHandler
	public ResponseEntity<UserErorrResponse> handleException(UserException exc) {

		UserErorrResponse error = new UserErorrResponse();
		error.setMessage(exc.getMessage());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setTimestamp(System.currentTimeMillis());

		return new ResponseEntity<UserErorrResponse>(error, HttpStatus.BAD_REQUEST);
	}

}
