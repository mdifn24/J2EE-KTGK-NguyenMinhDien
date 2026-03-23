package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "courses";
    }

    @GetMapping("/courses/new")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categoryRepository.findAll());
        // Fix: Trả về trực tiếp file course-form.html nằm trong thư mục templates
        return "course-form";
    }

    @GetMapping("/courses/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("categories", categoryRepository.findAll());
        // Fix: Trả về trực tiếp file course-form.html nằm trong thư mục templates
        return "course-form";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course, @RequestParam("imageFile") MultipartFile multipartFile) {

        // Nếu người dùng có chọn file ảnh mới
        if (!multipartFile.isEmpty()) {
            try {
                // 1. Lấy tên file
                String fileName = multipartFile.getOriginalFilename();

                // 2. Tạo thư mục 'uploads' ở ngoài cùng project nếu chưa có
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 3. Copy file vào thư mục
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                    // 4. Lưu đường dẫn vào object Course để insert DB
                    course.setImage("/uploads/" + fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // TRƯỜNG HỢP UPDATE: Nếu không chọn ảnh mới thì giữ nguyên ảnh cũ
            if (course.getId() != null) {
                Course existingCourse = courseRepository.findById(course.getId()).orElse(null);
                if (existingCourse != null) {
                    course.setImage(existingCourse.getImage());
                }
            }
        }

        courseRepository.save(course);
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/courses";
    }
}