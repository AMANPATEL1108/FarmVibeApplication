package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobileNumber(Long mobileNumber);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.user_firstName = :firstName, u.user_lastName = :lastName, u.user_email = :email, u.mobileNumber = :phone, u.profileImageUrl = :profileImage WHERE u.userId = :userId")
    int updateUserProfile(@Param("userId") Long userId,
                          @Param("firstName") String firstName,
                          @Param("lastName") String lastName,
                          @Param("email") String email,
                          @Param("phone") Long phone,
                          @Param("profileImage") String profileImage);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.profileImageUrl = :profileImage WHERE u.userId = :userId")
    void updateProfileImage(@Param("userId") Long userId,
                            @Param("profileImage") String profileImage);

}
