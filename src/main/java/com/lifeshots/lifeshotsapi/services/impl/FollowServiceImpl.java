package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.FollowRequestDTO;
import com.lifeshots.lifeshotsapi.enums.NotificationType;
import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;
import com.lifeshots.lifeshotsapi.exceptions.EntityNotFoundException;
import com.lifeshots.lifeshotsapi.mappers.NotificationMapper;
import com.lifeshots.lifeshotsapi.models.*;
import com.lifeshots.lifeshotsapi.repositories.FollowRepository;
import com.lifeshots.lifeshotsapi.repositories.NotificationRepository;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.FollowService;
import com.lifeshots.lifeshotsapi.services.NotificationProducer;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public DefaultDTO follow(FollowRequestDTO followRequestDTO) {
        User follower = this.authProvider.getAuthenticatedUser();
        User following = this.findUser(followRequestDTO.userId());

        Follow follow = this.findFollowById(follower, following);

        if(follower.getId().equals(following.getId())) {
            throw new BadRequestException("Você não pode seguir você mesmo");
        }

        if(follow != null) {
            throw new BadRequestException("Você já segue " + following.getNickname());
        }

        Follow createFollow = new Follow();
        createFollow.setId(this.createPk(follower, following));
        createFollow.setFollowing(following);
        createFollow.setFollower(follower);

        this.followRepository.save(createFollow);

        this.notification(follower, following);

        return new DefaultDTO("Você está seguindo " + following.getNickname(), Boolean.TRUE, null, null, null);
    }

    @Override
    @Transactional
    public DefaultDTO unfollow(FollowRequestDTO followRequestDTO) {
        User follower = this.authProvider.getAuthenticatedUser();
        User following = this.findUser(followRequestDTO.userId());

        Follow follow = this.findFollowById(follower, following);

        if(follow == null) {
            throw new BadRequestException("Você ainda não segue " + following.getNickname());
        }

        Follow deleteFollow = new Follow();
        deleteFollow.setId(this.createPk(follower, following));
        deleteFollow.setFollowing(following);
        deleteFollow.setFollower(follower);

        this.followRepository.delete(deleteFollow);

        return new DefaultDTO("Você não está mais seguindo " + following.getNickname(), Boolean.TRUE, null, null, null);
    }

    private Follow findFollowById(User follower, User following) {
        return this.followRepository.findById(this.createPk(follower, following)).orElse(null);
    }

    private FollowPk createPk(User follower, User following) {
        FollowPk followPk = new FollowPk();
        followPk.setFollowerId(follower.getId());
        followPk.setFollowingId(following.getId());
        return followPk;
    }

    private User findUser(String userId) {
        return this.userRepository.findById(UUIDConverter.toUUID(userId))
                .orElseThrow(() -> new EntityNotFoundException("O usuário não foi encontrado"));
    }

    private void notification(User sender, User receiver) {
        String message = String.format("Você tem um novo seguidor", sender.getNickname());

        NotificationPk notificationPk = new NotificationPk();
        notificationPk.setSenderId(sender.getId());
        notificationPk.setReceiverId(receiver.getId());

        Notification notification = new Notification();
        notification.setId(notificationPk);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setType(NotificationType.NEW_FOLLOW);
        notification.setTitle("Novo seguidor");
        notification.setMessage(message);

        this.notificationRepository.save(notification);

        notificationProducer.sendNotification(this.notificationMapper.toDTO(notification));
    }
}
