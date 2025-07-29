-- =============================================
-- Набор изменений №1: Создание основных таблиц
-- =============================================
-- Создание таблиц для системы динамических правил рекомендаций

-- Таблица для хранения основных правил рекомендаций
CREATE TABLE recommendation_rules (
    id UUID PRIMARY KEY,                          -- Уникальный идентификатор правила
    product_name VARCHAR(255) NOT NULL,           -- Название рекомендуемого продукта
    product_id UUID NOT NULL,                     -- ID продукта в основной системе
    product_text TEXT,                            -- Текст рекомендации
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата обновления записи
);

-- Таблица для хранения условий правил
CREATE TABLE rule_conditions (
    id SERIAL PRIMARY KEY,                        -- Автоинкрементный ID условия
    rule_id UUID NOT NULL,                        -- Ссылка на правило
    query VARCHAR(50) NOT NULL,                   -- Тип запроса (USER_OF, ACTIVE_USER_OF и т.д.)
    negate BOOLEAN NOT NULL DEFAULT false,        -- Флаг отрицания условия
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания условия
    FOREIGN KEY (rule_id) REFERENCES recommendation_rules(id) ON DELETE CASCADE -- Каскадное удаление
);

-- Таблица для хранения аргументов условий
CREATE TABLE rule_arguments (
    condition_id INTEGER NOT NULL,                -- Ссылка на условие
    argument_order INTEGER NOT NULL,              -- Порядковый номер аргумента (для сохранения порядка)
    argument_value VARCHAR(100) NOT NULL,         -- Значение аргумента
    PRIMARY KEY (condition_id, argument_order),   -- Составной первичный ключ
    FOREIGN KEY (condition_id) REFERENCES rule_conditions(id) ON DELETE CASCADE -- Каскадное удаление
);

