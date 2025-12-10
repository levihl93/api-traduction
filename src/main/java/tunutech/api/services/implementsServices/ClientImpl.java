package tunutech.api.services.implementsServices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.Utils.DateComparisonUtils;
import tunutech.api.dtos.ClientDto;
import tunutech.api.dtos.PaysResponsDTO;
import tunutech.api.model.*;
import tunutech.api.repositories.ClientRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.ActivityService;
import tunutech.api.services.ClientService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityService activityService;

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
    public List<Client> allClientCreatedAtPeriode(LocalDate date1, LocalDate date2,List<Client> list) {
        List<Client>clientList=new ArrayList<>();
        for(Client client:list)
        {
            if(DateComparisonUtils.isBetweenDate(client.getCreated_At(),date1,date2))
            {
                clientList.add(client);
            }
        }
        return clientList;
    }

    @Override
    public List<PaysResponsDTO> allCountryclientPresent(List<Client> clientList) {
        List<String>listresults=new ArrayList<>();
        List<PaysResponsDTO>listresultend=new ArrayList<>();
        boolean already=false;
        for(Client client:clientList)
        {
            already=false;
            for(String pays:listresults)
            {
                if(pays.equals(client.getPays()))
                {
                    already=true;
                }
            }
            if(!already)
            {
                PaysResponsDTO paysResponsDTO=new PaysResponsDTO();
                paysResponsDTO.setName(client.getPays());
                listresultend.add(paysResponsDTO);
                listresults.add(client.getPays());
            }
        }
        return listresultend;
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

    private Client getClientofUser(Long idUser)
    {
        Optional<User> user=userRepository.findById(Integer.valueOf(Math.toIntExact(idUser)));
        if(user.isPresent())
        {
            if(user.get().getClient()!=null)
            {
                return user.get().getClient();
            }
        }
        return null;
    }
    @Override
    public List<Client> getClientsActivityofPeriode( List<Activity> activityList)
    {
        List<Client> clientList=new ArrayList<>();
        for(Activity activity:activityList)
        {
            if(activity.getUserRole().equals(RoleUser.CLIENT))
            {
                Boolean deja=false;
                for(Client client:clientList)
                {
                    Optional<User> user=userRepository.findByClientId(client.getId());
                    if(user.isPresent())
                    {
                        if(user.get().getId()==activity.getUserId())
                        {
                            deja=true;
                        }
                    }
                }
                if(!deja)
                {
                    if(getClientofUser(activity.getUserId())!=null)
                    {
                        clientList.add(getClientofUser(activity.getUserId()));
                    }

                }
            }
        }
        return  clientList;
    }

}
