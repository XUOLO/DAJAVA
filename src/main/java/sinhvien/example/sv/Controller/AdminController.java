package sinhvien.example.sv.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping("/pageAdmin/{pageNo}")
    public String findPaginatedAdmin(@PathVariable(value = "pageNo")int pageNo,Model model,HttpSession session){
        int pageSize=5;
        Page<User> page=userService.findPaginated(pageNo,pageSize);
        List<User> userList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listUser",userList);
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
        List<User> userListt = userService.GetAllUser();
        List<User> adminAndEmployeeUsers = userListt.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")))
                .collect(Collectors.toList());

        model.addAttribute("listUser", adminAndEmployeeUsers);
        model.addAttribute("name", sessionUser.getName());
        model.addAttribute("user", sessionUser);
        return "Admin/Account";

    }

    @GetMapping("/listAccount")
    public String viewAdmin(Model model, HttpSession session) {
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
        List<User> adminAndEmployeeUsers = userList.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")))
                .collect(Collectors.toList());

        model.addAttribute("listUser", adminAndEmployeeUsers);
        model.addAttribute("name", sessionUser.getName());



        List<User> user = userRepository.findAll();


        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        model.addAttribute("user", sessionUser);

        return findPaginatedAdmin(1, model,session);
    }
    @GetMapping("/pageUser/{pageNo}")
    public String findPaginatedUser(@PathVariable(value = "pageNo")int pageNo,Model model,HttpSession session){
        int pageSize=5;
        Page<User> page=userService.findPaginated(pageNo,pageSize);
        List<User> userList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listUser",userList);
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
        List<User> userListt = userService.GetAllUser();
        List<User> adminAndEmployeeUsers = userListt.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("CUSTOMER")))
                .collect(Collectors.toList());

        model.addAttribute("listUser", adminAndEmployeeUsers);
        model.addAttribute("name", sessionUser.getName());
        model.addAttribute("user", sessionUser);
        return "Admin/listUserAccount";

    }

    @GetMapping("/listUserAccount")
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
        List<User> adminAndEmployeeUsers = userList.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("CUSTOMER")))
                .collect(Collectors.toList());

        model.addAttribute("listUser", adminAndEmployeeUsers);
        model.addAttribute("name", sessionUser.getName());




        List<User> user = userRepository.findAll();


        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        model.addAttribute("user", sessionUser);

        return findPaginatedUser(1, model,session);
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
            redirectAttributes.addFlashAttribute("error", "Department not found!");
        } else {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Department deleted successfully!");
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

    ///ticket statistics

    @GetMapping("/statistic")
    public String StatisticTicket(Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("countTicket", ticketRepository.count());
        model.addAttribute("countCustomer", userService.countCustomers());
        model.addAttribute("countAdmin", userService.countAdmin());
        model.addAttribute("countEmployee", userService.countEmployee());
        model.addAttribute("countAdminAndEmployee", userService.countEmployeesAndAdmins());
        String Date = java.time.LocalDate.now().toString();
        model.addAttribute("date",Date);
        model.addAttribute("month","2022-06");

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model

        Map<String, Long> ticketCounts = ticketService.countTicketByStatus();
        model.addAttribute("ticketCounts", ticketCounts);
        Long openTicketCount = ticketService.countOpenTickets();
        Long countInProgressTickets = ticketService.countInProgressTickets();
        Long countResolveTickets = ticketService.countResolveTickets();
        model.addAttribute("openTicketCount", openTicketCount);
        model.addAttribute("InProgressTicketCount", countInProgressTickets);
        model.addAttribute("ResolveCount", countResolveTickets);
        model.addAttribute("user", sessionUser);
        return "Admin/Statistic";
    }


//Chat

    @GetMapping("/chat")
    public String chat(Model model,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        String name = sessionUser.getName();

        // Tạo list roles để lưu tên các vai trò của người dùng đăng nhập
        List<String> roles = new ArrayList<>();
        for(Role role : sessionUser.getRoles()){
            roles.add(role.getName());
        }

        model.addAttribute("name", name);
        model.addAttribute("roles", roles); // Đưa danh sách các vai trò vào model
        model.addAttribute("user", sessionUser);
        return "Admin/Chat";
    }

    ///Account
//new employee
    @GetMapping("/CreateAccountEmployee")
    public String addEmpoyeeForm(Model model) {
        List<Role> roles = roleService.getAllRole();
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);

        return "Admin/CreateAccountEmployee";
    }
    @PostMapping("/CreateAccountEmployee")
    public String addACCOUNT(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRole());
            model.addAttribute("user", user);

            return "admin/CreateAccountEmployee";
        }
        userService.saveUser(user);
        return "redirect:/admin/listAccount";
    }
    @PostMapping("/saveEmployee")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        User existingUser = userService.findUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (result.hasErrors()) {
            if (existingUser.getUsername().equals(user.getUsername()) && existingUser.getEmail().equals(user.getEmail())) {
                result.rejectValue("username", "error.user", "This username and email are already taken");
                model.addAttribute("error", "This username and email are already taken");
            } else if (existingUser.getUsername().equals(user.getUsername())) {
                result.rejectValue("username", "error.user", "This username is already taken");
                model.addAttribute("error", "This username is already taken");
            } else {
                result.rejectValue("email", "error.user", "This email is already registered");
                model.addAttribute("error", "This email is already registered");
            }
            return "Admin/CreateAccountEmployee";
        }

        // Check if user already exists

        if (existingUser != null) {
            if (existingUser.getUsername().equals(user.getUsername()) && existingUser.getEmail().equals(user.getEmail())) {
                result.rejectValue("username", "error.user", "This username and email are already taken");
                model.addAttribute("error", "This username and email are already taken");
            } else if (existingUser.getUsername().equals(user.getUsername())) {
                result.rejectValue("username", "error.user", "This username is already taken");
                model.addAttribute("error", "This username is already taken");
            } else {
                result.rejectValue("email", "error.user", "This email is already registered");
                model.addAttribute("error", "This email is already registered");
            }
            return "Admin/CreateAccountEmployee";
        }

        String salt = PasswordUtils.getSalt(30);
        String hashedPassword = PasswordUtils.generateSecurePassword(user.getPassword(), salt);

        user.setSalt(salt);
        user.setPassword(hashedPassword);
        userService.saveEmployee(user);

        return "redirect:/admin/listAccount";
    }
//new admin
    @GetMapping("/CreateAccountAdmin")
    public String addAdminForm(Model model) {
        List<Role> roles = roleService.getAllRole();
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);

        return "Admin/CreateAccountAdmin";
    }
    @PostMapping("/CreateAccountAdmin")
    public String addACCOUNTAdmin(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRole());
            model.addAttribute("user", user);

            return "admin/CreateAccountEmployee";
        }
        userService.saveUser(user);
        return "redirect:/admin/listAccount";
    }
    @PostMapping("/saveAdmin")
    public String saveAccountAdmin(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {

            return "Admin/CreateAccountAdmin";
        }

        // Check if user already exists
        User existingUser = userService.findUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (existingUser != null) {
            if (existingUser.getUsername().equals(user.getUsername()) && existingUser.getEmail().equals(user.getEmail())) {
                result.rejectValue("username", "error.user", "This username and email are already taken");
                model.addAttribute("error", "This username and email are already taken");
            } else if (existingUser.getUsername().equals(user.getUsername())) {
                result.rejectValue("username", "error.user", "This username is already taken");
                model.addAttribute("error", "This username is already taken");
            } else {
                result.rejectValue("email", "error.user", "This email is already registered");
                model.addAttribute("error", "This email is already registered");
            }
            return "Admin/CreateAccountAdmin";
        }

        String salt = PasswordUtils.getSalt(30);
        String hashedPassword = PasswordUtils.generateSecurePassword(user.getPassword(), salt);

        user.setSalt(salt);
        user.setPassword(hashedPassword);
        userService.saveAdmin(user);

        return "redirect:/admin/listAccount";
    }
//
    @PostMapping("/savePhanQuyen")
    public String saveQuyen(@ModelAttribute("quyen") Role phanquyen) {
        roleService.saveRole(phanquyen);
        return "redirect:/admin/listAccount";
    }



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
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getUser(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        } else {
            user.getRoles().clear();
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        }
        return "redirect:/admin/listAccount";
    }
}
