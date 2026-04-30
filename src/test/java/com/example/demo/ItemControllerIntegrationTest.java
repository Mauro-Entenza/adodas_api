package com.example.demo;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.dto.ItemDto;
import com.example.demo.enumerate.CategoryEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private ItemDto buildValidItemDto() {
    return new ItemDto(
        0L,
        "Nike",
        "Red",
        CategoryEnum.SHOES,
        99.99f,
        true,
        LocalDate.of(2023, 1, 1)
    );
  }

  @Test
  void addItem_Returns201_WhenValid() throws Exception {
    ItemDto item = buildValidItemDto();

    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.brand").value("Nike"));
  }

  @Test
  void addItem_Returns400_WhenMissingRequiredFields() throws Exception {
    ItemDto item = new ItemDto();
    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation Failed"));
  }

  @Test
  void getAllItems_Returns200() throws Exception {
    mockMvc.perform(get("/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", isA(List.class)));
  }

  @Test
  void modifyItem_Returns200_WhenExists() throws Exception {
    ItemDto item = buildValidItemDto();
    String content = objectMapper.writeValueAsString(item);

    String response = mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    ItemDto created = objectMapper.readValue(response, ItemDto.class);
    created.setColor("Black");

    mockMvc.perform(put("/items/" + created.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.color").value("Black"));
  }

  @Test
  void modifyItem_Returns404_WhenNotFound() throws Exception {
    ItemDto item = buildValidItemDto();
    mockMvc.perform(put("/items/999999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteItem_Returns204_WhenExists() throws Exception {
    ItemDto item = buildValidItemDto();
    String response = mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andReturn().getResponse().getContentAsString();

    ItemDto created = objectMapper.readValue(response, ItemDto.class);

    mockMvc.perform(delete("/items/" + created.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteItem_Returns404_WhenNotFound() throws Exception {
    mockMvc.perform(delete("/items/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getItems_WithBrandFilter_ReturnsFiltered() throws Exception {
    // Crear item Nike
    ItemDto nikeItem = buildValidItemDto();
    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nikeItem)))
        .andExpect(status().isCreated());

    // Filtrar por brand="Nike"
    mockMvc.perform(get("/items")
            .param("brand", "Nike"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].brand").value("Nike"));
  }

  @Test
  void getItems_WithCategoryFilter_ReturnsFiltered() throws Exception {
    ItemDto shoesItem = buildValidItemDto();
    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shoesItem)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/items")
            .param("category", "SHOES"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].category").value("SHOES"));
  }

  @Test
  void getItems_WithPriceFilter_ReturnsFiltered() throws Exception {
    ItemDto expensiveItem = buildValidItemDto();
    expensiveItem.setPrice(99.99f);

    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expensiveItem)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/items")
            .param("price", "99.99"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].price").value(99.99f));
  }

  @Test
  void getItems_WithMultipleFilters_ReturnsIntersection() throws Exception {
    ItemDto nikeShoes = buildValidItemDto();
    mockMvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nikeShoes)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/items")
            .param("brand", "Nike")
            .param("category", "SHOES"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].brand").value("Nike"))
        .andExpect(jsonPath("$[0].category").value("SHOES"));
  }

  @Test
  void getItems_WithoutFilters_ReturnsAll() throws Exception {
    mockMvc.perform(get("/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

}
