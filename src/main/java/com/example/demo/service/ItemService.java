package com.example.demo.service;

import com.example.demo.domain.dto.ItemDto;
import java.util.List;

public interface ItemService {

  List<ItemDto> findAll();

  ItemDto addItem(ItemDto itemDto);
}
