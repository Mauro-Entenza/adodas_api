package com.example.demo;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.controller.ServiceController;
import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.service.ServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ServiceController.class)
class ServiceControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ServiceService serviceService;

  @Autowired
  private ObjectMapper objectMapper;

  private ServiceDto getValidServiceDto() {
    return new ServiceDto(1L, "Limpieza", "Servicio de limpieza", 100.0f,
        null, null, true, null, null);
  }

  @Test
  void addService_ReturnsCreated() throws Exception {
    ServiceDto input = getValidServiceDto();
    when(serviceService.addService(any())).thenReturn(input);

    mockMvc.perform(post("/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Limpieza"));
  }

  @Test
  void addService_Returns400_WhenInvalid() throws Exception {
    ServiceDto invalid = new ServiceDto();

    mockMvc.perform(post("/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void getAll_ReturnsList() throws Exception {
    when(serviceService.findByFilters(null, null, null, null))
        .thenReturn(List.of(getValidServiceDto()));

    mockMvc.perform(get("/services"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getAll_WithFilters_ReturnsFiltered() throws Exception {
    when(serviceService.findByFilters("Limpieza", 50.0f, 150.0f, true))
        .thenReturn(List.of(getValidServiceDto()));

    mockMvc.perform(get("/services")
            .param("name", "Limpieza")
            .param("minPrice", "50")
            .param("maxPrice", "150")
            .param("isActive", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void modifyService_ReturnsOK() throws Exception {
    ServiceDto input = getValidServiceDto();
    when(serviceService.modify(eq(1L), any())).thenReturn(input);

    mockMvc.perform(put("/services/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Limpieza"));
  }

  @Test
  void modifyService_Returns400_WhenInvalid() throws Exception {
    ServiceDto invalid = new ServiceDto();

    mockMvc.perform(put("/services/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyService_Returns404_WhenNotFound() throws Exception {
    ServiceDto input = getValidServiceDto();
    doThrow(new ServiceNotFoundException("No encontrado")).when(serviceService)
        .modify(eq(1L), any());

    mockMvc.perform(put("/services/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(containsString("No encontrado")));
  }

  @Test
  void patchService_ReturnsOK() throws Exception {
    ServiceDto input = getValidServiceDto();
    when(serviceService.patch(eq(1L), any())).thenReturn(input);

    mockMvc.perform(patch("/services/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Limpieza Actualizada\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Limpieza"));
  }

  @Test
  void patchService_Returns404_WhenNotFound() throws Exception {
    doThrow(new ServiceNotFoundException("No encontrado")).when(serviceService)
        .patch(eq(1L), any());

    mockMvc.perform(patch("/services/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteService_Returns204() throws Exception {
    mockMvc.perform(delete("/services/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteService_Returns404_WhenNotFound() throws Exception {
    doThrow(new ServiceNotFoundException("No encontrado")).when(serviceService).deleteService(1L);

    mockMvc.perform(delete("/services/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(containsString("No encontrado")));
  }
}
