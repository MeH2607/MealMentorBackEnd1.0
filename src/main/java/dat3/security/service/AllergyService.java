package dat3.security.service;

import dat3.openai_demo.entity.Allergy;
import dat3.openai_demo.repository.AllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllergyService {

    private AllergyRepository allergyRepository;

    public AllergyService(AllergyRepository allergyRepository) {
        this.allergyRepository = allergyRepository;
    }


    public Allergy createAllergy(String name) {
        Allergy allergy = new Allergy();
        allergy.setName(name);
        return allergyRepository.save(allergy);
    }


}
