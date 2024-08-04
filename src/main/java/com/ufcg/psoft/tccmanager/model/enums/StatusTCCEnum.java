package com.ufcg.psoft.tccmanager.model.enums;

import com.ufcg.psoft.tccmanager.dto.temaTCC.AlterarStatusDTO;

public enum StatusTCCEnum {
    NOVO {
        @Override
        public void alterarStatus(AlterarStatusDTO alterarStatusDTO) {
            if (alterarStatusDTO.getAprovacao() == null)
                alterarStatusDTO.getTemaTCC().setStatus(StatusTCCEnum.PENDENTE);
            else {
                if (alterarStatusDTO.getAprovacao())
                    alterarStatusDTO.getTemaTCC().setStatus(StatusTCCEnum.ALOCADO);
                else
                    alterarStatusDTO.getTemaTCC().setStatus(StatusTCCEnum.NOVO);
            }
        }
    },
    PENDENTE {
        @Override
        public void alterarStatus(AlterarStatusDTO alterarStatusDTO) {
            if (alterarStatusDTO.getAprovacao())
                alterarStatusDTO.getTemaTCC().setStatus(StatusTCCEnum.ALOCADO);
            else
                alterarStatusDTO.getTemaTCC().setStatus(StatusTCCEnum.NOVO);
        }
    },
    ALOCADO {
        @Override
        public void alterarStatus(AlterarStatusDTO alterarStatusDTO) {

        }
    };

    public abstract void alterarStatus(AlterarStatusDTO alterarStatusDTO);
}
