package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    public List<Department> GetAllDepartment(){
        return departmentRepository.findAll();
    }
    public Department getDepartment(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }
    public Department getDepartmentById(Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (!department.isPresent()) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        return department.get();
    }
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
   public Page <Department> findPaginated(int pageNo,int pageSize){
       Pageable pageable= PageRequest.of(pageNo - 1,pageSize);
       return this.departmentRepository.findAll(pageable);
   }
}