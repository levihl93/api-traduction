package tunutech.api.services;

import tunutech.api.dtos.ClientDto;
import tunutech.api.model.Client;

import java.util.List;

public interface ClientService {
    List<Client> allclient();
    List<Client> allclientPresent(Boolean present);
    Client getUnique(Long id);

    Client getbyEmail(String email);
    Client getbyEmailbyForce(String email);

    Client saveClient(ClientDto clientDto);
    Client updateClient(ClientDto clientDto);
    Boolean ifClientisPresent(String email);

    Long Number0fClients();

}
