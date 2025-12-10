package tunutech.api.services;

import tunutech.api.dtos.ClientDto;
import tunutech.api.dtos.PaysResponsDTO;
import tunutech.api.model.Activity;
import tunutech.api.model.Client;

import java.time.LocalDate;
import java.util.List;

public interface ClientService {
    List<Client> allclient();
    List<Client> allclientPresent(Boolean present);
    List<Client> allClientCreatedAtPeriode(LocalDate date1,LocalDate date2,List<Client> list);
    List<Client> getClientsActivityofPeriode(List<Activity> activityList);
    List<PaysResponsDTO> allCountryclientPresent(List<Client> list);
    Client getUnique(Long id);

    Client getbyEmail(String email);
    Client getbyEmailbyForce(String email);

    Client saveClient(ClientDto clientDto);
    Client updateClient(ClientDto clientDto);
    Boolean ifClientisPresent(String email);

    Long Number0fClients();

}
