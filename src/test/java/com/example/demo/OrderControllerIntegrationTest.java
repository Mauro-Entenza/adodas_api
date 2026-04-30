package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.dto.OrderDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Item;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ItemRepository itemRepository;

  private Customer testCustomer;
  private Item testItem;
  private OrderDto orderDto;

  @BeforeEach
  void setUp() {
    // Crear un cliente de prueba
    testCustomer = new Customer();
    testCustomer.setName("John Doe");
    testCustomer.setEmail("john@example.com");
    testCustomer = customerRepository.save(testCustomer);

    // Crear un item de prueba
    testItem = new Item();
    testItem.setPrice(100.0f);
    testItem = itemRepository.save(testItem);

    // Crear OrderDto base para los tests
    orderDto = new OrderDto();
    orderDto.setCustomerId(testCustomer.getId());
    orderDto.setOrderPrice(100.0f);
    orderDto.setOrderDate(LocalDate.now());
    orderDto.setItemIds(List.of(testItem.getId()));
    orderDto.setDelivered(false);
  }

  @Test
  void addOrder_Returns201_WhenValid() throws Exception {
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerId").value(orderDto.getCustomerId()))
        .andExpect(jsonPath("$.orderPrice").value(100.0))
        .andExpect(jsonPath("$.orderDate").value(LocalDate.now().toString()));
  }

  @Test
  void addOrder_Returns400_WhenInvalid() throws Exception {
    OrderDto invalidOrder = new OrderDto();
    invalidOrder.setItemIds(List.of());

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidOrder)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(500));
  }


  @Test
  void getAllOrders_Returns200() throws Exception {
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].customerId").exists())
        .andExpect(jsonPath("$[0].orderPrice").exists());
  }


  @Test
  void modifyOrder_Returns404_WhenNotFound() throws Exception {
    OrderDto updatedOrderDto = new OrderDto();
    updatedOrderDto.setCustomerId(999L);
    updatedOrderDto.setOrderPrice(100.0f);
    updatedOrderDto.setDelivered(false);
    updatedOrderDto.setItemIds(List.of());

    mockMvc.perform(put("/orders/999999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedOrderDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteOrder_Returns204_WhenExists() throws Exception {
    String createResponse = mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    OrderDto created = objectMapper.readValue(createResponse, OrderDto.class);

    mockMvc.perform(delete("/orders/" + created.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteOrder_Returns404_WhenNotFound() throws Exception {
    mockMvc.perform(delete("/orders/999999"))
        .andExpect(status().isNotFound());
  }
}
