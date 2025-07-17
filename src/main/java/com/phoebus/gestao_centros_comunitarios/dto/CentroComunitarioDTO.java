package com.phoebus.gestao_centros_comunitarios.dto;

import com.phoebus.gestao_centros_comunitarios.model.Endereco;
import com.phoebus.gestao_centros_comunitarios.model.Recurso;
import lombok.Data;
import java.util.List;

@Data
public class CentroComunitarioDTO {
    private String nome;
    private Endereco endereco;
    private String localizacao;
    private Integer capacidadeMaxima;
    private Integer ocupacaoAtual;
    private List<Recurso> recursos;
}
