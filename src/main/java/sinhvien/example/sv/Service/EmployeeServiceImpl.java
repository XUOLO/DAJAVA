package sinhvien.example.sv.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<User> GetAllUser(){
        return userRepository.findAll();
    }
    @Override
    public void saveUsers(User user){
        this.userRepository.save(user);
    }
    @Override
    public User getUserById(long id){
        Optional<User>optional=userRepository.findById(id);
        User user= null;
        if(optional.isPresent()){
            user=optional.get();

        }else {
            throw new RuntimeException("khong tim thay id nhan vien::"+id);
        }
        return user;
    }

    @Override
    public void  deleteUserById(long id){
        this.userRepository.deleteById(id);
    }




    @Override
    public Page<User> findPaginated(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.userRepository.findAll(pageable);
    }

    public Page<User>findPaginated(int pageNo, int pageSize,String sortField ,String sortDirection){
        Sort sort= sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending():
                Sort.by(sortField).descending();
                Pageable pageable=PageRequest.of(pageNo-1,pageSize,sort);
        return this.userRepository.findAll(pageable);
    }

}
