package com.example.demo.service;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.enumerate.CategoryEnum;
import com.example.demo.exception.ItemNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

public interface ItemService {

  List<ItemDto> findAll();

  ItemDto addItem(ItemDto itemDto);

  ItemDto modify(long itemID, ItemDto itemDto) throws ItemNotFoundException;

  ItemDto patchItem(long itemId, Map<String, Object> updates) throws ItemNotFoundException;

  void deleteItem(long itemId) throws ItemNotFoundException;

  List<ItemDto> getItemsByFilter(String brand, CategoryEnum category,
      Float price);

  // ✅ Método para GET con filtros directamente
  @Transactional(readOnly = true)
  List<ItemDto> findByFilters(String brand, CategoryEnum category, Float minPrice);
}
