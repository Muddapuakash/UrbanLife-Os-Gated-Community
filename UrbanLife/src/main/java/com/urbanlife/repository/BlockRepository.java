package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Block;
import com.urbanlife.enums.BlockStatus;

public interface BlockRepository
        extends JpaRepository<Block, Long> {

    List<Block> findByCommunityCommunityId(Long communityId);

    List<Block> findByStatus(BlockStatus status);

    boolean existsByBlockNameAndCommunityCommunityId(
            String blockName,
            Long communityId);

    boolean existsByBlockCodeAndCommunityCommunityId(
            String blockCode,
            Long communityId);
}