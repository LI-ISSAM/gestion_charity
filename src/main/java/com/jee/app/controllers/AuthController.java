package com.jee.app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.jee.app.enums.Role;
import com.jee.app.model.Organisation;
import com.jee.app.dto.LoginDTO;
import com.jee.app.dto.RegisterDTO;
import com.jee.app.services.OrganisationService;
import com.jee.app.model.Users;
import com.jee.app.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
@Controller
public class AuthController {

    private final UserService userService;
    private final OrganisationService organisationService;

    public AuthController(UserService userService, OrganisationService organisationService) {
        this.userService = userService;
        this.organisationService = organisationService;
    }

 @GetMapping("/register")
public String registerPage(Model model, HttpSession session) {
    // ✅ Si déjà connecté → redirige
    if (session.getAttribute("user") != null) {
        return "redirect:/";
    }
    model.addAttribute("registerDTO", new RegisterDTO());
    model.addAttribute("roles", Role.values());
    return "auth/register";
}

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO , BindingResult result , Model model){
        if (result.hasErrors()){
            return "auth/register";
        }
        try {
            userService.register(registerDTO);
            return "redirect:/login?success=true";
        }catch(RuntimeException e){
            model.addAttribute("error",e.getMessage());
            return "auth/register";
        }
    }

  @GetMapping("/login")
public String loginPage(Model model, HttpSession session) {
    // ✅ Si déjà connecté → redirige selon le rôle
    Users user = (Users) session.getAttribute("user");
    if (user != null) {
        if (user.getRole() == Role.ADMIN)
            return "redirect:/admin/dashboard";
        if (user.getRole() == Role.ORGANIZATION)
            return "redirect:/dashboard";
        return "redirect:/";
    }
    model.addAttribute("loginDTO", new LoginDTO());
    return "auth/login";
}

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO 
        ,BindingResult result , Model model,
        HttpSession session
    ){
        if(result.hasErrors()){
            return "auth/login";
        }
        try{
            Users user = userService.login(loginDTO);
            session.setAttribute("user", user);

           return "redirect:/";
        
                    
        }catch(RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
        
    }



    
    @GetMapping("/home")
    public String dashboard(HttpSession session , Model model){
        Users user = (Users) session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        Organisation org = organisationService.getByManager(user);
        model.addAttribute("organisation", org);

        return "dashboard";
    }

   @GetMapping("/logout")
public String logout(HttpSession session,
                     HttpServletResponse response) {
    // ✅ Invalide complètement la session
    session.invalidate();

    // ✅ Supprime les cookies de session
    jakarta.servlet.http.Cookie cookie =
            new jakarta.servlet.http.Cookie(
                    "JSESSIONID", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    // ✅ Headers anti-cache
    response.setHeader("Cache-Control",
            "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");

    return "redirect:/login?logout=true";
}
}

