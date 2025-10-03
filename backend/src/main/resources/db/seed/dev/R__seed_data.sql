-- Barajas de ejemplo
INSERT INTO decks (id, name, description)
VALUES
  ('00000000-0000-0000-0000-000000000001', 'Redes', 'Conceptos de capa 2/3/4'),
  ('00000000-0000-0000-0000-000000000002', 'Ciberseguridad', 'Fundamentos y ataques básicos')
ON CONFLICT DO NOTHING;

-- Tarjetas de ejemplo
INSERT INTO cards (id, deck_id, front, back, tags, latex)
VALUES
  ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '¿Qué es ARP?', 'Resuelve IP->MAC en LAN', 'redes,arp', false),
  ('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '¿Diferencia TCP/UDP?', 'TCP fiable, UDP no', 'redes,tcp,udp', false),
  ('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', 'CIA triad', 'Confidencialidad, Integridad, Disponibilidad', 'seguridad,cia', false)
ON CONFLICT DO NOTHING;

-- Usuario demo (solo si tienes V3 con app_user)
-- email: demo@local  / password: Demo12345!
INSERT INTO app_user (id, email, password_hash, role)
VALUES (
  '20000000-0000-0000-0000-000000000001',
  'demo@local',
  '$2a$10$3f1H3bI7A5y0GmYt3mJ5Y.u2eEw3sg1U5CiIk9pUqM0xXJw3d2B6y', -- BCrypt de "Demo12345!"
  'USER'
)
ON CONFLICT DO NOTHING;
