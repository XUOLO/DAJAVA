package sinhvien.example.sv.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Service.DepartmentService;
import sinhvien.example.sv.Service.RoleService;
import sinhvien.example.sv.Service.UserService;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @GetMapping("/home")
    public String index(){
        return "Admin/layout";
    }

    @GetMapping("/listAccount")
    public String viewUser(Model model) {
        List<User> userList = userService.GetAllUser();
        model.addAttribute("listUser", userList);
        return "Admin/Account";
    }

    ////Department
    @GetMapping("/listDepartment")
    public String viewDepartment(Model model) {


        return findPaginated(1,model);
    }
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo")int pageNo,Model model){
        int pageSize=5;
        Page<Department> page=departmentService.findPaginated(pageNo,pageSize);
        List<Department> departmentList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listDepartment",departmentList);
        return "Admin/listDepartment";

    }

    @GetMapping("/CreateDepartment")
    public String addDepartment(Model model) {

        model.addAttribute("department", new Department());


        return "Admin/CreateDepartment";
    }
    @PostMapping("/CreateDepartment")
    public String addDepartment(@Valid @ModelAttribute("department") Department department, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", department);
            return "admin/CreateDepartment";
        }
        departmentService.saveDepartment(department);
        return "redirect:/admin/listDepartment";
    }

    @GetMapping("/EditDepartment/{id}")
    public String editDepartment(@PathVariable("id") Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        model.addAttribute("department", department);
        return "Admin/EditDepartment";
    }

    @PostMapping("/EditDepartment/{id}")
    public String updateDepartment(@PathVariable("id") Long id, @Valid @ModelAttribute("department") Department department, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", department);
            return "Admin/EditDepartment";
        }
        Department existingDepartment = departmentService.getDepartmentById(id);
        if (existingDepartment == null) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        existingDepartment.setName(department.getName());
        existingDepartment.setDescription(department.getDescription());
        departmentService.saveDepartment(existingDepartment);
        return "redirect:/admin/listDepartment";
    }

    @GetMapping("/deleteDepartment/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Department department = departmentService.getDepartment(id);
        if (department == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        } else {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        }
        return "redirect:/admin/listDepartment";
    }




    ///Account
    @GetMapping("/CreateAccount")
    public String addBookForm(Model model) {
        List<Role> roles = roleService.getAllRole();
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);

        return "Admin/CreateAccount";
    }
    @PostMapping("/CreateAccount")
    public String addACCOUNT(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRole());
            model.addAttribute("user", user);

            return "admin/CreateAccount";
        }
        userService.saveUser(user);
        return "redirect:/admin/listAccount";
    }

    @GetMapping("/edit/{id}")
    public String editAccount(@PathVariable Long id, Model model) {
        User user = userService.getUser(id);
        model.addAttribute("user", user);
        return "Admin/EditAccount";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "Admin/Account";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getUser(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        } else {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        }
        return "redirect:/admin/listAccount";
    }
}
