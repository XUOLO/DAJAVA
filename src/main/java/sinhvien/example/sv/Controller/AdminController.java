package sinhvien.example.sv.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sinhvien.example.sv.Entity.User;
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
    private UserService userService;
    @GetMapping("/home")
    public String index(){
        return "Admin/layout";
    }
    @GetMapping("/CreateAccount")
    public String newAccount(){
        return "Admin/CreateAccount";
    }
    @GetMapping("listAccount")
    public String viewUser(Model model){

        String sql = "SELECT u.id, u.name, u.email, r.name AS role "
                + "FROM user u "
                + "JOIN user_role ur ON u.id = ur.user_id "
                + "JOIN role r ON ur.role_id = r.id";

        List<User> userList = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("listUser", userList);
        return "Admin/Account";
    }
    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id) {
        userService.deleteUserById(id);
        return "redirect:/admin/listAccount";
    }

}
