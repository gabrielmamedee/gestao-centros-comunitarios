package com.phoebus.gestao_centros_comunitarios.dto;

import com.phoebus.gestao_centros_comunitarios.enums.TipoRecurso;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaRecursoDTO {
    private TipoRecurso tipo;
    private double mediaPorCentro;
}
