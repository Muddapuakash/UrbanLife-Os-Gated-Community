package com.urbanlife.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.urbanlife.dto.CommunityResponse;
import com.urbanlife.dto.CreateCommunityRequest;
import com.urbanlife.dto.UpdateCommunityRequest;
import com.urbanlife.entity.Community;
import com.urbanlife.enums.CommunityStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {

    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private CommunityServiceImpl communityService;

    private Community community;

    // =====================================================
    // SETUP
    // =====================================================

    @BeforeEach
    void setUp() {

        community = new Community();

        community.setCommunityId(1L);
        community.setName("Green Valley");
        community.setRegistrationNumber("REG001");
        community.setEmail("greenvalley@gmail.com");
        community.setPhone("9876543210");
        community.setAddressLine("Madhapur");
        community.setCity("Hyderabad");
        community.setState("Telangana");
        community.setPincode("500081");
        community.setStatus(CommunityStatus.ACTIVE);
    }

    // =====================================================
    // CREATE COMMUNITY - SUCCESS
    // =====================================================

    @Test
    void createCommunity_ShouldCreateCommunitySuccessfully() {

        CreateCommunityRequest request =
                createCommunityRequest();

        when(communityRepository.existsByName(
                request.getName()))
                .thenReturn(false);

        when(communityRepository.existsByRegistrationNumber(
                request.getRegistrationNumber()))
                .thenReturn(false);

        when(communityRepository.save(any(Community.class)))
                .thenAnswer(invocation -> {

                    Community savedCommunity =
                            invocation.getArgument(0);

                    savedCommunity.setCommunityId(1L);

                    return savedCommunity;
                });

        CommunityResponse response =
                communityService.createCommunity(request);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getCommunityId());

        assertEquals(
                "Green Valley",
                response.getName());

        assertEquals(
                "Hyderabad",
                response.getCity());

        assertEquals(
                CommunityStatus.ACTIVE,
                response.getStatus());

        verify(communityRepository)
                .existsByName("Green Valley");

        verify(communityRepository)
                .existsByRegistrationNumber("REG001");

        verify(communityRepository)
                .save(any(Community.class));
    }

    // =====================================================
    // CREATE COMMUNITY - DUPLICATE NAME
    // =====================================================

    @Test
    void createCommunity_ShouldThrowException_WhenNameExists() {

        CreateCommunityRequest request =
                createCommunityRequest();

        when(communityRepository.existsByName(
                request.getName()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                    DuplicateResourceException.class,
                    () -> communityService
                        .createCommunity(request));

        assertEquals(
                "Community already exists with name: Green Valley",
                exception.getMessage());

        verify(communityRepository)
                .existsByName("Green Valley");

        verify(
                communityRepository,
                never())
                .save(any(Community.class));
    }

    // =====================================================
    // CREATE COMMUNITY - DUPLICATE REGISTRATION NUMBER
    // =====================================================

    @Test
    void createCommunity_ShouldThrowException_WhenRegistrationExists() {

        CreateCommunityRequest request =
                createCommunityRequest();

        when(communityRepository.existsByName(
                request.getName()))
                .thenReturn(false);

        when(communityRepository.existsByRegistrationNumber(
                request.getRegistrationNumber()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                    DuplicateResourceException.class,
                    () -> communityService
                        .createCommunity(request));

        assertEquals(
                "Registration number already exists: REG001",
                exception.getMessage());

        verify(
                communityRepository,
                never())
                .save(any(Community.class));
    }

    // =====================================================
    // GET COMMUNITY BY ID - SUCCESS
    // =====================================================

    @Test
    void getCommunityById_ShouldReturnCommunity() {

        when(communityRepository.findById(1L))
                .thenReturn(Optional.of(community));

        CommunityResponse response =
                communityService
                    .getCommunityById(1L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getCommunityId());

        assertEquals(
                "Green Valley",
                response.getName());

        assertEquals(
                "Hyderabad",
                response.getCity());

        verify(communityRepository)
                .findById(1L);
    }

    // =====================================================
    // GET COMMUNITY BY ID - NOT FOUND
    // =====================================================

    @Test
    void getCommunityById_ShouldThrowException_WhenNotFound() {

        when(communityRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                    ResourceNotFoundException.class,
                    () -> communityService
                        .getCommunityById(99L));

        assertEquals(
                "Community not found with id: 99",
                exception.getMessage());

        verify(communityRepository)
                .findById(99L);
    }

    // =====================================================
    // GET ALL COMMUNITIES
    // =====================================================

    @Test
    void getAllCommunities_ShouldReturnCommunityList() {

        Community secondCommunity =
                new Community();

        secondCommunity.setCommunityId(2L);
        secondCommunity.setName("Lake View");
        secondCommunity.setRegistrationNumber("REG002");
        secondCommunity.setEmail("lakeview@gmail.com");
        secondCommunity.setPhone("9876543211");
        secondCommunity.setAddressLine("Kondapur");
        secondCommunity.setCity("Hyderabad");
        secondCommunity.setState("Telangana");
        secondCommunity.setPincode("500084");
        secondCommunity.setStatus(
                CommunityStatus.ACTIVE);

        when(communityRepository.findAll())
                .thenReturn(
                    List.of(
                        community,
                        secondCommunity));

        List<CommunityResponse> responses =
                communityService
                    .getAllCommunities();

        assertNotNull(responses);

        assertEquals(
                2,
                responses.size());

        assertEquals(
                "Green Valley",
                responses.get(0).getName());

        assertEquals(
                "Lake View",
                responses.get(1).getName());

        verify(communityRepository)
                .findAll();
    }

    // =====================================================
    // UPDATE COMMUNITY - SUCCESS
    // =====================================================

    @Test
    void updateCommunity_ShouldUpdateSuccessfully() {

        UpdateCommunityRequest request =
                new UpdateCommunityRequest();

        request.setName("Green Valley Updated");
        request.setRegistrationNumber("REG001");
        request.setEmail("updated@gmail.com");
        request.setPhone("9999999999");
        request.setAddressLine("Hitech City");
        request.setCity("Hyderabad");
        request.setState("Telangana");
        request.setPincode("500081");
        request.setStatus(
                CommunityStatus.ACTIVE);

        when(communityRepository.findById(1L))
                .thenReturn(Optional.of(community));

        when(communityRepository.existsByName(
                "Green Valley Updated"))
                .thenReturn(false);

        when(communityRepository.save(
                any(Community.class)))
                .thenAnswer(
                    invocation ->
                        invocation.getArgument(0));

        CommunityResponse response =
                communityService.updateCommunity(
                        1L,
                        request);

        assertNotNull(response);

        assertEquals(
                "Green Valley Updated",
                response.getName());

        assertEquals(
                "updated@gmail.com",
                response.getEmail());

        assertEquals(
                "Hitech City",
                response.getAddressLine());

        verify(communityRepository)
                .findById(1L);

        verify(communityRepository)
                .save(any(Community.class));
    }

    // =====================================================
    // UPDATE - DUPLICATE NAME
    // =====================================================

    @Test
    void updateCommunity_ShouldThrowException_WhenNewNameExists() {

        UpdateCommunityRequest request =
                new UpdateCommunityRequest();

        request.setName("Existing Community");
        request.setRegistrationNumber("REG001");

        when(communityRepository.findById(1L))
                .thenReturn(Optional.of(community));

        when(communityRepository.existsByName(
                "Existing Community"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> communityService
                    .updateCommunity(
                        1L,
                        request));

        verify(
                communityRepository,
                never())
                .save(any(Community.class));
    }

    // =====================================================
    // DELETE COMMUNITY - SUCCESS
    // =====================================================

    @Test
    void deleteCommunity_ShouldDeleteSuccessfully() {

        when(communityRepository.findById(1L))
                .thenReturn(Optional.of(community));

        communityService.deleteCommunity(1L);

        verify(communityRepository)
                .findById(1L);

        verify(communityRepository)
                .delete(community);
    }

    // =====================================================
    // DELETE COMMUNITY - NOT FOUND
    // =====================================================

    @Test
    void deleteCommunity_ShouldThrowException_WhenNotFound() {

        when(communityRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> communityService
                    .deleteCommunity(99L));

        verify(
                communityRepository,
                never())
                .delete(any(Community.class));
    }

    // =====================================================
    // GET BY CITY
    // =====================================================

    @Test
    void getCommunitiesByCity_ShouldReturnCommunities() {

        when(communityRepository.findByCity(
                "Hyderabad"))
                .thenReturn(List.of(community));

        List<CommunityResponse> responses =
                communityService
                    .getCommunitiesByCity(
                        "Hyderabad");

        assertNotNull(responses);

        assertFalse(responses.isEmpty());

        assertEquals(
                1,
                responses.size());

        assertEquals(
                "Hyderabad",
                responses.get(0).getCity());

        verify(communityRepository)
                .findByCity("Hyderabad");
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Test
    void getCommunitiesByStatus_ShouldReturnCommunities() {

        when(communityRepository.findByStatus(
                CommunityStatus.ACTIVE))
                .thenReturn(List.of(community));

        List<CommunityResponse> responses =
                communityService
                    .getCommunitiesByStatus(
                        CommunityStatus.ACTIVE);

        assertNotNull(responses);

        assertEquals(
                1,
                responses.size());

        assertEquals(
                CommunityStatus.ACTIVE,
                responses.get(0).getStatus());

        verify(communityRepository)
                .findByStatus(
                    CommunityStatus.ACTIVE);
    }

    // =====================================================
    // TEST DATA
    // =====================================================

    private CreateCommunityRequest
            createCommunityRequest() {

        CreateCommunityRequest request =
                new CreateCommunityRequest();

        request.setName("Green Valley");
        request.setRegistrationNumber("REG001");
        request.setEmail("greenvalley@gmail.com");
        request.setPhone("9876543210");
        request.setAddressLine("Madhapur");
        request.setCity("Hyderabad");
        request.setState("Telangana");
        request.setPincode("500081");

        return request;
    }
}