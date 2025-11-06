package com.example.food_delivery.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Get current working directory
            String currentDir = System.getProperty("user.dir");
            
            // Check if we're in food_delivery directory
            String projectRoot;
            if (currentDir.endsWith("food_delivery")) {
                // We're in food_delivery, go up one level
                projectRoot = new File(currentDir).getParent();
            } else {
                // We're at project root
                projectRoot = currentDir;
            }
            
            // Serve theme-sidebar frontend files
            File themeSidebarDir = new File(projectRoot, "theme-sidebar");
            String themeSidebarPath = themeSidebarDir.getAbsolutePath().replace("\\", "/") + "/";
            
            if (themeSidebarDir.exists()) {
                registry.addResourceHandler("/theme/**")
                        .addResourceLocations("file:" + themeSidebarPath)
                        .setCachePeriod(3600);
                System.out.println("✅ Theme-sidebar configured: " + themeSidebarPath);
            } else {
                System.err.println("❌ Theme-sidebar directory not found: " + themeSidebarPath);
            }

            // Serve admin frontend files
            File adminDir = new File(projectRoot, "admin");
            String adminPath = adminDir.getAbsolutePath().replace("\\", "/") + "/";
            
            if (adminDir.exists()) {
                registry.addResourceHandler("/admin/**")
                        .addResourceLocations("file:" + adminPath)
                        .setCachePeriod(3600);
                System.out.println("✅ Admin configured: " + adminPath);
            } else {
                System.err.println("❌ Admin directory not found: " + adminPath);
            }

            // Serve theme-sidebar as root (only if exists)
            if (themeSidebarDir.exists()) {
                registry.addResourceHandler("/")
                        .addResourceLocations("file:" + themeSidebarPath)
                        .setCachePeriod(3600);
            }
            
            // Serve uploaded files from uploads folder
            File uploadsDir = new File(projectRoot, "uploads");
            String uploadsPath = uploadsDir.getAbsolutePath().replace("\\", "/") + "/";
            
            if (uploadsDir.exists()) {
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + uploadsPath)
                        .setCachePeriod(3600);
                System.out.println("✅ Uploads folder configured: " + uploadsPath);
            } else {
                System.err.println("⚠️ Uploads directory not found: " + uploadsPath);
            }
            
            System.out.println("📁 Project root: " + projectRoot);
        } catch (Exception e) {
            System.err.println("❌ Error configuring static resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

