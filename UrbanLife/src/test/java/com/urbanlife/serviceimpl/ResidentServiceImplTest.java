package com.urbanlife.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.urbanlife.dto.CreateResidentRequest;
import com.urbanlife.dto.ResidentResponse;
import com.urbanlife.entity.Block;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.Role;
import com.urbanlife.entity.User;
import com.urbanlife.enums.FlatStatus;
import com.urbanlife.enums.FlatType;
import com.urbanlife.enums.OwnershipType;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.ResidentType;
import com.urbanlife.enums.RoleName;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.BlockRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ResidentServiceImplTest {

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlatRepository flatRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private ResidentServiceImpl residentService;

    private User user;
    private Flat flat;
    private Resident resident;
    private Block block;
    private Community community;

    @BeforeEach
    void setUp() {

        // COMMUNITY
        community = new Community();
        community.setCommunityId(1L);
        community.setName("Green Valley");

        // BLOCK
        block = new Block();
        block.setBlockId(1L);
        block.setBlockName("Block A");
        block.setCommunity(community);

        // FLAT
        flat = new Flat();
        flat.setFlatId(1L);
        flat.setFlatNumber("A-101");
        flat.setFloorNumber(1);
        flat.setFlatType(FlatType.TWO_BHK);
        flat.setOwnershipType(OwnershipType.OWNER_OCCUPIED);
        flat.setStatus(FlatStatus.VACANT);
        flat.setBlock(block);

        // ROLE
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName(RoleName.RESIDENT);

        // USER
        user = new User();
        user.setUserId(1L);
        user.setFirstName("Akash");
        user.setLastName("M");
        user.setEmail("akash@gmail.com");
        user.setPhone("9876543210");
        user.setRole(role);

        // RESIDENT
        resident = new Resident();
        resident.setResidentId(1L);
        resident.setUser(user);
        resident.setFlat(flat);
        resident.setResidentType(ResidentType.OWNER);
        resident.setStatus(ResidentStatus.ACTIVE);
        resident.setPrimaryResident(true);
        resident.setMoveInDate(
                LocalDate.of(2026, 1, 1));
        resident.setEmergencyContactName("Ravi");
        resident.setEmergencyContactPhone("9999999999");
    }

    // =====================================================
    // CREATE RESIDENT SUCCESS
    // =====================================================

    @Test
    void createResident_ShouldCreateSuccessfully() {

        CreateResidentRequest request =
                createRequest();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(residentRepository
                .existsByUserUserId(1L))
                .thenReturn(false);

        when(flatRepository.findById(1L))
                .thenReturn(Optional.of(flat));

        when(residentRepository
                .existsByFlatFlatIdAndPrimaryResidentTrue(1L))
                .thenReturn(false);

        when(residentRepository.save(
                any(Resident.class)))
                .thenAnswer(invocation -> {

                    Resident saved =
                            invocation.getArgument(0);

                    saved.setResidentId(1L);

                    return saved;
                });

        ResidentResponse response =
                residentService
                    .createResident(request);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getResidentId());

        assertEquals(
                1L,
                response.getUserId());

        assertEquals(
                "A-101",
                response.getFlatNumber());

        assertEquals(
                ResidentStatus.ACTIVE,
                response.getStatus());

        // Flat should become occupied
        assertEquals(
                FlatStatus.OCCUPIED,
                flat.getStatus());

        verify(residentRepository)
                .save(any(Resident.class));

        verify(flatRepository)
                .save(flat);
    }

    // =====================================================
    // USER NOT FOUND
    // =====================================================

    @Test
    void createResident_ShouldThrow_WhenUserNotFound() {

        CreateResidentRequest request =
                createRequest();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> residentService
                    .createResident(request));

        verify(
                residentRepository,
                never())
                .save(any());
    }

    // =====================================================
    // USER DOES NOT HAVE RESIDENT ROLE
    // =====================================================

    @Test
    void createResident_ShouldThrow_WhenUserIsNotResident() {

        CreateResidentRequest request =
                createRequest();

        Role adminRole = new Role();
        adminRole.setRoleName(RoleName.ADMIN);

        user.setRole(adminRole);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> residentService
                        .createResident(request));

        assertEquals(
                "Only users with RESIDENT role can have a resident profile",
                exception.getMessage());

        verify(
                residentRepository,
                never())
                .save(any());
    }

    // =====================================================
    // DUPLICATE RESIDENT PROFILE
    // =====================================================

    @Test
    void createResident_ShouldThrow_WhenProfileAlreadyExists() {

        CreateResidentRequest request =
                createRequest();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(residentRepository
                .existsByUserUserId(1L))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> residentService
                    .createResident(request));

        verify(
                residentRepository,
                never())
                .save(any());
    }

    // =====================================================
    // PRIMARY RESIDENT ALREADY EXISTS
    // =====================================================

    @Test
    void createResident_ShouldThrow_WhenPrimaryAlreadyExists() {

        CreateResidentRequest request =
                createRequest();

        request.setPrimaryResident(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(residentRepository
                .existsByUserUserId(1L))
                .thenReturn(false);

        when(flatRepository.findById(1L))
                .thenReturn(Optional.of(flat));

        when(residentRepository
                .existsByFlatFlatIdAndPrimaryResidentTrue(1L))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                    DuplicateResourceException.class,
                    () -> residentService
                        .createResident(request));

        assertEquals(
                "Flat already has a primary resident",
                exception.getMessage());

        verify(
                residentRepository,
                never())
                .save(any());
    }

    // =====================================================
    // GET RESIDENT BY ID
    // =====================================================

    @Test
    void getResidentById_ShouldReturnResident() {

        when(residentRepository.findById(1L))
                .thenReturn(Optional.of(resident));

        ResidentResponse response =
                residentService
                    .getResidentById(1L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getResidentId());

        assertEquals(
                "Akash M",
                response.getResidentName());

        assertEquals(
                "A-101",
                response.getFlatNumber());

        verify(residentRepository)
                .findById(1L);
    }

    // =====================================================
    // RESIDENT NOT FOUND
    // =====================================================

    @Test
    void getResidentById_ShouldThrow_WhenNotFound() {

        when(residentRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                    ResourceNotFoundException.class,
                    () -> residentService
                        .getResidentById(99L));

        assertEquals(
                "Resident not found with id: 99",
                exception.getMessage());
    }

    // =====================================================
    // GET BY USER
    // =====================================================

    @Test
    void getResidentByUserId_ShouldReturnResident() {

        when(residentRepository
                .findByUserUserId(1L))
                .thenReturn(Optional.of(resident));

        ResidentResponse response =
                residentService
                    .getResidentByUserId(1L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getUserId());

        assertEquals(
                "akash@gmail.com",
                response.getEmail());
    }

    // =====================================================
    // GET RESIDENTS BY FLAT
    // =====================================================

    @Test
    void getResidentsByFlat_ShouldReturnList() {

        when(flatRepository.findById(1L))
                .thenReturn(Optional.of(flat));

        when(residentRepository
                .findByFlatFlatId(1L))
                .thenReturn(List.of(resident));

        List<ResidentResponse> result =
                residentService
                    .getResidentsByFlat(1L);

        assertEquals(1, result.size());

        assertEquals(
                "A-101",
                result.get(0).getFlatNumber());
    }

    // =====================================================
    // GET RESIDENTS BY BLOCK
    // =====================================================

    @Test
    void getResidentsByBlock_ShouldReturnList() {

        when(blockRepository.existsById(1L))
                .thenReturn(true);

        when(residentRepository
                .findByFlatBlockBlockId(1L))
                .thenReturn(List.of(resident));

        List<ResidentResponse> result =
                residentService
                    .getResidentsByBlock(1L);

        assertEquals(1, result.size());

        assertEquals(
                1L,
                result.get(0).getBlockId());
    }

    // =====================================================
    // BLOCK NOT FOUND
    // =====================================================

    @Test
    void getResidentsByBlock_ShouldThrow_WhenBlockNotFound() {

        when(blockRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> residentService
                    .getResidentsByBlock(99L));

        verify(
                residentRepository,
                never())
                .findByFlatBlockBlockId(anyLong());
    }

    // =====================================================
    // GET BY COMMUNITY
    // =====================================================

    @Test
    void getResidentsByCommunity_ShouldReturnList() {

        when(communityRepository.existsById(1L))
                .thenReturn(true);

        when(residentRepository
                .findByFlatBlockCommunityCommunityId(1L))
                .thenReturn(List.of(resident));

        List<ResidentResponse> result =
                residentService
                    .getResidentsByCommunity(1L);

        assertEquals(1, result.size());

        assertEquals(
                1L,
                result.get(0).getCommunityId());

        assertEquals(
                "Green Valley",
                result.get(0).getCommunityName());
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Test
    void getResidentsByStatus_ShouldReturnList() {

        when(residentRepository
                .findByStatus(ResidentStatus.ACTIVE))
                .thenReturn(List.of(resident));

        List<ResidentResponse> result =
                residentService
                    .getResidentsByStatus(
                        ResidentStatus.ACTIVE);

        assertEquals(1, result.size());

        assertEquals(
                ResidentStatus.ACTIVE,
                result.get(0).getStatus());
    }

    // =====================================================
    // DELETE RESIDENT
    // =====================================================

    @Test
    void deleteResident_ShouldDeleteSuccessfully() {

        when(residentRepository.findById(1L))
                .thenReturn(Optional.of(resident));

        /*
         * After deleting this resident, assume
         * no active residents remain in the flat.
         */
        when(residentRepository
                .findByFlatFlatId(1L))
                .thenReturn(List.of());

        residentService.deleteResident(1L);

        verify(residentRepository)
                .delete(resident);

        assertEquals(
                FlatStatus.VACANT,
                flat.getStatus());

        verify(flatRepository)
                .save(flat);
    }

    // =====================================================
    // DELETE - KEEP FLAT OCCUPIED
    // =====================================================

    @Test
    void deleteResident_ShouldKeepFlatOccupied_WhenActiveResidentExists() {

        Resident secondResident =
                new Resident();

        secondResident.setResidentId(2L);
        secondResident.setStatus(
                ResidentStatus.ACTIVE);

        when(residentRepository.findById(1L))
                .thenReturn(Optional.of(resident));

        when(residentRepository
                .findByFlatFlatId(1L))
                .thenReturn(
                    List.of(secondResident));

        flat.setStatus(FlatStatus.OCCUPIED);

        residentService.deleteResident(1L);

        verify(residentRepository)
                .delete(resident);

        assertEquals(
                FlatStatus.OCCUPIED,
                flat.getStatus());

        verify(
                flatRepository,
                never())
                .save(flat);
    }

    // =====================================================
    // CREATE REQUEST
    // =====================================================

    private CreateResidentRequest createRequest() {

        CreateResidentRequest request =
                new CreateResidentRequest();

        request.setUserId(1L);
        request.setFlatId(1L);
        request.setResidentType(
                ResidentType.OWNER);
        request.setPrimaryResident(true);

        request.setMoveInDate(
                LocalDate.of(2026, 1, 1));

        request.setEmergencyContactName(
                "Ravi");

        request.setEmergencyContactPhone(
                "9999999999");

        return request;
    }
}