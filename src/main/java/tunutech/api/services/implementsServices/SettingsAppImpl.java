package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.model.SettingsApp;
import tunutech.api.repositories.SettingsAppRepository;
import tunutech.api.services.SettingAppService;

@Service
public class SettingsAppImpl implements SettingAppService {

    @Autowired
    private SettingsAppRepository settingsAppRepository;
    @Override
    public SettingsApp getSettingsApp() {
        SettingsApp settingsApp = null;
        for(SettingsApp settingsApp1:settingsAppRepository.findAll())
        {
            settingsApp=settingsApp1;
        }
        return  settingsApp;
    }
}
