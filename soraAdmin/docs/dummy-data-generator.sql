-- ============================================================
-- Streak 배치 성능 테스트용 더미 데이터 생성 스크립트
-- 대상: sora-mysql-test (port 43120)
--
-- 데이터 규모:
--   user_info:     1,000,000건
--   streak_info:   1,000,000건
--   os_auth_info:  1,000,000건
--   review:        ~12,500,000건 (유저당 0~50건, 평균 25건)
--
-- 예상 소요 시간: 15~30분
-- ============================================================

SET @BATCH_SIZE = 5000;
SET @TOTAL_USERS = 1000000;

-- ============================================================
-- 0. 기존 데이터 초기화 + 성능 설정
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE review;
TRUNCATE TABLE os_auth_info;
TRUNCATE TABLE user_info;
TRUNCATE TABLE streak_info;

-- Spring Batch 메타 테이블도 초기화 (재실행 가능하도록)
TRUNCATE TABLE BATCH_STEP_EXECUTION_CONTEXT;
TRUNCATE TABLE BATCH_STEP_EXECUTION;
TRUNCATE TABLE BATCH_JOB_EXECUTION_CONTEXT;
TRUNCATE TABLE BATCH_JOB_EXECUTION_PARAMS;
TRUNCATE TABLE BATCH_JOB_EXECUTION;
TRUNCATE TABLE BATCH_JOB_INSTANCE;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '기존 데이터 초기화 완료' AS status;

-- ============================================================
-- 1. 성능을 위한 설정
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET autocommit = 0;
SET @OLD_SQL_MODE = @@SQL_MODE;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- ============================================================
-- 1. streak_info 100만건
--    review_at: 70% 18:00~22:00, 30% 나머지
-- ============================================================
DROP PROCEDURE IF EXISTS insert_streak_info;

DELIMITER //
CREATE PROCEDURE insert_streak_info()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE batch_start INT;
    DECLARE j INT;
    DECLARE rand_val DOUBLE;
    DECLARE hour_val INT;
    DECLARE minute_val INT;
    DECLARE streak_count INT;
    DECLARE aspirations JSON;
    DECLARE locations JSON;

    SET aspirations = JSON_ARRAY('개발자', '디자이너', '기획자', '작가', '운동선수', '음악가', '요리사', '사업가');
    SET locations = JSON_ARRAY('카페', '집', '도서관', '사무실', '공원', '학교', '헬스장', '독서실');

    WHILE i < @TOTAL_USERS DO
        SET batch_start = i;
        SET j = 0;

        START TRANSACTION;

        WHILE j < @BATCH_SIZE AND (i + j) < @TOTAL_USERS DO
            SET rand_val = RAND();

            -- 70% 저녁(18~22시), 30% 나머지
            IF rand_val < 0.7 THEN
                SET hour_val = 18 + FLOOR(RAND() * 4);   -- 18,19,20,21
            ELSE
                SET minute_val = FLOOR(RAND() * 24);
                -- 18~21시 제외
                IF minute_val >= 18 AND minute_val <= 21 THEN
                    SET minute_val = (minute_val + 4) MOD 24;
                END IF;
                SET hour_val = minute_val;
            END IF;

            SET minute_val = FLOOR(RAND() * 4) * 15;     -- 0, 15, 30, 45
            SET streak_count = FLOOR(RAND() * 31);        -- 0~30

            INSERT INTO streak_info (
                review_at,
                write_location,
                aspiration,
                is_receive_notification,
                last_write_date,
                review_streak_count
            ) VALUES (
                MAKETIME(hour_val, minute_val, 0),
                JSON_UNQUOTE(JSON_EXTRACT(locations, CONCAT('$[', FLOOR(RAND() * 8), ']'))),
                JSON_UNQUOTE(JSON_EXTRACT(aspirations, CONCAT('$[', FLOOR(RAND() * 8), ']'))),
                TRUE,
                DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 7) DAY),
                streak_count
            );

            SET j = j + 1;
        END WHILE;

        COMMIT;
        SET i = i + @BATCH_SIZE;

        -- 진행률 출력 (10만건마다)
        IF i MOD 100000 = 0 THEN
            SELECT CONCAT('streak_info: ', i, ' / ', @TOTAL_USERS, ' (', ROUND(i / @TOTAL_USERS * 100), '%)') AS progress;
        END IF;
    END WHILE;
END //
DELIMITER ;

CALL insert_streak_info();
DROP PROCEDURE IF EXISTS insert_streak_info;

-- ============================================================
-- 2. user_info 100만건
--    streak_info_id = 1:1 매핑
-- ============================================================
DROP PROCEDURE IF EXISTS insert_user_info;

DELIMITER //
CREATE PROCEDURE insert_user_info()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT;

    WHILE i < @TOTAL_USERS DO
        SET j = 0;

        START TRANSACTION;

        WHILE j < @BATCH_SIZE AND (i + j) < @TOTAL_USERS DO
            INSERT INTO user_info (
                uuid,
                social_id,
                streak_info_id,
                initial_review_count,
                username,
                role,
                refresh_token,
                created_at,
                updated_at
            ) VALUES (
                UUID(),
                CONCAT('social_', i + j),
                i + j + 1,  -- streak_info PK (1부터 시작)
                0,
                CONCAT('user_', i + j),
                'ROLE_USER',
                NULL,
                NOW(),
                NOW()
            );

            SET j = j + 1;
        END WHILE;

        COMMIT;
        SET i = i + @BATCH_SIZE;

        IF i MOD 100000 = 0 THEN
            SELECT CONCAT('user_info: ', i, ' / ', @TOTAL_USERS, ' (', ROUND(i / @TOTAL_USERS * 100), '%)') AS progress;
        END IF;
    END WHILE;
END //
DELIMITER ;

CALL insert_user_info();
DROP PROCEDURE IF EXISTS insert_user_info;

-- ============================================================
-- 3. os_auth_info 100만건
--    유저당 1개, OS 타입 랜덤
-- ============================================================
DROP PROCEDURE IF EXISTS insert_os_auth_info;

DELIMITER //
CREATE PROCEDURE insert_os_auth_info()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT;
    DECLARE os_types JSON;
    DECLARE os_idx INT;

    SET os_types = JSON_ARRAY('IOS', 'ANDROID', 'WINDOW');

    WHILE i < @TOTAL_USERS DO
        SET j = 0;

        START TRANSACTION;

        WHILE j < @BATCH_SIZE AND (i + j) < @TOTAL_USERS DO
            SET os_idx = FLOOR(RAND() * 3);

            INSERT INTO os_auth_info (
                user_info_id,
                os_id,
                os_type,
                fcm_token,
                created_at,
                updated_at
            ) VALUES (
                i + j + 1,  -- user_info PK (1부터 시작)
                CONCAT('os_', UUID()),
                JSON_UNQUOTE(JSON_EXTRACT(os_types, CONCAT('$[', os_idx, ']'))),
                CONCAT('fcm_token_', i + j),
                NOW(),
                NOW()
            );

            SET j = j + 1;
        END WHILE;

        COMMIT;
        SET i = i + @BATCH_SIZE;

        IF i MOD 100000 = 0 THEN
            SELECT CONCAT('os_auth_info: ', i, ' / ', @TOTAL_USERS, ' (', ROUND(i / @TOTAL_USERS * 100), '%)') AS progress;
        END IF;
    END WHILE;
END //
DELIMITER ;

CALL insert_os_auth_info();
DROP PROCEDURE IF EXISTS insert_os_auth_info;

-- ============================================================
-- 4. review ~1250만건
--    유저당 0~50건, reviewDate: 2025-01-01 ~ 2026-03-29
-- ============================================================
DROP PROCEDURE IF EXISTS insert_reviews;

DELIMITER //
CREATE PROCEDURE insert_reviews()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT;
    DECLARE review_count INT;
    DECLARE k INT;
    DECLARE total_days INT;
    DECLARE rand_date DATE;
    DECLARE feedback_types JSON;
    DECLARE inserted_count INT DEFAULT 0;

    SET total_days = DATEDIFF('2026-03-29', '2025-01-01');
    SET feedback_types = JSON_ARRAY('FEELING', 'THINKING');

    WHILE i < @TOTAL_USERS DO
        SET j = 0;

        START TRANSACTION;

        WHILE j < @BATCH_SIZE AND (i + j) < @TOTAL_USERS DO
            -- 유저당 0~50건 랜덤
            SET review_count = FLOOR(RAND() * 51);
            SET k = 0;

            WHILE k < review_count DO
                SET rand_date = DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * total_days) DAY);

                INSERT INTO review (
                    user_info_id,
                    feedback_type,
                    body,
                    feedback,
                    review_date,
                    created_at,
                    updated_at
                ) VALUES (
                    i + j + 1,
                    JSON_UNQUOTE(JSON_EXTRACT(feedback_types, CONCAT('$[', FLOOR(RAND() * 2), ']'))),
                    CONCAT('오늘 하루 회고입니다. 유저 ', i + j, ' 리뷰 ', k),
                    CONCAT('AI 피드백입니다. 잘하고 있어요! 리뷰 ', k),
                    rand_date,
                    TIMESTAMP(rand_date, MAKETIME(FLOOR(RAND() * 24), FLOOR(RAND() * 60), 0)),
                    TIMESTAMP(rand_date, MAKETIME(FLOOR(RAND() * 24), FLOOR(RAND() * 60), 0))
                );

                SET k = k + 1;
                SET inserted_count = inserted_count + 1;
            END WHILE;

            SET j = j + 1;
        END WHILE;

        COMMIT;
        SET i = i + @BATCH_SIZE;

        IF i MOD 100000 = 0 THEN
            SELECT CONCAT('review: 유저 ', i, ' / ', @TOTAL_USERS, ' 처리 완료 (리뷰 총 ', inserted_count, '건)') AS progress;
        END IF;
    END WHILE;

    SELECT CONCAT('review 완료: 총 ', inserted_count, '건') AS result;
END //
DELIMITER ;

CALL insert_reviews();
DROP PROCEDURE IF EXISTS insert_reviews;

-- ============================================================
-- 5. 설정 복원
-- ============================================================
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET autocommit = 1;
SET SQL_MODE = @OLD_SQL_MODE;

-- ============================================================
-- 6. 데이터 검증
-- ============================================================
SELECT 'user_info' AS tbl, COUNT(*) AS cnt FROM user_info
UNION ALL
SELECT 'streak_info', COUNT(*) FROM streak_info
UNION ALL
SELECT 'os_auth_info', COUNT(*) FROM os_auth_info
UNION ALL
SELECT 'review', COUNT(*) FROM review;

-- review_at 시간대 분포 확인
SELECT
    CASE
        WHEN HOUR(review_at) BETWEEN 18 AND 21 THEN '저녁(18~22)'
        ELSE '기타'
    END AS time_slot,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) / (SELECT COUNT(*) FROM streak_info) * 100, 1) AS pct
FROM streak_info
GROUP BY time_slot;

-- 유저당 리뷰 수 분포 확인
SELECT
    CASE
        WHEN cnt = 0 THEN '0건'
        WHEN cnt BETWEEN 1 AND 10 THEN '1~10건'
        WHEN cnt BETWEEN 11 AND 30 THEN '11~30건'
        ELSE '31~50건'
    END AS review_range,
    COUNT(*) AS user_count
FROM (
    SELECT u.user_info_id, COALESCE(r.cnt, 0) AS cnt
    FROM user_info u
    LEFT JOIN (SELECT user_info_id, COUNT(*) AS cnt FROM review GROUP BY user_info_id) r
    ON u.user_info_id = r.user_info_id
) t
GROUP BY review_range;
