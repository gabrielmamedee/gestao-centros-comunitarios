package com.phoebus.gestao_centros_comunitarios.model;

import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import lombok.Data;

@Data
public class ItemIntercambio {
    private TipoRecurso tipoRecurso;
    private Integer quantidade;
}
