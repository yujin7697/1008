package com.example.demo.domain.service;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.Board;
import com.example.demo.domain.entity.Reply;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Transactional(rollbackFor = SQLException.class)
    public Boolean UserUpdate(String email, String newNickname){
        try {
            User user = userRepository.findByEmail(email);

            if (user != null) {
                user.setNickname(newNickname);
                userRepository.save(user);
                System.out.println("user: " +user);
                return true;
            } else {
                return false; // 사용자를 찾지 못한 경우
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 업데이트 중 예외 발생
        }

    }

    @Transactional
    public boolean withdrawUser(String email, String password) {
        try {
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (userOptional.isPresent()) {
                User user = userOptional.get(); // Optional에서 User를 얻는 방법
                // 비밀번호 확인
                if (passwordEncoder.matches(password, user.getPassword())) {
                    userRepository.delete(user);
                    return true;
                }
            }
            return false; // 사용자를 찾지 못하거나 비밀번호가 일치하지 않음
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 탈퇴 실패
        }
    }
    @Transactional
    public void updateUserProfilePicture(String email, String profilePicturePath) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setProfile(profilePicturePath);
            userRepository.save(user);
        }
    }

    public boolean joinMember(UserDto dto, Model model, HttpServletRequest request) {
        //패스워드 일치여부 확인
        if (!dto.getPassword().equals(dto.getRepassword())) {
            model.addAttribute("repassword", "패스워드가 일치하지 않습니다.");
            return false;
        }
        //=======================================
        //Email 중복체크
        if (isEmailAlreadyTaken(dto.getEmail())) {
            model.addAttribute("email", "이미 사용중인 이메일입니다.");
            return false;
        }
        return false;
    }
    public boolean isEmailAlreadyTaken (String email){
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(email));
        return existingUser.isPresent();
    }

}