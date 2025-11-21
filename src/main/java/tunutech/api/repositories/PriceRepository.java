package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Tarif;
import tunutech.api.model.TypeDocument;

public interface PriceRepository extends JpaRepository<Tarif, Long> {
    Tarif findByTypeDocument(TypeDocument typeDocument);
}
