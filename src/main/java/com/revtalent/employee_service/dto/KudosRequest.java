package com.revtalent.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KudosRequest {
    private Long senderId;
    private Long receiverId;
    private String message;
    private String category;
    private String senderName;
    private String receiverName;
    private String receiverInitials;
}
