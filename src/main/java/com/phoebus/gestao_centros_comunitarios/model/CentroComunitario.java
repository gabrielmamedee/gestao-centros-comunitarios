package com.phoebus.gestao_centros_comunitarios.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "centros_comunitarios")
public class CentroComunitario {
    @Id
    private String id;
    private String nome;
    private Endereco endereco;
    private String localizacao; // Ex: "-46.6333, -23.5505"
    private Integer capacidadeMaxima;
    private Integer ocupacaoAtual;
    private Double percentualOcupacao;
    private List<Recurso> recursos;

    public void calcularPercentualOcupacao() {
        if (capacidadeMaxima == null || capacidadeMaxima == 0) {
            this.percentualOcupacao = 0.0;
        } else {
            this.percentualOcupacao = ((double) this.ocupacaoAtual / this.capacidadeMaxima) * 100;
        }
    }
}