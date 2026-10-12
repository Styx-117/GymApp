package com.smartfit.gymApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.smartfit.gymApp.views.LoginView;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class GymAppApplication {
    public static void main(String[] args) {
        //  Debe ir ANTES de SpringApplication.run()
        System.setProperty("java.awt.headless", "false");

        SpringApplication app = new SpringApplication(GymAppApplication.class);
        app.setHeadless(false);          // <-- propiedad de Spring
        app.run(args);

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}