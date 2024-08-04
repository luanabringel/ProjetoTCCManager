package com.ufcg.psoft.tccmanager.dto.temaTCC;

import com.ufcg.psoft.tccmanager.model.TemaTCC;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlterarStatusDTO {

    private TemaTCC temaTCC;

    private Boolean aprovacao;

}
