package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.dto.CustomerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
class CustomerControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private CustomerDto validCustomer;

  @BeforeEach
  void setUp() {
    validCustomer = new CustomerDto();
    validCustomer.setName("John");
    validCustomer.setDni("12345678A");
    validCustomer.setSurname("Doe");
    validCustomer.setEmail("john@example.com");
    validCustomer.setEmailConfirmed(true);
    validCustomer.setRegisterDate(LocalDate.now());
  }

  @Test
  void getAllCustomers_Returns200() throws Exception {
    mockMvc.perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void addCustomer_Returns201() throws Exception {
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.dni").value("12345678A"));
  }

  @Test
  void addCustomer_Returns400_WhenMissingRequiredFields() throws Exception {
    CustomerDto invalid = new CustomerDto();
    invalid.setName(null);

    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void updateCustomer_Returns200_WhenExists() throws Exception {
    // 1. Crear customer
    String createResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    // 2. Extraer ID real
    CustomerDto created = objectMapper.readValue(createResponse, CustomerDto.class);
    created.setName("John Updated");

    // 3. Actualizar
    mockMvc.perform(put("/customers/" + created.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(created)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Updated"));
  }

  @Test
  void updateCustomer_Returns404_WhenNotFound() throws Exception {
    CustomerDto validDto = new CustomerDto();
    validDto.setName("Valid");
    validDto.setDni("99999999Z");
    validDto.setSurname("Test");
    validDto.setEmail("test@example.com");
    validDto.setEmailConfirmed(false);
    validDto.setRegisterDate(LocalDate.now());

    mockMvc.perform(put("/customers/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validDto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404));
  }

  @Test
  void deleteCustomer_Returns204_WhenExists() throws Exception {
    String createResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    CustomerDto created = objectMapper.readValue(createResponse, CustomerDto.class);

    mockMvc.perform(delete("/customers/" + created.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCustomer_Returns404_WhenNotFound() throws Exception {
    mockMvc.perform(delete("/customers/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404));
  }

  @Test
  void getCustomers_WithNameFilter_ReturnsFiltered() throws Exception {
    // Crear John
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated());

    // Filtrar SOLO John (ignora los 3 preexistentes)
    mockMvc.perform(get("/customers")
            .param("name", "John"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("John"));  // ✅ Primer resultado = John
  }

  @Test
  void getCustomers_WithEmailFilter_ReturnsFiltered() throws Exception {
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/customers")
            .param("email", "john@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void getCustomers_WithMultipleFilters_ReturnsIntersection() throws Exception {
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/customers")
            .param("name", "John")
            .param("surname", "Doe"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("John"));
  }

  @Test
  void getCustomers_WithoutFilters_ReturnsAll() throws Exception {
    // NO esperamos cantidad exacta (hay datos preexistentes)
    mockMvc.perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());  //
  }

}
