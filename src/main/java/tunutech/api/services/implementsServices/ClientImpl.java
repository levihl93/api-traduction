package tunutech.api.services.implementsServices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.ClientDto;
import tunutech.api.model.Client;
import tunutech.api.repositories.ClientRepository;
import tunutech.api.services.ClientService;

import java.util.List;
import java.util.Optional;

@Service
public class ClientImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<Client> allclient() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> allclientPresent(Boolean present) {
        return clientRepository.findByPresent(present);
    }

    @Override
    public Client getUnique(Long id) {
        Optional<Client>client=clientRepository.findById(id);
    if(client.isPresent())
    {
        return client.get();
    }throw  new RuntimeException("Client not found");
    }

    @Override
    public Client getbyEmail(String email) {
        Optional<Client>client=clientRepository.findByEmail(email);
        if(client.isPresent())
        {
            return client.get();
        }throw  new RuntimeException("Client not found");
    }

    @Override
    public Client getbyEmailbyForce(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found with email: " + email));
    }


    @Override
    public Client saveClient(ClientDto clientDto) {
        Client client=new Client();
        client.setEmail(clientDto.getEmail());
        client.setDenomination(clientDto.getDenomination());
        client.setPays(clientDto.getPays());
        client.setSexe(clientDto.getSexe());
        client.setFirstname(clientDto.getFirstname());
        client.setLastname(clientDto.getLastname());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        client.setSecteur(clientDto.getSecteur());
        client.setPresent(true);
        Client leclient=clientRepository.save(client);
        clientRepository.flush();
        return leclient;
    }

    @Override
    public Client updateClient(ClientDto clientDto) {
        Client client=this.getUnique(clientDto.getClientid());
        client.setEmail(clientDto.getEmail());
        client.setDenomination(clientDto.getDenomination());
        client.setPays(clientDto.getPays());
        client.setSexe(clientDto.getSexe());
        client.setFirstname(clientDto.getFirstname());
        client.setLastname(clientDto.getLastname());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        client.setSecteur(clientDto.getSecteur());
        Client leclient=clientRepository.save(client);
        return leclient;
    }

    @Override
    public Boolean ifClientisPresent(String email) {
        Optional<Client>client=clientRepository.findByEmail(email);
        boolean res=false;
        if(client.isPresent())
        {
           res=true;
        }
        return res;
    }

    @Override
    public Long Number0fClients() {
        return clientRepository.count();
    }
}
