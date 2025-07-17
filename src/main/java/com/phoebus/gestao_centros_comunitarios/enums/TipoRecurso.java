package com.phoebus.gestao_centros_comunitarios.enums;

public enum TipoRecurso {
    MEDICO(4),
    VOLUNTARIO(3),
    KIT_SUPRIMENTOS_MEDICOS(7),
    VEICULO_TRANSPORTE(5),
    CESTA_BASICA(2);

    private final int pontos;

    TipoRecurso(int pontos) {
        this.pontos = pontos;
    }

    public int getPontos() {
        return pontos;
    }
}
