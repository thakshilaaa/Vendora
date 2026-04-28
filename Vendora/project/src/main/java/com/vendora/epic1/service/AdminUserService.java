package com.vendora.epic1.service;

import com.vendora.epic1.dto.admin.UpdateUserRoleRequest;
import com.vendora.epic1.dto.admin.UpdateUserStatusRequest;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.user.UserResponse;

import java.util.List;

public interface AdminUserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    MessageResponse updateUserRole(Long userId, UpdateUserRoleRequest request);

    MessageResponse updateUserStatus(Long userId, UpdateUserStatusRequest request);
}
