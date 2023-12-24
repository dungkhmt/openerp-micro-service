package com.hust.openerp.taskmanagement.service;

public interface NotificationService {
    void sendNotification(String from, String to, String content, String url);
}
