package com.urbanlife.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.urbanlife.dto.AssignComplaintRequest;
import com.urbanlife.dto.ComplaintResponse;
import com.urbanlife.dto.CreateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintStatusRequest;
import com.urbanlife.entity.Block;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Complaint;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.Role;
import com.urbanlife.entity.User;
import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;
import com.urbanlife.enums.ComplaintStatus;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.RoleName;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ComplaintRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private Complaint complaint;
    private Resident resident;
    private User residentUser;
    private User staffUser;

    @BeforeEach
    void setUp() {

        Community community = new Community();
        community.setCommunityId(1L);
        community.setName("Green Valley");

        Block block = new Block();
        block.setBlockId(1L);
        block.setBlockName("Block A");
        block.setCommunity(community);

        Flat flat = new Flat();
        flat.setFlatId(1L);
        flat.setFlatNumber("A-101");
        flat.setBlock(block);

        Role residentRole = new Role();
        residentRole.setRoleName(RoleName.RESIDENT);

        residentUser = new User();
        residentUser.setUserId(1L);
        residentUser.setFirstName("Akash");
        residentUser.setLastName("M");
        residentUser.setRole(residentRole);

        resident = new Resident();
        resident.setResidentId(1L);
        resident.setUser(residentUser);
        resident.setFlat(flat);
        resident.setStatus(ResidentStatus.ACTIVE);

        Role staffRole = new Role();
        staffRole.setRoleName(RoleName.STAFF);

        staffUser = new User();
        staffUser.setUserId(2L);
        staffUser.setFirstName("Ravi");
        staffUser.setLastName("Kumar");
        staffUser.setRole(staffRole);

        complaint = new Complaint();
        complaint.setComplaintId(1L);
        complaint.setResident(resident);
        complaint.setTitle("Water Leakage");
        complaint.setDescription(
                "Water leaking near bathroom");
        complaint.setCategory(
                ComplaintCategory.PLUMBING);
        complaint.setPriority(
                ComplaintPriority.HIGH);
        complaint.setStatus(
                ComplaintStatus.OPEN);
    }

    // =====================================================
    // CREATE COMPLAINT SUCCESS
    // =====================================================

    @Test
    void createComplaint_ShouldCreateSuccessfully() {

        CreateComplaintRequest request =
                createRequest();

        when(residentRepository.findById(1L))
                .thenReturn(Optional.of(resident));

        when(complaintRepository.save(
                any(Complaint.class)))
                .thenAnswer(invocation -> {

                    Complaint saved =
                            invocation.getArgument(0);

                    saved.setComplaintId(1L);

                    return saved;
                });

        ComplaintResponse response =
                complaintService
                    .createComplaint(request);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getComplaintId());

        assertEquals(
                "Water Leakage",
                response.getTitle());

        assertEquals(
                ComplaintStatus.OPEN,
                response.getStatus());

        assertEquals(
                1L,
                response.getResidentId());

        verify(complaintRepository)
                .save(any(Complaint.class));
    }

    // =====================================================
    // INACTIVE RESIDENT CANNOT CREATE
    // =====================================================

    @Test
    void createComplaint_ShouldThrow_WhenResidentInactive() {

        resident.setStatus(
                ResidentStatus.INACTIVE);

        CreateComplaintRequest request =
                createRequest();

        when(residentRepository.findById(1L))
                .thenReturn(Optional.of(resident));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> complaintService
                        .createComplaint(request));

        assertEquals(
                "Only active residents can raise complaints",
                exception.getMessage());

        verify(
                complaintRepository,
                never())
                .save(any());
    }

    // =====================================================
    // COMPLAINT NOT FOUND
    // =====================================================

    @Test
    void getComplaintById_ShouldThrow_WhenNotFound() {

        when(complaintRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                    ResourceNotFoundException.class,
                    () -> complaintService
                        .getComplaintById(99L));

        assertEquals(
                "Complaint not found with id: 99",
                exception.getMessage());
    }

    // =====================================================
    // GET COMPLAINT SUCCESS
    // =====================================================

    @Test
    void getComplaintById_ShouldReturnComplaint() {

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        ComplaintResponse response =
                complaintService
                    .getComplaintById(1L);

        assertNotNull(response);

        assertEquals(
                "Water Leakage",
                response.getTitle());

        assertEquals(
                "Akash M",
                response.getResidentName());

        assertEquals(
                "A-101",
                response.getFlatNumber());
    }

    // =====================================================
    // ASSIGN COMPLAINT TO STAFF
    // =====================================================

    @Test
    void assignComplaint_ShouldAssignToStaff() {

        AssignComplaintRequest request =
                new AssignComplaintRequest();

        request.setUserId(2L);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(staffUser));

        when(complaintRepository.save(
                any(Complaint.class)))
                .thenAnswer(invocation ->
                    invocation.getArgument(0));

        ComplaintResponse response =
                complaintService
                    .assignComplaint(
                        1L,
                        request);

        assertEquals(
                ComplaintStatus.ASSIGNED,
                response.getStatus());

        assertEquals(
                2L,
                response.getAssignedToUserId());

        assertEquals(
                "Ravi Kumar",
                response.getAssignedToName());

        assertNotNull(
                response.getAssignedAt());

        verify(complaintRepository)
                .save(complaint);
    }

    // =====================================================
    // CANNOT ASSIGN TO RESIDENT
    // =====================================================

    @Test
    void assignComplaint_ShouldThrow_WhenAssignedToResident() {

        AssignComplaintRequest request =
                new AssignComplaintRequest();

        request.setUserId(1L);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(residentUser));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> complaintService
                        .assignComplaint(
                            1L,
                            request));

        assertEquals(
                "Complaint can only be assigned to STAFF or ADMIN",
                exception.getMessage());

        verify(
                complaintRepository,
                never())
                .save(any());
    }

    // =====================================================
    // ASSIGNED -> IN PROGRESS
    // =====================================================

    @Test
    void updateStatus_ShouldMoveAssignedToInProgress() {

        complaint.setStatus(
                ComplaintStatus.ASSIGNED);

        complaint.setAssignedTo(staffUser);

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        when(complaintRepository.save(
                any(Complaint.class)))
                .thenAnswer(invocation ->
                    invocation.getArgument(0));

        ComplaintResponse response =
                complaintService
                    .updateComplaintStatus(
                        1L,
                        request);

        assertEquals(
                ComplaintStatus.IN_PROGRESS,
                response.getStatus());
    }

    // =====================================================
    // IN PROGRESS REQUIRES ASSIGNMENT
    // =====================================================

    @Test
    void updateStatus_ShouldThrow_WhenNotAssigned() {

        /*
         * OPEN -> IN_PROGRESS is itself invalid,
         * therefore start with ASSIGNED.
         */
        complaint.setStatus(
                ComplaintStatus.ASSIGNED);

        complaint.setAssignedTo(null);

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> complaintService
                        .updateComplaintStatus(
                            1L,
                            request));

        assertEquals(
                "Complaint must be assigned before starting work",
                exception.getMessage());
    }

    // =====================================================
    // RESOLUTION NOTE REQUIRED
    // =====================================================

    @Test
    void resolveComplaint_ShouldThrow_WhenResolutionNoteMissing() {

        complaint.setStatus(
                ComplaintStatus.IN_PROGRESS);

        complaint.setAssignedTo(staffUser);

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.RESOLVED);

        request.setResolutionNote(null);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> complaintService
                        .updateComplaintStatus(
                            1L,
                            request));

        assertEquals(
                "Resolution note is required when resolving complaint",
                exception.getMessage());
    }

    // =====================================================
    // RESOLVE SUCCESS
    // =====================================================

    @Test
    void resolveComplaint_ShouldResolveSuccessfully() {

        complaint.setStatus(
                ComplaintStatus.IN_PROGRESS);

        complaint.setAssignedTo(staffUser);

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.RESOLVED);

        request.setResolutionNote(
                "Pipe replaced successfully");

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        when(complaintRepository.save(
                any(Complaint.class)))
                .thenAnswer(invocation ->
                    invocation.getArgument(0));

        ComplaintResponse response =
                complaintService
                    .updateComplaintStatus(
                        1L,
                        request);

        assertEquals(
                ComplaintStatus.RESOLVED,
                response.getStatus());

        assertEquals(
                "Pipe replaced successfully",
                response.getResolutionNote());

        assertNotNull(
                response.getResolvedAt());
    }

    // =====================================================
    // CLOSE RESOLVED COMPLAINT
    // =====================================================

    @Test
    void closeComplaint_ShouldCloseSuccessfully() {

        complaint.setStatus(
                ComplaintStatus.RESOLVED);

        complaint.setResolvedAt(
                LocalDateTime.now());

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.CLOSED);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        when(complaintRepository.save(
                any(Complaint.class)))
                .thenAnswer(invocation ->
                    invocation.getArgument(0));

        ComplaintResponse response =
                complaintService
                    .updateComplaintStatus(
                        1L,
                        request);

        assertEquals(
                ComplaintStatus.CLOSED,
                response.getStatus());

        assertNotNull(
                response.getClosedAt());
    }

    // =====================================================
    // INVALID STATUS TRANSITION
    // =====================================================

    @Test
    void updateStatus_ShouldThrow_ForInvalidTransition() {

        complaint.setStatus(
                ComplaintStatus.OPEN);

        UpdateComplaintStatusRequest request =
                new UpdateComplaintStatusRequest();

        request.setStatus(
                ComplaintStatus.RESOLVED);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> complaintService
                        .updateComplaintStatus(
                            1L,
                            request));

        assertTrue(
                exception.getMessage()
                    .contains(
                        "Invalid complaint status transition"));
    }

    // =====================================================
    // DELETE OPEN COMPLAINT
    // =====================================================

    @Test
    void deleteComplaint_ShouldDeleteOpenComplaint() {

        complaint.setStatus(
                ComplaintStatus.OPEN);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        complaintService
                .deleteComplaint(1L);

        verify(complaintRepository)
                .delete(complaint);
    }

    // =====================================================
    // CANNOT DELETE IN PROGRESS
    // =====================================================

    @Test
    void deleteComplaint_ShouldThrow_WhenNotOpen() {

        complaint.setStatus(
                ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(1L))
                .thenReturn(Optional.of(complaint));

        assertThrows(
                IllegalArgumentException.class,
                () -> complaintService
                    .deleteComplaint(1L));

        verify(
                complaintRepository,
                never())
                .delete(any());
    }

    // =====================================================
    // TEST REQUEST
    // =====================================================

    private CreateComplaintRequest createRequest() {

        CreateComplaintRequest request =
                new CreateComplaintRequest();

        request.setResidentId(1L);
        request.setTitle("Water Leakage");
        request.setDescription(
                "Water leaking near bathroom");

        request.setCategory(
                ComplaintCategory.PLUMBING);

        request.setPriority(
                ComplaintPriority.HIGH);

        return request;
    }
}