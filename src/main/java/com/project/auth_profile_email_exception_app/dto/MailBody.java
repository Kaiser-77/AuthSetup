package com.project.auth_profile_email_exception_app.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
