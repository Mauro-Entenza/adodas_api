package com.example.demo.controller;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.dto.CustomerV2Dto;
import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @Autowired
  private CustomerService customerService;


  @GetMapping("/v1/customers")
  public ResponseEntity<List<CustomerDto>> getCustomers(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "surname", required = false) String surname) {

    logger.info("BEGIN getCustomers with filters: name={}, email={}, surname={}", name, email,
        surname);
    List<CustomerDto> customers = customerService.findByFilters(name, email, surname);
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @GetMapping("/v2/customers")
  //V2 CHANGE - AHORA ESTÁN PAGINADOS LOS RESULTADOS
  public ResponseEntity<List<CustomerDto>> getCustomersV2(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {

    List<CustomerDto> customers = customerService.findAll()
        .stream()
        .skip((long) page * size)
        .limit(size)
        .toList();

    return ResponseEntity.ok(customers);
  }

  @PostMapping("/v1/customers")
  public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
    logger.info("BEGIN addCustomer");
    CustomerDto newCustomer = customerService.addCustomer(customerDto);
    return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
  }

  @PostMapping("/v2/customers")
  // V2 CHANGE: nuevo contrato de cliente
  // - Eliminado: dni (no requerido en v2)
  // - Añadido: phone (campo nuevo, obligatorio en v2)
  // - Simplificado el modelo de cliente
  public ResponseEntity<CustomerV2Dto> addCustomerV2(
      @Valid @RequestBody CustomerV2Dto dto) {

    logger.info("BEGIN addCustomer V2");

    // Convertir a DTO v1
    CustomerDto oldDto = new CustomerDto();
    oldDto.setName(dto.getName());
    oldDto.setEmail(dto.getEmail());

    CustomerDto saved = customerService.addCustomer(oldDto);

    // Construir respuesta v2
    CustomerV2Dto response = new CustomerV2Dto(
        saved.getId(),
        saved.getName(),
        saved.getEmail(),
        dto.getPhone()
    );

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/v1/customers/{customerId}")
  public ResponseEntity<CustomerDto> modifyCustomer(
      @PathVariable long customerId,
      @Valid @RequestBody CustomerDto customerDto)
      throws CustomerNotFoundException {

    customerDto.setId(customerId);
    logger.info("BEGIN modifyCustomer");
    CustomerDto modifiedCustomer = customerService.modify(customerId, customerDto);
    return new ResponseEntity<>(modifiedCustomer, HttpStatus.OK);
  }

  @PutMapping("/v2/customers/{id}")
  /*
   * V2 CHANGE:
   * En esta versión del PUT se modifica el comportamiento tradicional de "reemplazo completo"
   * de un recurso.
   *
   * Ahora solo se permiten actualizar campos concretos (email y surname),
   * ignorando cualquier otro campo enviado en la petición.
   *
   * Esto mejora la seguridad de la API evitando modificaciones accidentales
   * de campos sensibles como id, dni o name.
   */
  public ResponseEntity<CustomerDto> updateCustomerV2(
      @PathVariable long id,
      @RequestBody Map<String, Object> updates)
      throws CustomerNotFoundException {

    logger.info("BEGIN PUT v2 - controlled update for customer {}", id);

    // Eliminamos cualquier campo que NO esté permitido actualizar en v2
    updates.keySet().removeIf(key ->
        !key.equals("email") && !key.equals("surname"));

    // Reutilizamos la lógica de actualización parcial del servicio
    CustomerDto updated = customerService.patchCustomer(id, updates);

    return ResponseEntity.ok(updated);
  }

  @PatchMapping("/v1/customers/{customerId}")
  public ResponseEntity<CustomerDto> patchCustomer(
      @PathVariable long customerId,
      @RequestBody Map<String, Object> updates)
      throws CustomerNotFoundException {

    logger.info("BEGIN patchCustomer - id: {}, updates: {}", customerId, updates);
    CustomerDto updatedCustomer = customerService.patchCustomer(customerId, updates);
    return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
  }

  @DeleteMapping("/v1/customers/{customerId}")
  public ResponseEntity<Void> removeCustomer(@PathVariable long customerId)
      throws CustomerNotFoundException {

    customerService.deleteCustomer(customerId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/v2/customers/{id}")
  public ResponseEntity<String> deleteCustomerV2(@PathVariable long id)
      throws CustomerNotFoundException {

    logger.info("BEGIN DELETE v2 - soft delete simulation for customer {}", id);

    /*
     * V2 CHANGE:
     * En la versión v1, el endpoint DELETE eliminaba físicamente el registro de la base de datos.
     *
     * En la versión v2 se cambia el comportamiento para simular un "soft delete".
     * Es decir, en lugar de eliminar el cliente de la base de datos, se devuelve una respuesta
     * indicando que el cliente ha sido marcado como eliminado.
     *
     * Esto representa una mejora habitual en APIs reales, donde los datos no se borran físicamente
     * para evitar pérdida de información histórica o problemas de integridad referencial.
     *
     * (En un sistema real, aquí se actualizaría un campo como "deleted = true")
     */

    // En v2 no eliminamos realmente el registro (soft delete simulado)
    // customerService.deleteCustomer(id); // <- NO se ejecuta en v2

    return ResponseEntity.ok(
        "Customer with id " + id + " marked as deleted (soft delete in v2)"
    );
  }

  // =======================
  // EXCEPTION HANDLERS
  // =======================

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleCustomerNotFoundException(
      CustomerNotFoundException exception) {

    ErrorResponseDto error = ErrorResponseDto.generalError(404, exception.getMessage());
    logger.error(exception.getMessage(), exception);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationErrors(
      MethodArgumentNotValidException exception) {

    List<String> errors = exception.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .toList();

    ErrorResponseDto error = new ErrorResponseDto(400, "Validation failed", errors);
    logger.error("Validation errors: {}", errors);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception exception) {

    ErrorResponseDto error = new ErrorResponseDto(
        500,
        "An unexpected error occurred",
        List.of(exception.getMessage())
    );

    logger.error("Unexpected error: {}", exception.getMessage(), exception);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}