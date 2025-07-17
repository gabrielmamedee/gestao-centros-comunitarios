package com.phoebus.gestao_centros_comunitarios.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "historico_intercambios")
public class HistoricoIntercambio {
    @Id
    private String id;
    private String centroOrigemId;
    private String centroDestinoId;
    private List<ItemIntercambio> itensOferecidosOrigem;
    private Integer pontosOferecidosOrigem;
    private List<ItemIntercambio> itensOferecidosDestino;
    private Integer pontosOferecidosDestino;
    private Date dataIntercambio;
    private boolean regraExcecaoAplicada;
}
