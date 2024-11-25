package com.example.demo.service;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.exception.ItemNotFoundException;
import java.util.List;

public interface ItemService {

  List<ItemDto> findAll();

  ItemDto addItem(ItemDto itemDto);

  ItemDto modify(long itemID, ItemDto itemDto) throws ItemNotFoundException;

  void deleteItem(long itemId) throws ItemNotFoundException;
}
