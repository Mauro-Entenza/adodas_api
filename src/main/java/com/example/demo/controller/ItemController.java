package com.example.demo.controller;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.service.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

  private final Logger logger = LoggerFactory.getLogger(ItemController.class);
  @Autowired
  private ItemService itemService;

  @GetMapping("/items")
  public ResponseEntity<List<ItemDto>> getAll() {
    logger.info("BEGIN getAll");
    List<ItemDto> items = itemService.findAll();
    return new ResponseEntity<>(items, HttpStatus.OK);
  }

  @PostMapping("/items")
  public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto) {
    logger.info("BEGIN addItem");
    ItemDto newItem = itemService.addItem(itemDto);
    return new ResponseEntity<>(newItem, HttpStatus.CREATED);
  }
}
