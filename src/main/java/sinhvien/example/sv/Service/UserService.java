package sinhvien.example.sv.Service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.User;

import java.util.List;

public interface UserService {




    List<User> GetAllUser();
    void saveUsers(User user);
    User getUserById(long id);
    void deleteUserById(long id);

    Page<User> findPaginated(int pageNo, int pageSize);
    Page<User> findPaginated(int pageNo,int pageSize,String sortField,String sortDirection);

}
