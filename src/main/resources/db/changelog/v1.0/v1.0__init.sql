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

-- =============================================
-- ИНИЦИАЛИЗАЦИЯ ОСНОВНЫХ ТАБЛИЦ БАЗЫ ДАННЫХ
-- Версия: 1.0
-- Цель: Создание базовой структуры для системы рекомендаций
-- =============================================

-- *********************************************
-- Таблица: Рекомендационные правила
-- Назначение: Хранение основных метаданных правил рекомендаций
-- Примеры данных: "Кредит под 5%", "Инвестпортфель" и т.д.
-- *********************************************
CREATE TABLE IF NOT EXISTS recommendation_rules (
    id UUID PRIMARY KEY,                  -- Уникальный идентификатор правила
    product_id UUID NOT NULL,             -- Ссылка на рекомендуемый продукт
    product_name VARCHAR(255) NOT NULL,   -- Название продукта (человекочитаемое)
    product_text TEXT,                    -- Полное описание рекомендации
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата создания записи
);

COMMENT ON TABLE recommendation_rules IS 'Основные правила для генерации рекомендаций';
COMMENT ON COLUMN recommendation_rules.product_id IS 'Внешний ключ к продукту';
COMMENT ON COLUMN recommendation_rules.product_text IS 'Текст рекомендации для пользователя';

-- *********************************************
-- Таблица: Условия правил
-- Назначение: Хранение условий для срабатывания правил
-- Пример: "Если есть дебетовая карта" и "Сумма пополнений > 100000"
-- *********************************************
CREATE TABLE IF NOT EXISTS rule_conditions (
    id BIGSERIAL PRIMARY KEY,             -- Автоинкрементный ID
    rule_id UUID NOT NULL REFERENCES recommendation_rules(id) ON DELETE CASCADE,
    query VARCHAR(50) NOT NULL,           -- Тип условия (например: "has_product")
    negate BOOLEAN NOT NULL DEFAULT FALSE, -- Флаг отрицания условия
    arguments JSONB                       -- Параметры условия в JSON формате
);

COMMENT ON TABLE rule_conditions IS 'Условия для активации правил рекомендаций';
COMMENT ON COLUMN rule_conditions.query IS 'Системное название условия для парсинга';
COMMENT ON COLUMN rule_conditions.arguments IS 'Доп. параметры условия в JSON';

-- *********************************************
-- Таблица: Продукты пользователей
-- Назначение: Хранение информации о продуктах клиентов
-- Пример: У пользователя ID=123 есть дебетовая карта
-- *********************************************
CREATE TABLE IF NOT EXISTS user_products (
    user_id UUID NOT NULL,                -- Идентификатор пользователя
    product_type VARCHAR(50) NOT NULL,    -- Тип продукта: 'DEBIT', 'CREDIT' и т.д.
    PRIMARY KEY (user_id, product_type)   -- Составной первичный ключ
);

COMMENT ON TABLE user_products IS 'Продукты, подключенные у пользователей';
COMMENT ON COLUMN user_products.product_type IS 'Тип продукта из фиксированного списка';

-- *********************************************
-- Таблица: Транзакции
-- Назначение: История операций по продуктам
-- Пример: Пополнение на 50000 руб по дебетовой карте
-- *********************************************
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,             -- Уникальный ID транзакции
    user_id UUID NOT NULL,                -- Ссылка на пользователя
    product_type VARCHAR(50) NOT NULL,    -- Тип продукта
    transaction_type VARCHAR(20) NOT NULL,-- 'DEPOSIT'/'WITHDRAWAL'
    amount DECIMAL(19,4) NOT NULL,        -- Сумма с точностью до 4 знаков
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_transaction_type CHECK (transaction_type IN ('INCOMING', 'OUTGOING'))
);

COMMENT ON TABLE transactions IS 'История транзакций пользователей';
COMMENT ON COLUMN transactions.amount IS 'Сумма в валюте продукта';

-- Создаем индексы для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_transactions_user ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_product ON transactions(product_type);