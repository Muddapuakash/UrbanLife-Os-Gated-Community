package com.urbanlife.service;

import com.urbanlife.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getCommunityDashboard(
            Long communityId);
}