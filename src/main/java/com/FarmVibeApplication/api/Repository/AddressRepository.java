package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    //    List<Address> findByUser_User_Id(Long userId);
    List<Address> findByUser_userId(Long userId); // ✅ This will now work!

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET " +
            "a.first_name = :firstName, " +
            "a.last_name = :lastName, " +
            "a.email = :email, " +
            "a.number = :phone, " +
            "a.house_number = :houseNumber, " +
            "a.street = :street, " +
            "a.city = :city, " +
            "a.area = :area, " +
            "a.pincode = :pincode " +
            "WHERE a.id = :addressId")
    int updateAddress(
            @Param("addressId") Long addressId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("houseNumber") String houseNumber,
            @Param("street") String street,
            @Param("city") String city,
            @Param("area") String area,
            @Param("pincode") String pincode
    );
}

