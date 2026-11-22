package com.empresa.emprestimos.dto;

import com.empresa.emprestimos.model.TipoEquipamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroEquipamentoRequest(
        @NotBlank(message = "O patrimônio é obrigatório.")
        String patrimonio,

        @NotBlank(message = "O nome do equipamento é obrigatório.")
        String nome,

        @NotNull(message = "O tipo do equipamento é obrigatório.")
        TipoEquipamento tipo
) {}
