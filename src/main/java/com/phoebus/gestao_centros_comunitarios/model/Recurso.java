package com.phoebus.gestao_centros_comunitarios.model;

import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {
    private TipoRecurso tipo;
    private Integer quantidade;
}
