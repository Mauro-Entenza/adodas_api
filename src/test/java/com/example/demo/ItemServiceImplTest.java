package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.domain.entity.Item;
import com.example.demo.enumerate.CategoryEnum;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.impl.ItemServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class ItemServiceImplTest {

  private ItemRepository itemRepository;
  private ModelMapper modelMapper;
  private ItemServiceImpl itemService;

  @BeforeEach
  void setUp() {
    itemRepository = mock(ItemRepository.class);
    modelMapper = new ModelMapper();
    itemService = new ItemServiceImpl(itemRepository, modelMapper);
  }

  @Test
  void testFindAll() {
    Item item = getSampleItem();
    when(itemRepository.findAll()).thenReturn(List.of(item));

    List<ItemDto> result = itemService.findAll();

    assertEquals(1, result.size());
    assertEquals("Nike", result.get(0).getBrand());
  }

  @Test
  void testAddItem() {
    ItemDto dto = getSampleDto();
    Item item = modelMapper.map(dto, Item.class);
    item.setId(1L);

    when(itemRepository.save(any(Item.class))).thenReturn(item);

    ItemDto result = itemService.addItem(dto);

    assertEquals("Nike", result.getBrand());
    assertEquals(1L, result.getId());
  }

  @Test
  void testModifySuccess() throws ItemNotFoundException {
    Item existingItem = getSampleItem();
    ItemDto newDto = new ItemDto(1L, "Adidas", "Blue", CategoryEnum.SHOES, 89.99f, false,
        LocalDate.now());

    when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ItemDto result = itemService.modify(1L, newDto);

    assertEquals("Adidas", result.getBrand());
    assertEquals(89.99f, result.getPrice());
  }

  @Test
  void testModifyThrowsWhenNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ItemNotFoundException.class, () -> itemService.modify(99L, getSampleDto()));
  }

  @Test
  void testDeleteItemSuccess() throws ItemNotFoundException {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(getSampleItem()));
    doNothing().when(itemRepository).deleteById(1L);

    itemService.deleteItem(1L);
    verify(itemRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteItemThrowsWhenNotFound() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(99L));
  }

  private Item getSampleItem() {
    return new Item(1L, "Nike", "Black", CategoryEnum.SHOES, 59.99f, true, LocalDate.of(2022, 1, 1),
        null);
  }

  private ItemDto getSampleDto() {
    return new ItemDto(1L, "Nike", "Black", CategoryEnum.SHOES, 59.99f, true,
        LocalDate.of(2022, 1, 1));
  }
}
