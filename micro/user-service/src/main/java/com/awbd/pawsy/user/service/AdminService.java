package com.awbd.pawsy.user.service;

import com.awbd.pawsy.user.dto.AdminStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;

    public AdminStats getStats() {
        return new AdminStats(0L,
                0L,
                0L,
                userService.count(),
                0L,
                0L,
                0L,
                0L,
                0L);
    }
}
