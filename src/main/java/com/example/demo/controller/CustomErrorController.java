package com.example.demo.controller;

            import jakarta.servlet.RequestDispatcher;
            import jakarta.servlet.http.HttpServletRequest;
            import org.springframework.http.HttpStatus;
            import org.springframework.stereotype.Controller;
            import org.springframework.ui.Model;
            import org.springframework.web.bind.annotation.RequestMapping;

            @Controller
            public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

                @RequestMapping("/error")
                public String handleError(HttpServletRequest request, Model model) {
                    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
                    String errorMsg = "Une erreur s'est produite";

                    if (status != null) {
                        int statusCode = Integer.parseInt(status.toString());

                        if (statusCode == HttpStatus.NOT_FOUND.value()) {
                            errorMsg = "Page non trouvée";
                        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                            errorMsg = "Erreur interne du serveur";
                        }
                    }

                    model.addAttribute("errorMessage", errorMsg);
                    return "error";
                }
            }