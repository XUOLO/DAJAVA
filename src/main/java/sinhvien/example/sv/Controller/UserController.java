package sinhvien.example.sv.Controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sinhvien.example.sv.Entity.User;


@Controller
@RequestMapping({"/users","/"})
public class UserController {



    @GetMapping("")
    public  String index (){
        return "User/layout";
    }
    @GetMapping("ticket")
    public  String ticket (){
        return "User/ticket";
    }
    @GetMapping("contact")
    public  String contact (){
        return "User/Contact";
    }
    @GetMapping("about")
    public  String about (){
        return "User/about";
    }
    @GetMapping("service")
    public  String service (){
        return "User/Service";
    }

    @GetMapping("chat")
    public  String chat (){
        return "User/chat";
    }
}
