USE adodas;

INSERT INTO Customers (
  id,
  name,
  dni,
  surname,
  email,
  is_email_confirmed,
  register_date,
  order_id
) VALUES (
  1,
  'Cliente de prueba',
  '12345678A',
  'Test',
  'cliente@test.com',
  1,
  '2026-05-01',
  NULL
);