package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.dto.RefundDto;
import com.example.demo.domain.entity.Customer;
import com.example.demo.domain.entity.Order;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RefundRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class RefundControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RefundRepository refundRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  private Customer testCustomer;
  private Order testOrder;
  private RefundDto testRefund;
  private RefundDto testApprovedRefund;
  private RefundDto testHighAmountRefund;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    refundRepository.deleteAll();
    orderRepository.deleteAll();
    customerRepository.deleteAll();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.findAndRegisterModules();

    testCustomer = new Customer();
    testCustomer.setName("Test Customer");
    testCustomer = customerRepository.save(testCustomer);

    testOrder = new Order();
    testOrder.setCustomer(testCustomer);
    testOrder.setOrderDate(LocalDate.now());
    testOrder.setOrderPrice(200.0f);
    testOrder.setCustomerNotes("Test order notes");
    testOrder.setDelivered(false);
    testOrder = orderRepository.save(testOrder);

    testRefund = new RefundDto();
    testRefund.setReason("Product defect");
    testRefund.setRefundDate(LocalDate.now());
    testRefund.setAmount(50.0f);
    testRefund.setOrderId(testOrder.getId());
    testRefund.setCustomerId(testCustomer.getId());
    testRefund.setApproved(false);

    testApprovedRefund = new RefundDto();
    testApprovedRefund.setReason("Wrong item");
    testApprovedRefund.setRefundDate(LocalDate.now());
    testApprovedRefund.setAmount(150.0f);
    testApprovedRefund.setOrderId(testOrder.getId());
    testApprovedRefund.setCustomerId(testCustomer.getId());
    testApprovedRefund.setApproved(true);

    testHighAmountRefund = new RefundDto();
    testHighAmountRefund.setReason("Customer regret");
    testHighAmountRefund.setRefundDate(LocalDate.now());
    testHighAmountRefund.setAmount(150.0f);
    testHighAmountRefund.setOrderId(testOrder.getId());
    testHighAmountRefund.setCustomerId(testCustomer.getId());
    testHighAmountRefund.setApproved(false);
  }

  @Test
  void addRefund_Success() throws Exception {
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.reason").value("Product defect"))
        .andExpect(jsonPath("$.amount").value(50.0))
        .andExpect(jsonPath("$.approved").value(false));
  }

  @Test
  void getAllRefunds_Success() throws Exception {
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].reason").value("Product defect"));
  }

  @Test
  void modifyRefund_Success() throws Exception {
    String createResponse = mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    long refundId = objectMapper.readTree(createResponse).get("id").asLong();

    RefundDto modifiedRefund = new RefundDto();
    modifiedRefund.setReason("Updated reason");
    modifiedRefund.setRefundDate(LocalDate.now());
    modifiedRefund.setAmount(75.0f);
    modifiedRefund.setOrderId(testOrder.getId());
    modifiedRefund.setCustomerId(testCustomer.getId());
    modifiedRefund.setApproved(true);

    mockMvc.perform(put("/refunds/{refundId}", refundId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(modifiedRefund)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reason").value("Updated reason"));
  }

  @Test
  void deleteRefund_Success() throws Exception {
    String createResponse = mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    long refundId = objectMapper.readTree(createResponse).get("id").asLong();

    mockMvc.perform(delete("/refunds/{refundId}", refundId))
        .andExpect(status().isNoContent());
  }

  @Test
  void addRefund_Returns400_WhenInvalid() throws Exception {
    RefundDto invalidRefund = new RefundDto();
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRefund)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteRefund_Returns404_WhenNotFound() throws Exception {
    mockMvc.perform(delete("/refunds/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void filterByApproved() throws Exception {
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testApprovedRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds?isApproved=true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].approved").value(true));
  }

  @Test
  void filterByMinAmount() throws Exception {
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testHighAmountRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds?minAmount=100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].amount").value(150.0));
  }

  @Test
  void filterByReason() throws Exception {

    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testApprovedRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds?reason=Product defect"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void filterCombined() throws Exception {

    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testApprovedRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testApprovedRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds?isApproved=true&minAmount=100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].amount").value(150.0));
  }

  @Test
  void filterNoParams_ReturnsAll() throws Exception {
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testApprovedRefund)))
        .andExpect(status().isCreated());
    mockMvc.perform(post("/refunds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testHighAmountRefund)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/refunds"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3));
  }
}
