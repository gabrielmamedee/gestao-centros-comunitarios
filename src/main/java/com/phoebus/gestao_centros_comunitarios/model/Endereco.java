package com.phoebus.gestao_centros_comunitarios.model;

import lombok.Data;

@Data
public class Endereco {
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
}
