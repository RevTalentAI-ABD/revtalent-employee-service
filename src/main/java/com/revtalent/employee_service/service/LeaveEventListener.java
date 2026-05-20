package com.revtalent.employee_service.service;

import com.revtalent.employee_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventListener {

    @RabbitListener(queues = RabbitMQConfig.LEAVE_APPLIED_QUEUE)
    public void handleLeaveApplied(Map<String, Object> event) {
        log.info("Received leave applied event: {}", event);

        Long employeeId = Long.valueOf(event.get("employeeId").toString());
        String employeeName = (String) event.get("employeeName");
        String leaveType = (String) event.get("leaveType");
        String startDate = (String) event.get("startDate");
        String endDate = (String) event.get("endDate");

        log.info("Employee {} ({}) applied for {} leave from {} to {}",
                employeeName, employeeId, leaveType, startDate, endDate);

        // Here you can add logic like:
        // - notify manager via email
        // - update employee status
        // - log activity
    }

    @RabbitListener(queues = RabbitMQConfig.LEAVE_STATUS_QUEUE)
    public void handleLeaveStatusUpdated(Map<String, Object> event) {
        log.info("Received leave status update event: {}", event);

        String status = (String) event.get("status");
        String employeeName = (String) event.get("employeeName");

        log.info("Leave for {} has been {}", employeeName, status);
    }
}