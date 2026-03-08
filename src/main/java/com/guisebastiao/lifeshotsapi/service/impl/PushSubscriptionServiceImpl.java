package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.entity.Device;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.repository.DeviceRepository;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final DeviceRepository deviceRepository;
    private final UUIDConverter uuidConverter;
    private final MessageSource messageSource;

    @Value("${cookie.device-id.name}")
    private String cookieDeviceIdName;

    public PushSubscriptionServiceImpl(PushSubscriptionRepository pushSubscriptionRepository, DeviceRepository deviceRepository, UUIDConverter uuidConverter, MessageSource messageSource) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.deviceRepository = deviceRepository;
        this.uuidConverter = uuidConverter;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> subscribe(HttpServletRequest request, PushSubscriptionRequest dto) {
        String deviceIdRequest = getCookieByRequest(request, cookieDeviceIdName)
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.push-subscription.methods.subscribe.not-found-device-id")));

        Device device = deviceRepository.findByIdForUpdate(uuidConverter.toUUID(deviceIdRequest))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.push-subscription.methods.subscribe.not-found-device-id")));

        PushSubscription subscription = device.getPushSubscription();

        if (subscription == null) {
            subscription = new PushSubscription();
            subscription.setDevice(device);
            device.setPushSubscription(subscription);
        }

        subscription.setEndpoint(dto.endpoint());
        subscription.setP256dh(dto.keys().p256dh());
        subscription.setAuth(dto.keys().auth());

        pushSubscriptionRepository.save(subscription);

        return DefaultResponse.success();
    }

    private Optional<String> getCookieByRequest(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
