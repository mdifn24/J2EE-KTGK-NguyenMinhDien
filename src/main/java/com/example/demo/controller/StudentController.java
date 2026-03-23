package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class StudentController {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostMapping("/enroll/{courseId}")
    public String enrollCourse(@PathVariable Long courseId, Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName());
        Course course = courseRepository.findById(courseId).orElseThrow();

        if (!enrollmentRepository.existsByStudentAndCourse(student, course)) {
            Enrollment enrollment = new Enrollment(null, student, course, LocalDate.now());
            enrollmentRepository.save(enrollment);
        }

        return "redirect:/my-courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model, Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName());
        model.addAttribute("enrollments", enrollmentRepository.findByStudent(student));
        return "my-courses";
    }
}