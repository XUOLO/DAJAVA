package sinhvien.example.sv.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import sinhvien.example.sv.Entity.Ticket;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.RoleRepository;
import sinhvien.example.sv.Repository.TicketRepository;
import sinhvien.example.sv.Repository.UserRepository;
import sinhvien.example.sv.Security.PasswordUtils;
import sinhvien.example.sv.Service.DepartmentService;
import sinhvien.example.sv.Service.RoleService;
import sinhvien.example.sv.Service.TicketService;
import sinhvien.example.sv.Service.UserService;


import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @GetMapping("/home")
    public String index(HttpSession session, Model model) {
        // Kiểm tra đăng nhập
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/admin"; // Chuyển hướng đến trang đăng nhập nếu người dùng chưa đăng nhập
        }

        // Kiểm tra vai trò của người dùng
        boolean isAdminOrEmployee = false;
        for(Role role : sessionUser.getRoles()){
            if(role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")) {
                isAdminOrEmployee = true;
                break;
            }
        }

        if(!isAdminOrEmployee) {
            return "redirect:/admin"; // Nếu không phải admin hoặc employee, chuyển hướng đến trang đăng nhập
        }

        // Nếu đã đăng nhập và có vai trò admin/employee, tiếp tục xử lý
        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        model.addAttribute("user", sessionUser);

        return "Admin/layout";
    }

    @GetMapping("")
    public String login(){
        return "Admin/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
        User user = userService.findUserByUsername(username);

        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "Admin/login";
        }

        String hashedPassword = PasswordUtils.generateSecurePassword(password, user.getSalt());

        if (!user.getPassword().equals(hashedPassword)) {
            model.addAttribute("error", "Invalid username or password");
            return "Admin/login";
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("user", user);
        return "redirect:/admin/home";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin";
    }

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/listAccount")
    public String viewUser(Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/admin"; // Chuyển hướng đến trang đăng nhập nếu người dùng chưa đăng nhập
        }

        // Kiểm tra vai trò của người dùng
        boolean isAdminOrEmployee = false;
        for(Role role : sessionUser.getRoles()){
            if(role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")) {
                isAdminOrEmployee = true;
                break;
            }
        }

        if(!isAdminOrEmployee) {
            return "redirect:/admin"; // Nếu không phải admin hoặc employee, chuyển hướng đến trang đăng nhập
        }

        // Nếu đã đăng nhập và có vai trò admin/employee, tiếp tục xử lý
        List<User> userList = userService.GetAllUser();
        model.addAttribute("listUser", userList);

        List<User> user = userRepository.findAll();
        model.addAttribute("listUser", user);

        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        model.addAttribute("user", sessionUser);

        return "Admin/Account";
    }

    ////Department
    @GetMapping("/listDepartment")
    public String viewDepartment(Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/admin"; // Chuyển hướng đến trang đăng nhập nếu người dùng chưa đăng nhập
        }

        // Kiểm tra vai trò của người dùng
        boolean isAdminOrEmployee = false;
        for(Role role : sessionUser.getRoles()){
            if(role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")) {
                isAdminOrEmployee = true;
                break;
            }
        }
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        if(!isAdminOrEmployee) {
            return "redirect:/admin"; // Nếu không phải admin hoặc employee, chuyển hướng đến trang đăng nhập
        }

        // Nếu đã đăng nhập và có vai trò admin/employee, tiếp tục xử lý
        return findPaginated(1, model,session);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo")int pageNo,Model model,HttpSession session){
        int pageSize=5;
        Page<Department> page=departmentService.findPaginated(pageNo,pageSize);
        List<Department> departmentList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listDepartment",departmentList);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        return "Admin/listDepartment";

    }

    @GetMapping("/CreateDepartment")
    public String addDepartment(Model model,HttpSession session) {

        model.addAttribute("department", new Department());

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        return "Admin/CreateDepartment";
    }
    @PostMapping("/CreateDepartment")
    public String addDepartment(@Valid @ModelAttribute("department") Department department,HttpSession session, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", department);
            return "admin/CreateDepartment";
        }
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        departmentService.saveDepartment(department);
        return "redirect:/admin/listDepartment";
    }

    @GetMapping("/EditDepartment/{id}")
    public String editDepartment(@PathVariable("id") Long id, Model model,HttpSession session) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        model.addAttribute("department", department);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        return "Admin/EditDepartment";
    }

    @PostMapping("/EditDepartment/{id}")
    public String EditDepartment(@PathVariable("id") Long id,HttpSession session, @Valid @ModelAttribute("department") Department department, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", department);
            return "Admin/EditDepartment";
        }
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        departmentService.saveDepartment(department);
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

//////ListRequest
@GetMapping("/listRequest")
public String viewRequest(Model model,HttpSession session) {
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser != null) {
        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
    }
    model.addAttribute("user", sessionUser);

    return findPaginatedRequest(1,model,session);
}
    @GetMapping("/pageTicket/{pageNo}")
    public String findPaginatedRequest(@PathVariable(value = "pageNo")int pageNo,Model model,HttpSession session){
        int pageSize=5;
        Page<Ticket> page= ticketService.findPaginatedRequest(pageNo,pageSize);
        List<Ticket> ticketList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listTicket",ticketList);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        return "Admin/listRequest";

    }

    @PostMapping("/{id}/updateStatus")
    public String updateTicketStatus(@PathVariable("id") Long id, @RequestParam("status") String status,Model model,HttpSession session, HttpServletRequest request) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket id: " + id));
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        String referer = request.getHeader("Referer");
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);
        return "redirect:" + referer;
    }

    @PostMapping("/ListRequest/search")
    public String searchTicket(@RequestParam("keyword") String keyword, Model model ,HttpSession session) {
        List<Ticket> tickets = ticketService.searchTicketAdmin(keyword);
        if (tickets.isEmpty()) {
            String errorMessage = "No matching tickets found";
            model.addAttribute("errorMessage", errorMessage);
        } else {
            model.addAttribute("listTicket", tickets);
        }
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for(Role role : sessionUser.getRoles()){
                roles.add(role.getName());
            }

            model.addAttribute("name", name);
            model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        }
        model.addAttribute("user", sessionUser);

        return "Admin/ListRequest"  ;
    }


//    @GetMapping("/ListRequest/search")
//    public List<Ticket> searchTicketAdmin(@RequestParam("keyword") String keyword) {
//        return ticketService.searchTicketAdmin(keyword);
//    }
//    @PostMapping("/tickets/{id}/status")
//    public ResponseEntity<Void> updateTicketStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
//        Ticket ticket = ticketService.getTicketById(id);
//        if (ticket == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        try {
//            Ticket.TicketStatus newStatus = Ticket.TicketStatus.valueOf(request.get("status"));
//            ticket.setStatus(newStatus);
//            ticketService.saveTicket(ticket);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().build();
//        }
//    }



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

//    @GetMapping("/edit/{id}")
//    public String editAccount(@PathVariable Long id, Model model) {
//        User user = userService.getUser(id);
//        model.addAttribute("user", user);
//
//        List<Role> roleList = roleRepository.findAll();
//        model.addAttribute("roleList", roleList);
//
//        return "Admin/EditAccount";
//    }
@GetMapping("/edit/{id}")
public String showUpdateForm(@PathVariable("id") Long id, Model model,HttpSession session) {
    User user = userService.getUserById(id);
    List<Role> roleList = roleService.getAllRole();
    model.addAttribute("user", user);
    model.addAttribute("roleList", roleList);
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser != null) {
        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
    }
    model.addAttribute("user", sessionUser);
    return "Admin/EditAccount";
}

    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           HttpSession session,Model model) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid input");
            return "redirect:/admin/listAccount";
        }

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();

            // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
            List<String> roles = new ArrayList<>();
            for (Role role : sessionUser.getRoles()) {
                roles.add(role.getName());
            }

            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("roles", roles);
        }

        // Cập nhật thông tin và vai trò của người dùng
        User updatedUser = userService.editUser(user);

        if (updatedUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
        } else {
            redirectAttributes.addFlashAttribute("success", "Account is updated successfully.");
        }
        model.addAttribute("user", sessionUser);
        return "redirect:/admin/listAccount";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@Valid @ModelAttribute Long id, RedirectAttributes redirectAttributes) {
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
