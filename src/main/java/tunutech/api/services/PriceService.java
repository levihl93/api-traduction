package tunutech.api.services;

import tunutech.api.model.Tarif;
import tunutech.api.model.TypeDocument;

public interface PriceService {
    Tarif gettarif(String typeDocument);
}
