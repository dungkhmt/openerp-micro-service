package com.hust.wmsbackend.management.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wmsv2/admin/inventory_item")
@CrossOrigin
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class InventoryItemController {

}
