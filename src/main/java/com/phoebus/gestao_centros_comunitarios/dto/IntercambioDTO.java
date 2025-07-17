package com.phoebus.gestao_centros_comunitarios.dto;

import com.phoebus.gestao_centros_comunitarios.model.ItemIntercambio;
import lombok.Data;
import java.util.List;

@Data
public class IntercambioDTO {
    private String centroOrigemId;
    private String centroDestinoId;
    private List<ItemIntercambio> itensOferecidosOrigem;
    private List<ItemIntercambio> itensOferecidosDestino;
}
