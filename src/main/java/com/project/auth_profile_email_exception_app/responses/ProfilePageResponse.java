package com.project.auth_profile_email_exception_app.responses;

import com.project.auth_profile_email_exception_app.dto.ProfileDto;

import java.util.List;

public record ProfilePageResponse(List<ProfileDto> profileDtoList,
                                  Integer pageNumber,
                                  Integer pageSize,
                                  long totalElements,
                                  int totalPages,
                                  boolean isLast) {
}
