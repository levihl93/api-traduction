package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.model.Complexity;
import tunutech.api.model.ProjectComplexity;
import tunutech.api.repositories.ComplexityRepository;
import tunutech.api.services.ComplexityService;

import java.util.Optional;

@Service
public class ComplexityServiceImpl implements ComplexityService {

    @Autowired
    private ComplexityRepository complexityRepository;
    @Override
    public Complexity getofCompexity(ProjectComplexity projectComplexity) {
        return complexityRepository.findByProjectComplexity(projectComplexity);
        }
}
