package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.model.Course;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN");
        if (adminRole == null) {
            adminRole = new Role(null, "ADMIN");
            roleRepository.save(adminRole);
        }

        Role studentRole = roleRepository.findByName("STUDENT");
        if (studentRole == null) {
            studentRole = new Role(null, "STUDENT");
            roleRepository.save(studentRole);
        }

        if (studentRepository.findByUsername("admin") == null) {
            Student admin = new Student();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123")); // Mật khẩu là 123
            admin.setEmail("admin@gmail.com");
            admin.setRoles(Set.of(adminRole));
            studentRepository.save(admin);
        }

        if (studentRepository.findByUsername("student") == null) {
            Student student = new Student();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("123"));
            student.setEmail("student@gmail.com");
            student.setRoles(Set.of(studentRole));
            studentRepository.save(student);
        }

        if (courseRepository.count() == 0) {
            Category itCat = new Category(null, "Công nghệ thông tin");
            categoryRepository.save(itCat);

            courseRepository.save(new Course(null, "Lập trình Java", "https://via.placeholder.com/300?text=Java", 3, "Nguyễn Văn A", itCat));
            courseRepository.save(new Course(null, "Spring Boot", "https://via.placeholder.com/300?text=Spring", 4, "Trần Thị B", itCat));
            courseRepository.save(new Course(null, "Cơ sở dữ liệu", "https://via.placeholder.com/300?text=SQL", 3, "Lê Văn C", itCat));
            courseRepository.save(new Course(null, "Cấu trúc dữ liệu", "https://via.placeholder.com/300?text=DSA", 3, "Phạm Văn D", itCat));
            courseRepository.save(new Course(null, "Mạng máy tính", "https://via.placeholder.com/300?text=Network", 2, "Hoàng Thị E", itCat));
            courseRepository.save(new Course(null, "Trí tuệ nhân tạo", "https://via.placeholder.com/300?text=AI", 4, "Vũ Văn F", itCat));
        }
    }
}