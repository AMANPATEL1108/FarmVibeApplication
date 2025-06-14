package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {

//    List<Address> findByUser_User_Id(Long userId);
List<Address> findByUser_userId(Long userId); // ✅ This will now work!


}
