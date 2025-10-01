package com.graduationproject.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponseDTO<T> {
    private List<T> data;
    private long total;
    private String message;
    private boolean success;
}
