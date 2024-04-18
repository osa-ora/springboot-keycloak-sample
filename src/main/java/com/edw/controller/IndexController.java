package com.edw.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
/**
 * <pre>
 *     com.edw.controller.IndexController
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 21 Mar 2023 20:09
 * 
 * Modified by Osama Oransa
 * 
 */
@RestController
public class IndexController {

    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.security.oauth2.client.provider.external.issuer-uri}")
    private String ssoServer;

    @GetMapping(path = "/")
    public HashMap homePage() {
        // get a successful user login
        OAuth2User user = ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new HashMap(){{
            for (String key : user.getAttributes().keySet()) {
                System.out.println(key+"="+user.getAttribute(key));
            }
            put("Welcome", user.getAttribute("name"));
            put("Auth Time", user.getAttribute("auth_time"));
            put("Your email is", user.getAttribute("email"));
            put("View Account Details",ssoServer+"/account");
            put("Resource Access",user.getAttribute("resource_access"));
            put("To access admin page:","http://localhost:"+serverPort+"/admin");
            put("To logout:","http://localhost:"+serverPort+"/logout");
        }};
    }


    @GetMapping(path = "/admin")
    public HashMap adminAccess() {
        OAuth2User user = ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (user.getAttribute("resource_access")!=null && !user.getAttribute("resource_access").toString().contains("BIG_USER")) {
            // If the user does not have the required role, return unauthorized response
            return new HashMap(){{
                put("Error", "You are not authorized to access this resource");
            }};
        }
        return new HashMap(){{
            put("This service", "is restricted endpoint for Big User Only");
            put("hello", user.getAttribute("name")+"("+user.getAttribute("email")+")");
            put("To logout:","http://localhost:"+serverPort+"/logout");
        }};
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Add any additional logout logic here
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext(); // Clear the security context
        return "Logged out successfully!";
    }

    @GetMapping(path = "/public")
    public HashMap publicAccessService() {
        return new HashMap(){{
            put("This service", "publicly exposed endpoint");
            put("To login:","http://localhost:"+serverPort+"/");

        }};
    }
}
