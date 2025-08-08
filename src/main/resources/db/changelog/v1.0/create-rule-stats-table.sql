-- Таблица статистики срабатываний
CREATE TABLE IF NOT EXISTS rule_stats (
    rule_id UUID PRIMARY KEY REFERENCES recommendation_rules(id) ON DELETE CASCADE,
    count BIGINT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индекс для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_rule_stats_count ON rule_stats(count);