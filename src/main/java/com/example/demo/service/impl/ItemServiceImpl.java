package com.example.demo.service.impl;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.domain.entity.Item;
import com.example.demo.enumerate.CategoryEnum;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemService;
import jakarta.persistence.criteria.Predicate;
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
  public ItemDto modify(long itemId, ItemDto itemDto) throws ItemNotFoundException {
    Item existingItem = itemRepository.findById(itemId)
        .orElseThrow(ItemNotFoundException::new);
    existingItem.setBrand(itemDto.getBrand());
    existingItem.setColor(itemDto.getColor());
    existingItem.setCategory(itemDto.getCategory());
    existingItem.setPrice(itemDto.getPrice());
    existingItem.setWaterproof(itemDto.isWaterproof());
    existingItem.setReleaseDate(itemDto.getReleaseDate());
    Item updatedItem = itemRepository.save(existingItem);
    return modelMapper.map(updatedItem, ItemDto.class);
  }


  @Override
  public void deleteItem(long itemId) throws ItemNotFoundException {
    this.itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    this.itemRepository.deleteById(itemId);
  }

  @Override
  public List<ItemDto> getItemsByFilter(String brand, CategoryEnum category, Float price) {
    return itemRepository.findAll((root, query, criteriaBuilder) -> {
          Predicate predicate = criteriaBuilder.conjunction();

          if (brand != null && !brand.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("brand"), brand));
          }

          if (category != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(root.get("category"), category));
          }

          if (price != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
          }

          return predicate;
        }).stream()
        .map(item -> modelMapper.map(item, ItemDto.class))
        .toList();
  }


}
