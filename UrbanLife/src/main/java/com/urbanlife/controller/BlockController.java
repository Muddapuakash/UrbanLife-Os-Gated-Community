package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.BlockResponse;
import com.urbanlife.dto.CreateBlockRequest;
import com.urbanlife.dto.UpdateBlockRequest;
import com.urbanlife.enums.BlockStatus;
import com.urbanlife.service.BlockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/blocks")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    // =====================================================
    // CREATE BLOCK
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<BlockResponse> createBlock(
            @Valid @RequestBody CreateBlockRequest request) {

        return new ResponseEntity<>(
                blockService.createBlock(request),
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET BLOCK BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{blockId}")
    public ResponseEntity<BlockResponse> getBlockById(
            @PathVariable Long blockId) {

        return ResponseEntity.ok(
                blockService.getBlockById(blockId)
        );
    }

    // =====================================================
    // GET ALL BLOCKS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping
    public ResponseEntity<List<BlockResponse>> getAllBlocks() {

        return ResponseEntity.ok(
                blockService.getAllBlocks()
        );
    }

    // =====================================================
    // GET BLOCKS BY COMMUNITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<BlockResponse>>
            getBlocksByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                blockService.getBlocksByCommunity(communityId)
        );
    }

    // =====================================================
    // SEARCH BLOCKS BY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/search/status")
    public ResponseEntity<List<BlockResponse>>
            getBlocksByStatus(
                    @RequestParam BlockStatus status) {

        return ResponseEntity.ok(
                blockService.getBlocksByStatus(status)
        );
    }

    // =====================================================
    // UPDATE BLOCK
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{blockId}")
    public ResponseEntity<BlockResponse> updateBlock(
            @PathVariable Long blockId,
            @Valid @RequestBody UpdateBlockRequest request) {

        return ResponseEntity.ok(
                blockService.updateBlock(blockId, request)
        );
    }

    // =====================================================
    // DELETE BLOCK
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{blockId}")
    public ResponseEntity<Void> deleteBlock(
            @PathVariable Long blockId) {

        blockService.deleteBlock(blockId);

        return ResponseEntity.noContent().build();
    }
}