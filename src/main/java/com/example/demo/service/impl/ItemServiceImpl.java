package com.example.demo.service.impl;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.domain.entity.Item;
import com.example.demo.enumerate.CategoryEnum;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemService;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public ItemServiceImpl(ItemRepository itemRepository, ModelMapper modelMapper) {
    this.itemRepository = itemRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public List<ItemDto> findAll() {
    return this.modelMapper.map(
        this.itemRepository.findAll(),
        new TypeToken<List<ItemDto>>() {
        }.getType()
    );
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
  public ItemDto patchItem(long itemId, Map<String, Object> updates) throws ItemNotFoundException {
    Item existingItem = itemRepository.findById(itemId)
        .orElseThrow(ItemNotFoundException::new);

    if (updates.containsKey("brand") && updates.get("brand") != null) {
      existingItem.setBrand((String) updates.get("brand"));
    }
    if (updates.containsKey("color") && updates.get("color") != null) {
      existingItem.setColor((String) updates.get("color"));
    }
    if (updates.containsKey("category") && updates.get("category") != null) {
      existingItem.setCategory(CategoryEnum.valueOf((String) updates.get("category")));
    }
    if (updates.containsKey("price") && updates.get("price") != null) {
      existingItem.setPrice(((Number) updates.get("price")).floatValue());
    }
    if (updates.containsKey("isWaterproof") && updates.get("isWaterproof") != null) {
      existingItem.setWaterproof((Boolean) updates.get("isWaterproof"));
    }
    if (updates.containsKey("releaseDate") && updates.get("releaseDate") != null) {
      existingItem.setReleaseDate(LocalDate.parse((String) updates.get("releaseDate")));
    }

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
    return itemRepository.findAll((root, query, cb) -> {
          Predicate predicate = cb.conjunction();

          if (brand != null && !brand.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
          }

          if (category != null) {
            predicate = cb.and(predicate, cb.equal(root.get("category"), category));
          }

          if (price != null) {
            // Usar rango para float/double, evita errores de precisión
            predicate = cb.and(predicate,
                cb.between(root.get("price"), price - 0.001f, price + 0.001f));
          }

          return predicate;
        }).stream()
        .map(item -> modelMapper.map(item, ItemDto.class))
        .toList();
  }

  // ✅ Método para GET con filtros directamente
  @Override
  @Transactional(readOnly = true)
  public List<ItemDto> findByFilters(String brand, CategoryEnum category, Float minPrice) {
    Specification<Item> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (brand != null && !brand.isEmpty()) {
        predicates.add(cb.equal(root.get("brand"), brand));
      }
      if (category != null) {
        predicates.add(cb.equal(root.get("category"), category));
      }
      if (minPrice != null) {
        predicates.add(cb.ge(root.get("price"), minPrice));
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };

    return itemRepository.findAll(spec).stream()
        .map(item -> modelMapper.map(item, ItemDto.class))
        .collect(Collectors.toList());
  }
}