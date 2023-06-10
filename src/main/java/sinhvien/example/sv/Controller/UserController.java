package sinhvien.example.sv.Controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.Ticket;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Security.PasswordUtils;
import sinhvien.example.sv.Service.DepartmentService;
import sinhvien.example.sv.Service.TicketService;
import sinhvien.example.sv.Service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping({"/users", "/"})
public class UserController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String index(HttpSession session, Model model) {
        List<Department> departmentList = departmentService.GetAllDepartment();
        model.addAttribute("departmentList", departmentList);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            String name = sessionUser.getName();
            model.addAttribute("name", name);
        }
        model.addAttribute("user", sessionUser);
        return "User/layout";
    }


    @GetMapping("contact")
    public String contact() {
        return "User/Contact";
    }

    @GetMapping("about")
    public String about() {
        return "User/about";
    }

    @GetMapping("service")
    public String service() {
        return "User/Service";
    }

    @GetMapping("chat")
    public String chat() {
        return "User/chat";
    }




    @GetMapping("/MyTicket")
    public String showTicketsForUser(Model model ) {


        return "User/MyTicket";
    }

    @GetMapping("/SubmitTicket")
    public String sendTicketForm(Model model) {
        List<Department> departments = departmentService.GetAllDepartment();
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("listDepartments", departments);

        return "User/SubmitTicket";
    }

    @PostMapping("/SubmitTicket")
    public String sendTicket(@Valid @ModelAttribute("ticket") Ticket ticket, HttpSession session, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws MessagingException {       if (bindingResult.hasErrors()) {
            model.addAttribute("ticket", ticket);
            model.addAttribute("listDepartments", departmentService.GetAllDepartment());
            return "users/ticket";
        }

        ticket.setCreateTime(LocalDateTime.now());
        ticket.setStatus("1");

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUser(userId);
        ticket.setUser(user);
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket has been submitted successfully! Please check your mail.");

        String name = ticket.getName();
        String phone = ticket.getPhone();
        String email = ticket.getEmail();
        String subject = ticket.getSubject();
        String message = ticket.getMessage();

        // Lấy thông tin phòng ban được chọn
        Long selectedDepartmentId = ticket.getDepartment().getId();
        Department selectedDepartment = departmentService.getDepartmentById(selectedDepartmentId);

        // Email người dùng
        MimeMessage userMimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper userHelper = new MimeMessageHelper(userMimeMessage, "utf-8");
        userHelper.setFrom("xuanloc290901@gmail.com"); // Email người gửi
        userHelper.setTo(email); // Email người nhận lấy từ form
        userHelper.setSubject(subject); // Tiêu đề
        userHelper.setText("Name: " + name + "\nPhone: " + phone + "\nMessage: " + message, false); // Nội dung
        mailSender.send(userMimeMessage); // Gửi email cho người dùng

        // Email cho phòng ban được chọn
        String departmentEmail = selectedDepartment.getMail();
        if (departmentEmail != null && departmentEmail.trim().length() > 0) {
            MimeMessage departmentMimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper departmentHelper = new MimeMessageHelper(departmentMimeMessage, "utf-8");
            departmentHelper.setFrom("xuanloc290901@gmail.com"); // Email người gửi
            departmentHelper.setTo(departmentEmail); // Email người nhận lấy từ thông tin phòng ban
            departmentHelper.setSubject(subject); // Tiêu đề
            departmentHelper.setText("bạn có 1 yêu cầu từ " + name + "\nPhone: " + phone + "\nMessage: " + message, false); // Nội dung
            // Thiết lập category khi gửi email
            departmentMimeMessage.setHeader("X-Mailer-Category", selectedDepartment.getName());
            mailSender.send(departmentMimeMessage); // Gửi email cho phòng ban được chọn
        }

        return "redirect:/users/SubmitTicket";
    }







    //Login register
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "User/register";
    }



    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "User/register";
        }

        String salt = PasswordUtils.getSalt(30);
        String hashedPassword = PasswordUtils.generateSecurePassword(user.getPassword(), salt);

        user.setSalt(salt);
        user.setPassword(hashedPassword);
        userService.saveUser(user);

        return "redirect:/Users/login";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
        User user = userService.findUserByUsername(username);

        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "user/login";
        }

        String hashedPassword = PasswordUtils.generateSecurePassword(password, user.getSalt());

        if (!user.getPassword().equals(hashedPassword)) {
            model.addAttribute("error", "Invalid username or password");
            return "user/login";
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("user", user);
        return "redirect:/users";
    }

}
