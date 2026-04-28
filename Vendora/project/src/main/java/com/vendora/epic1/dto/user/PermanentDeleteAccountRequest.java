package com.vendora.epic1.dto.user;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermanentDeleteAccountRequest {

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirmation is required")
    private String confirmation;

}