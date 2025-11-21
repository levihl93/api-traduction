package tunutech.api.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-directory}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Normaliser le chemin - le uploadDir pointe déjà vers documents/
        String avatarsPath = uploadDir + "avatars/".replace("\\", "/");

        // S'assurer que le chemin se termine par /
        if (!avatarsPath.endsWith("/")) {
            avatarsPath += "/";
        }

        System.out.println("🔄 Chemin absolu des avatars: " + avatarsPath);
        System.out.println("📺 URL mapping: /documents/avatars/** → file:" + avatarsPath);

        // Vérifier si le dossier existe
        File avatarsDir = new File(avatarsPath);
        System.out.println("📁 Dossier existe: " + avatarsDir.exists());
        System.out.println("📁 Chemin absolu dossier: " + avatarsDir.getAbsolutePath());

        // Servir les avatars depuis documents/avatars/
        registry.addResourceHandler("/documents/avatars/**")
                .addResourceLocations("file:" + avatarsPath);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}