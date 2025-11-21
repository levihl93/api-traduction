package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.model.Tarif;
import tunutech.api.model.TypeDocument;
import tunutech.api.repositories.PriceRepository;
import tunutech.api.services.PriceService;

@Service
public class TarifImpl implements PriceService {
    @Autowired
    private PriceRepository priceRepository;

    @Override
    public Tarif gettarif(String typeDocument) {
        try{
            TypeDocument typeDocument1=TypeDocument.valueOf(typeDocument.toUpperCase());
            Tarif tarif=priceRepository.findByTypeDocument(TypeDocument.valueOf(typeDocument));
            if(!(tarif ==null))
            {
                return tarif;
            }throw  new RuntimeException("Aucun Prix fixé pour ce type de Document");
        }catch (IllegalArgumentException e)
        {
            throw new RuntimeException("Type de Document non reconnu");
        }

    }
}
