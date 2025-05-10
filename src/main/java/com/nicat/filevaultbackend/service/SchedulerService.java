package com.nicat.filevaultbackend.service;

import org.springframework.stereotype.Service;

@Service
public interface SchedulerService {
    void expiredFileCleanUp();
}
