-- Adicionar coluna role_id na tabela users
ALTER TABLE users ADD COLUMN role_id UUID;

-- Migrar dados existentes (pegar o primeiro role de cada usuário)
UPDATE users u
SET role_id = (
    SELECT ur.role_id 
    FROM user_roles ur 
    WHERE ur.user_id = u.id 
    LIMIT 1
);

-- Tornar a coluna obrigatória
ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

-- Adicionar foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_role 
FOREIGN KEY (role_id) REFERENCES roles(id);

-- Remover a tabela de relacionamento many-to-many
DROP TABLE user_roles;
