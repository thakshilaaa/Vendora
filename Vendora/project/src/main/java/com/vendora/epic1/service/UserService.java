package com.vendora.epic1.service;

import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.user.DeleteAccountRequest;
import com.vendora.epic1.dto.user.PermanentDeleteAccountRequest;
import com.vendora.epic1.dto.user.ProfileResponse;
import com.vendora.epic1.dto.user.UpdateProfileRequest;
import com.vendora.epic1.dto.user.UserDetailsResponse;
import com.vendora.epic1.model.User;

public interface UserService {

    User getCurrentUser();

    ProfileResponse getCurrentUserProfile();

    ProfileResponse updateProfile(UpdateProfileRequest request);

    MessageResponse deleteAccount(DeleteAccountRequest request);

    MessageResponse permanentDeleteAccount(PermanentDeleteAccountRequest request);

    User getUserByEmail(String email);

    UserDetailsResponse getUserDetails(String email);

    UserDetailsResponse updateUserProfile(String email, UserDetailsResponse request);
}
