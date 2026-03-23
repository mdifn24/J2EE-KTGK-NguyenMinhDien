package com.example.demo.security;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin user từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        // Kiểm tra xem email này đã tồn tại trong DB chưa
        Student student = studentRepository.findByUsername(email);

        if (student == null) {
            // Tự động tạo tài khoản mới cho sinh viên đăng nhập bằng Google
            student = new Student();
            student.setUsername(email); // Dùng email làm username
            student.setEmail(email);
            // Mã hóa một mật khẩu ngẫu nhiên không sử dụng tới
            student.setPassword(passwordEncoder.encode("OAUTH2_DUMMY_PASSWORD_123!"));

            Role studentRole = roleRepository.findByName("STUDENT");
            if (studentRole == null) {
                studentRole = new Role(null, "STUDENT");
                roleRepository.save(studentRole);
            }

            student.setRoles(Collections.singleton(studentRole));
            studentRepository.save(student);
        }

        // Trả về User kèm theo quyền ROLE_STUDENT
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_STUDENT")),
                oAuth2User.getAttributes(),
                "email" // Sử dụng email làm khóa chính
        );
    }
}