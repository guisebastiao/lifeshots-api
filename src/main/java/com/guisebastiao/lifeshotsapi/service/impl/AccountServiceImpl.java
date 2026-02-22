package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePrivacyRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public AccountServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, AuthenticatedUserProvider authenticatedUserProvider, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> setProfilePrivacy(ProfilePrivacyRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        if (profile.isPrivate() == dto.privacy()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, getMessage("services.account-service.methods.set-profile-privacy.conflict"));
        }

        profile.setPrivate(dto.privacy());
        profileRepository.save(profile);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> updatePassword(UpdatePasswordRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.account-service.methods.update-password.bad-request"));
        }

        user.setPassword(passwordEncoder.encode(dto.confirmPassword()));

        userRepository.save(user);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteAccount(DeleteAccountRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.account-service.methods.delete-account.bad-request"));
        }

        user.setDeleted(true);
        userRepository.save(user);

        return DefaultResponse.success();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
