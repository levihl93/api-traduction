package tunutech.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileStorageConfig {
    @Value("${app.file-storage.base-directory}")
    private String baseDirectory;

    public String getBaseDirectory() {
        return baseDirectory;
    }
}
