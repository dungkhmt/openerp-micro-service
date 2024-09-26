package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.SearchDTO;

public interface SearchService {
    SearchDTO getRecentActivity(String userId);

    SearchDTO search(String userId, String keyword);
}