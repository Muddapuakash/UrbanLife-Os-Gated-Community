package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.CollectParcelRequest;
import com.urbanlife.dto.CreateParcelRequest;
import com.urbanlife.dto.ParcelResponse;
import com.urbanlife.dto.ReturnParcelRequest;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.Parcel;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.User;
import com.urbanlife.enums.DeliveryProvider;
import com.urbanlife.enums.ParcelStatus;
import com.urbanlife.enums.ParcelType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.ParcelRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.ParcelService;

@Service
public class ParcelServiceImpl
        implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final FlatRepository flatRepository;

    public ParcelServiceImpl(
            ParcelRepository parcelRepository,
            ResidentRepository residentRepository,
            UserRepository userRepository,
            CommunityRepository communityRepository,
            FlatRepository flatRepository) {

        this.parcelRepository = parcelRepository;
        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.flatRepository = flatRepository;
    }

    @Override
    @Transactional
    public ParcelResponse createParcel(
            CreateParcelRequest request) {

        Resident resident =
                residentRepository
                    .findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + request.getResidentId()));

        if (resident.getFlat() == null) {
            throw new IllegalArgumentException(
                "Resident is not assigned to a flat");
        }

        User receivedBy =
                userRepository
                    .findById(request.getReceivedByUserId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Receiving user not found with id: "
                            + request.getReceivedByUserId()));

        if (request.getDeliveryProvider()
                == DeliveryProvider.OTHER
                && (request.getProviderName() == null
                || request.getProviderName()
                    .isBlank())) {

            throw new IllegalArgumentException(
                "Provider name is required when delivery provider is OTHER");
        }

        Flat flat = resident.getFlat();

        Community community =
                flat.getBlock().getCommunity();

        Parcel parcel = new Parcel();

        parcel.setResident(resident);
        parcel.setFlat(flat);
        parcel.setCommunity(community);

        parcel.setParcelType(
                request.getParcelType());

        parcel.setDeliveryProvider(
                request.getDeliveryProvider());

        parcel.setProviderName(
                request.getProviderName());

        parcel.setTrackingNumber(
                request.getTrackingNumber());

        parcel.setDeliveryPersonName(
                request.getDeliveryPersonName());

        parcel.setDeliveryPersonPhone(
                request.getDeliveryPersonPhone());

        parcel.setDescription(
                request.getDescription());

        parcel.setReceivedBy(receivedBy);

        parcel.setStatus(
                ParcelStatus.RECEIVED);

        parcel.setReceivedAt(
                LocalDateTime.now());

        return mapToResponse(
                parcelRepository.save(parcel));
    }

    @Override
    public ParcelResponse getParcelById(
            Long parcelId) {

        return mapToResponse(
                findParcel(parcelId));
    }

    @Override
    public List<ParcelResponse> getAllParcels() {

        return mapList(
                parcelRepository.findAll());
    }

    @Override
    public List<ParcelResponse> getByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {

            throw new ResourceNotFoundException(
                "Resident not found with id: "
                + residentId);
        }

        return mapList(
                parcelRepository
                    .findByResidentResidentId(
                        residentId));
    }

    @Override
    public List<ParcelResponse> getByFlat(
            Long flatId) {

        if (!flatRepository.existsById(flatId)) {

            throw new ResourceNotFoundException(
                "Flat not found with id: "
                + flatId);
        }

        return mapList(
                parcelRepository
                    .findByFlatFlatId(flatId));
    }

    @Override
    public List<ParcelResponse> getByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return mapList(
                parcelRepository
                    .findByCommunityCommunityId(
                        communityId));
    }

    @Override
    public List<ParcelResponse> getByStatus(
            Long communityId,
            ParcelStatus status) {

        validateCommunity(communityId);

        return mapList(
                parcelRepository
                    .findByCommunityCommunityIdAndStatus(
                        communityId,
                        status));
    }

    @Override
    public List<ParcelResponse> getByType(
            Long communityId,
            ParcelType parcelType) {

        validateCommunity(communityId);

        return mapList(
                parcelRepository
                    .findByCommunityCommunityIdAndParcelType(
                        communityId,
                        parcelType));
    }

    @Override
    @Transactional
    public ParcelResponse markAsNotified(
            Long parcelId) {

        Parcel parcel = findParcel(parcelId);

        if (parcel.getStatus()
                != ParcelStatus.RECEIVED) {

            throw new IllegalArgumentException(
                "Only RECEIVED parcel can be marked as NOTIFIED");
        }

        parcel.setStatus(
                ParcelStatus.NOTIFIED);

        parcel.setNotifiedAt(
                LocalDateTime.now());

        return mapToResponse(
                parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelResponse collectParcel(
            Long parcelId,
            CollectParcelRequest request) {

        Parcel parcel = findParcel(parcelId);

        if (parcel.getStatus()
                != ParcelStatus.NOTIFIED) {

            throw new IllegalArgumentException(
                "Only NOTIFIED parcel can be collected");
        }

        parcel.setStatus(
                ParcelStatus.COLLECTED);

        parcel.setCollectedAt(
                LocalDateTime.now());

        parcel.setCollectedByName(
                request.getCollectedByName());

        return mapToResponse(
                parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelResponse returnParcel(
            Long parcelId,
            ReturnParcelRequest request) {

        Parcel parcel = findParcel(parcelId);

        if (parcel.getStatus()
                == ParcelStatus.COLLECTED) {

            throw new IllegalArgumentException(
                "Collected parcel cannot be returned");
        }

        if (parcel.getStatus()
                == ParcelStatus.RETURNED) {

            throw new IllegalArgumentException(
                "Parcel is already returned");
        }

        parcel.setStatus(
                ParcelStatus.RETURNED);

        parcel.setReturnedAt(
                LocalDateTime.now());

        parcel.setReturnReason(
                request.getReturnReason());

        return mapToResponse(
                parcelRepository.save(parcel));
    }

    @Override
    public long getPendingParcelCount(
            Long communityId) {

        validateCommunity(communityId);

        long received =
                parcelRepository
                    .countByCommunityCommunityIdAndStatus(
                        communityId,
                        ParcelStatus.RECEIVED);

        long notified =
                parcelRepository
                    .countByCommunityCommunityIdAndStatus(
                        communityId,
                        ParcelStatus.NOTIFIED);

        return received + notified;
    }

    private Parcel findParcel(Long parcelId) {

        return parcelRepository
                .findById(parcelId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Parcel not found with id: "
                        + parcelId));
    }

    private void validateCommunity(
            Long communityId) {

        if (!communityRepository
                .existsById(communityId)) {

            throw new ResourceNotFoundException(
                "Community not found with id: "
                + communityId);
        }
    }

    private List<ParcelResponse> mapList(
            List<Parcel> parcels) {

        return parcels.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ParcelResponse mapToResponse(
            Parcel parcel) {

        ParcelResponse response =
                new ParcelResponse();

        response.setParcelId(
                parcel.getParcelId());

        response.setResidentId(
                parcel.getResident()
                    .getResidentId());

        response.setResidentName(
                parcel.getResident()
                    .getUser()
                    .getFirstName()
                + " "
                + parcel.getResident()
                    .getUser()
                    .getLastName());

        response.setFlatId(
                parcel.getFlat()
                    .getFlatId());

        response.setFlatNumber(
                parcel.getFlat()
                    .getFlatNumber());

        response.setCommunityId(
                parcel.getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                parcel.getCommunity()
                    .getName());

        response.setParcelType(
                parcel.getParcelType());

        response.setDeliveryProvider(
                parcel.getDeliveryProvider());

        response.setProviderName(
                parcel.getProviderName());

        response.setTrackingNumber(
                parcel.getTrackingNumber());

        response.setDeliveryPersonName(
                parcel.getDeliveryPersonName());

        response.setDeliveryPersonPhone(
                parcel.getDeliveryPersonPhone());

        response.setDescription(
                parcel.getDescription());

        response.setStatus(
                parcel.getStatus());

        response.setReceivedByUserId(
                parcel.getReceivedBy()
                    .getUserId());

        response.setReceivedByName(
                parcel.getReceivedBy()
                    .getFirstName()
                + " "
                + parcel.getReceivedBy()
                    .getLastName());

        response.setReceivedAt(
                parcel.getReceivedAt());

        response.setNotifiedAt(
                parcel.getNotifiedAt());

        response.setCollectedAt(
                parcel.getCollectedAt());

        response.setReturnedAt(
                parcel.getReturnedAt());

        response.setCollectedByName(
                parcel.getCollectedByName());

        response.setReturnReason(
                parcel.getReturnReason());

        return response;
    }
}