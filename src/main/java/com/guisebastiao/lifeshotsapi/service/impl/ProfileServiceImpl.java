package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.ProfileService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public DefaultResponse<ProfileResponse> me() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        ProfileResponse data = this.profileMapper.toDTO(user.getProfile());

        return new DefaultResponse<ProfileResponse>(true, "Perfil retornado com sucesso", data);
    }

    @Override
    public DefaultResponse<ProfileResponse> findProfileById(String profileId) {
        Profile profile = this.profileRepository.findById(UUIDConverter.toUUID(profileId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        // VERIFICAR SE O PERFIL É PRIVADO E OS USUARIOS NÃO SE SEGUEM RETORNAR 403.

        ProfileResponse data = this.profileMapper.toDTO(profile);

        return new DefaultResponse<ProfileResponse>(true, "Perfil retornado com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<ProfileResponse> updateProfile(ProfileRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = user.getProfile();

        this.profileMapper.updateProfile(dto, profile);

        Profile savedProfile = this.profileRepository.save(profile);

        ProfileResponse data = this.profileMapper.toDTO(savedProfile);

        return new DefaultResponse<ProfileResponse>(true, "Perfil atualizado com sucesso", data);
    }
}
