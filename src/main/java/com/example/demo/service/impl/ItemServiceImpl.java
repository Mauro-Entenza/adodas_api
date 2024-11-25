package com.example.demo.service.impl;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.domain.entity.Item;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<ItemDto> findAll() {
    return this.modelMapper.map(this.itemRepository.findAll(), new TypeToken<List<ItemDto>>() {
    }.getType());
  }

  @Override
  public ItemDto addItem(ItemDto itemDto) {
    Item item = this.modelMapper.map(itemDto, Item.class);
    item = itemRepository.save(item);
    return this.modelMapper.map(item, ItemDto.class);
  }

  @Override
  public ItemDto modify(long itemID, ItemDto itemDto) throws ItemNotFoundException {
    this.itemRepository.findById(itemID).orElseThrow(ItemNotFoundException::new);
    return this.addItem(itemDto);
  }

  @Override
  public void deleteItem(long itemId) throws ItemNotFoundException {
    this.itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    this.itemRepository.deleteById(itemId);
  }
}
