-- 1) Написать функцию, возвращающую таблицу TransferredPoints в более человекочитаемом виде
-- Ник пира 1, ник пира 2, количество переданных пир поинтов. 
-- Количество отрицательное, если пир 2 получил от пира 1 больше поинтов.
DROP FUNCTION IF EXISTS TransferredPoints;
CREATE OR REPLACE FUNCTION TransferredPoints () RETURNS TABLE (peer1 varchar, peer2 varchar, PointsAmount int) AS $$ BEGIN RETURN QUERY EXECUTE 'SELECT 
        Peer1, 
        Peer2, 
        COALESCE(t1.PointsAmount, 0) - COALESCE(t2.PointsAmount, 0) AS PointsAmount
    FROM
    (SELECT DISTINCT
        CASE WHEN CheckingPeer > CheckedPeer THEN CheckingPeer ELSE CheckedPeer END AS Peer1,
        CASE WHEN CheckingPeer <= CheckedPeer THEN CheckingPeer ELSE CheckedPeer END AS Peer2
     FROM TransferredPoints)p 
    LEFT JOIN TransferredPoints t1 ON p.Peer1 = t1.CheckingPeer AND p.Peer2 = t1.CheckedPeer
    LEFT JOIN TransferredPoints t2 ON p.Peer1 = t2.CheckedPeer AND p.Peer2 = t2.CheckingPeer
    ORDER BY Peer1, Peer2 ';
END;
$$ LANGUAGE plpgsql;
--
--
-- 2) Написать функцию, которая возвращает таблицу вида: ник пользователя, название проверенного задания, 
-- кол-во полученного XP
-- В таблицу включать только задания, успешно прошедшие проверку (определять по таблице Checks).
-- Одна задача может быть успешно выполнена несколько раз. В таком случае в таблицу включать все успешные проверки.
DROP FUNCTION IF EXISTS CheckedPeersWithXP;
CREATE OR REPLACE FUNCTION CheckedPeersWithXP() RETURNS TABLE(
        Peer varchar(255),
        Task varchar(255),
        XP int
    ) AS $$
select c.peer,
    c.task,
    xp.xpamount
from p2p p
    left join checks c on p.check_ = c.id
    left join verter v on v.check_ = c.id
    left join xp on c.id = xp.check_
    left join tasks t on t.title = c.task
where p.state = 'Success'
    AND (
        v.state = 'Success'
        OR v.state IS NULL
    )
    AND xp.xpamount * 100 / t.maxxp >= 80;
$$ LANGUAGE SQL;
--
-- 3) Написать функцию, определяющую пиров, которые не выходили из кампуса в течение всего дня
-- Параметры функции: день, например 12.05.2022.
-- Функция возвращает только список пиров.
DROP FUNCTION IF EXISTS PeersInCampus;
CREATE OR REPLACE FUNCTION PeersInCampus(date_ date) RETURNS TABLE(Peer varchar(30)) AS $$ WITH count_ AS (
        SELECT t1.peer,
            t1.date,
            t1.state,
            COUNT(t1.peer) AS cv
        FROM timetracking t1
        GROUP BY t1.date,
            t1.peer,
            t1.state
    )
SELECT DISTINCT t3.peer
FROM (
        SELECT t.peer,
            t.date
        FROM timetracking t
        WHERE (
                SELECT cv
                FROM count_ c
                WHERE t.peer = c.peer
                    AND t.date = c.date
                    AND c.state = 1
            ) = 1
            AND (
                (
                    SELECT cv
                    FROM count_ c
                    WHERE t.peer = c.peer
                        AND t.date = c.date
                        AND c.state = 2
                ) = 1
                OR NOT EXISTS (
                    SELECT cv
                    FROM count_ c
                    WHERE t.peer = c.peer
                        AND t.date = c.date
                        AND c.state = 2
                )
            )
    ) as t3
WHERE date = date_;
$$ LANGUAGE SQL;
-- 4) Посчитать изменение в количестве пир поинтов каждого пира по таблице TransferredPoints
-- Результат вывести отсортированным по изменению числа поинтов. 
-- Формат вывода: ник пира, изменение в количество пир поинтов
DROP FUNCTION IF EXISTS PointsChange;
CREATE OR REPLACE FUNCTION PointsChange() RETURNS TABLE(Peer varchar(30), PointsChange bigint) AS $$ 
SELECT Peer,
    SUM(PointsChange) AS PointsChange
FROM (
        SELECT CheckingPeer AS Peer,
            PointsAmount AS PointsChange
        FROM TransferredPoints
        UNION
        SELECT CheckedPeer,
            - PointsAmount
        FROM TransferredPoints
    ) chages
GROUP BY Peer
ORDER BY PointsChange;
$$ LANGUAGE SQL;
--
-- 5) Посчитать изменение в количестве пир поинтов каждого пира по таблице, возвращаемой первой функцией из Part 3
-- Результат вывести отсортированным по изменению числа поинтов. 
-- Формат вывода: ник пира, изменение в количество пир поинтов
DROP FUNCTION IF EXISTS PointsChange_v2;
CREATE OR REPLACE FUNCTION PointsChange_v2() RETURNS TABLE(Peer varchar(30), PointsChange bigint) AS $$ 
SELECT Peer,
    SUM(pointsamount) AS PointsChange
FROM (
        SELECT peer1 AS peer,
            pointsamount
        FROM transferredpoints()
        UNION
        SELECT peer2,
            - pointsamount
        FROM transferredpoints()
    ) tp
GROUP BY Peer
ORDER BY PointsChange;
$$ LANGUAGE SQL;
-- 
-- 6) Определить самое часто проверяемое задание за каждый день
-- При одинаковом количестве проверок каких-то заданий в определенный день, вывести их все. 
-- Формат вывода: день, название задания
DROP FUNCTION IF EXISTS MostFrequentTaskDaily;
CREATE OR REPLACE FUNCTION MostFrequentTaskDaily() RETURNS TABLE(day date, task varchar(50)) AS $$ 
WITH A AS (
    SELECT date,
        checks.task,
        COUNT(id) AS countId
    FROM checks
    GROUP BY checks.task,
        date
)
SELECT date AS day,
    B.task
FROM (
        SELECT A.task,
            A.date,
            rank() OVER (
                PARTITION BY A.date
                ORDER BY countId DESC
            ) AS rank
        FROM A
    ) AS B
WHERE rank = 1
ORDER BY day
$$ LANGUAGE SQL;

-- 7) Найти всех пиров, выполнивших весь заданный блок задач и дату завершения последнего задания
-- Параметры процедуры: название блока, например "CPP". 
-- Результат вывести отсортированным по дате завершения. 
-- Формат вывода: ник пира, дата завершения блока (т.е. последнего выполненного задания из этого блока)
DROP FUNCTION IF EXISTS CompleteBlock;
CREATE OR REPLACE FUNCTION CompleteBlock(block varchar) RETURNS TABLE(peer varchar(30), day date) AS $$ 
WITH count_ as ((
    SELECT COUNT(tasks.title)
    FROM tasks
    WHERE tasks.title ~ CONCAT('^', block)
)), A AS (
    SELECT p2p.check_,
        p2p.state
    FROM p2p
    WHERE p2p.state = 'Success'
),
B AS (
    SELECT verter.check_,
        verter.state
    FROM verter
    WHERE verter.state = 'Success'
),
C AS (
    SELECT COUNT(id) AS count_success,
        MAX(date) AS day,
        peer
    FROM checks
    WHERE task ~ CONCAT('^', block)
        AND checks.id IN (
            SELECT check_
            FROM A
        )
        AND checks.id IN (
            SELECT check_
            FROM A
        )
    GROUP BY peer
)
SELECT peer,
    day
FROM C
WHERE count_success = (select * from count_)
$$ LANGUAGE SQL;
--
-- 8) Определить, к какому пиру стоит идти на проверку каждому обучающемуся
-- Определять нужно исходя из рекомендаций друзей пира, т.е. нужно найти пира, проверяться у которого рекомендует наибольшее число друзей. 
-- Формат вывода: ник пира, ник найденного проверяющего
DROP FUNCTION IF EXISTS FindPeerForCheck;
CREATE OR REPLACE FUNCTION FindPeerForCheck() RETURNS TABLE(peer varchar(50), RecommendedPeer varchar(50)) AS $$ 
WITH recomend_count AS (SELECT f.peer,
            COUNT(friend) AS recommendations_count,
            RecommendedPeer
        FROM (
                SELECT Peer1 AS peer,
                    Peer2 AS friend
                FROM friends
                UNION
                SELECT Peer2,
                    Peer1
                FROM friends
            ) f
            JOIN recommendations ON f.friend = recommendations.peer
            AND f.peer <> recommendations.recommendedpeer
        GROUP BY f.peer,
            recommendedpeer
    )
SELECT r.peer as Peer,
    recomend_count.RecommendedPeer
FROM (
        SELECT peer,
            MAX(recommendations_count) AS max_recomend_count
        FROM recomend_count
        GROUP BY peer
    ) r
    JOIN recomend_count ON r.peer = recomend_count.peer
    AND r.max_recomend_count = recomend_count.recommendations_count
ORDER BY Peer,
    RecommendedPeer
$$ LANGUAGE SQL;
--
-- 9) Определить процент пиров, которые:
--
-- Приступили к блоку 1
-- Приступили к блоку 2
-- Приступили к обоим
-- Не приступили ни к одному
--
-- Параметры процедуры: название блока 1, например CPP, название блока 2, например A.
-- Формат вывода: процент приступивших к первому блоку, процент приступивших ко второму блоку, 
-- процент приступивших к обоим, процент не приступивших ни к одному
DROP FUNCTION IF EXISTS PeersByGroups;
CREATE OR REPLACE FUNCTION PeersByGroups(block1 varchar, block2 varchar) RETURNS TABLE(StartedBlock1 bigint, StartedBlock2 bigint, StartedBothBlocks bigint, DidntStartAnyBlock bigint) AS $$ 
with count_peers as (
        select count(*) as count_
        from peers
    ),
    count_peers_C as (
        select count(*) as count_
        from (
                select distinct on (peer) *
                from checks
                where task ~ CONCAT('^', block2)
            ) as temp
    ),
    count_peers_CPP as (
        select count(*) as count_
        from (
                select distinct on (peer) *
                from checks
                where task ~ CONCAT('^', block1)
            ) as temp
    ),
    count_peers_none as (
        select count(*) as count_
        from peers
            left join (
                select *
                from checks
                where task ~ CONCAT('^(', block1,'|',block2,')')
            ) as temp on peers.nickname = temp.peer
        where id is null
    ),
    count_peers_all as (
        select count(*) as count_
        from (
                select distinct on (peer) peer
                from checks
                where task ~ CONCAT('^', block2)
                intersect
                select distinct on (peer) peer
                from checks
                where task ~ CONCAT('^', block1)
            ) as temp
    )
select (count_peers_C.count_ * 100 / count_peers.count_) as StartedBlock1,
    (
        count_peers_CPP.count_ * 100 / count_peers.count_
    ) as StartedBlock2,
    (
        count_peers_all.count_ * 100 / count_peers.count_
    ) as StartedBothBlocks,
    (
        count_peers_none.count_ * 100 / count_peers.count_
    ) as DidntStartAnyBlock
from count_peers_none,
    count_peers_C,
    count_peers,
    count_peers_all,
    count_peers_CPP;
$$ LANGUAGE SQL;
--
-- 10) Определить процент пиров, которые когда-либо успешно проходили проверку в свой день рождения
-- Также определите процент пиров, которые хоть раз проваливали проверку в свой день рождения.
-- Формат вывода: процент успехов в день рождения, процент неуспехов в день рождения
DROP FUNCTION IF EXISTS PeersWithBDayCheck;
CREATE OR REPLACE FUNCTION PeersWithBDayCheck() RETURNS TABLE(
        SuccessfulChecks int,
        UnsuccessfulChecks int
    ) AS $$ with temp_table as (
        select state
        from p2p
            left join checks on p2p.check_ = checks.id
            left join peers on checks.peer = peers.nickname
        where extract(
                day
                from checks.date
            ) = extract(
                day
                from peers.birthday
            )
            and extract(
                month
                from checks.date
            ) = extract(
                month
                from peers.birthday
            )
    ),
    all_count as (
        select count(*) as count_
        from temp_table
        where state = 'Start'
    ),
    success_count as (
        select count(*) as count_
        from temp_table
        where state = 'Success'
    ),
    failure_count as (
        select count(*) as count_
        from temp_table
        where state = 'Failure'
    )
select (
        success_count.count_ * 100 / CASE
            WHEN all_count.count_ = 0 THEN 1
            ELSE all_count.count_
        END
    ) AS SuccessfulChecks,
    (
        failure_count.count_ * 100 / CASE
            WHEN all_count.count_ = 0 THEN 1
            ELSE all_count.count_
        END
    ) AS UnsuccessfulChecks
from success_count,
    failure_count,
    all_count;
$$ LANGUAGE SQL;
--
-- 11) Определить всех пиров, которые сдали заданные задания 1 и 2, но не сдали задание 3
-- Параметры процедуры: названия заданий 1, 2 и 3. 
-- Формат вывода: список пиров
DROP FUNCTION IF EXISTS GivenAndNotGivenTasks;
CREATE OR REPLACE FUNCTION GivenAndNotGivenTasks(task1 varchar,
        task2 varchar,
        task3 varchar) RETURNS TABLE(peer varchar) AS $$ 
SELECT Peer
FROM (
        SELECT Peer,
            SUM(
                CASE
                    WHEN Task = task1 THEN 1
                    ELSE 0
                END
            ) AS is_task1_do,
            SUM(
                CASE
                    WHEN Task = task2 THEN 1
                    ELSE 0
                END
            ) AS is_task2_do,
            SUM(
                CASE
                    WHEN Task = task3 THEN 1
                    ELSE 0
                END
            ) AS is_task3_do
        FROM (
                SELECT DISTINCT Peer,
                    Task
                FROM xp
                    JOIN checks ON xp.check_ = checks.id
            ) ch
        GROUP BY Peer
    ) is_do
WHERE is_task1_do = 1
    and is_task2_do = 1
    and is_task3_do = 0;
$$ LANGUAGE SQL;

--
-- 12) Используя рекурсивное обобщенное табличное выражение, 
-- для каждой задачи вывести кол-во предшествующих ей задач
-- То есть сколько задач нужно выполнить, исходя из условий входа, 
-- чтобы получить доступ к текущей. 
-- Формат вывода: название задачи, количество предшествующих
DROP FUNCTION IF EXISTS CountOfPreviousTasks;
CREATE OR REPLACE FUNCTION CountOfPreviousTasks() RETURNS TABLE(Task varchar, PrevCount int) AS $$ 
WITH RECURSIVE recursiveQuery(title, parenttask, n) AS (
    SELECT tasks.title,
        tasks.parenttask,
        0
    FROM tasks
    UNION
    SELECT T.title,
        T.parenttask,
        n + 1
    FROM tasks T
        INNER JOIN recursiveQuery REC ON REC.title = T.parenttask
)
SELECT title AS Task,
    MAX(n) AS PrevCount
FROM recursiveQuery
GROUP BY title
ORDER BY PrevCount ASC;
$$ LANGUAGE SQL;
--
-- 13) Найти "удачные" для проверок дни. День считается "удачным", 
-- если в нем есть хотя бы N идущих подряд успешных проверки
-- Параметры процедуры: количество идущих подряд успешных проверок N. 
-- Временем проверки считать время начала P2P этапа. 
-- Под идущими подряд успешными проверками подразумеваются успешные проверки, между которыми нет неуспешных. 
-- При этом кол-во опыта за каждую из этих проверок должно быть не меньше 80% от максимального. 
-- Формат вывода: список дней
DROP FUNCTION IF EXISTS FindLuckyDaysForChecks;
CREATE OR REPLACE FUNCTION FindLuckyDaysForChecks(N int) RETURNS TABLE(date date) AS $$ 
WITH data AS(
        SELECT date,
            time,
            status_check,
            LEAD(status_check) OVER (
                ORDER BY date,
                    time
            ) AS next_status_check
        FROM (
                SELECT checks.date,
                    CASE
                        WHEN 100 * xp.XPAmount / tasks.MaxXP >= 80 THEN true
                        ELSE false
                    END AS status_check,
                    p2p.time
                FROM checks
                    JOIN tasks ON checks.task = tasks.title
                    JOIN xp ON checks.id = xp.check_
                    JOIN p2p ON checks.id = p2p.check_
                    AND p2p.state in('Success', 'Failure')
            ) ch
    ),
    data_prev_checks AS (
        SELECT t1.date,
            t1.time,
            t1.status_check,
            t1.next_status_check,
            COUNT (t2.date)
        FROM data t1
            JOIN data t2 on t1.date = t2.date
            AND t1.time <= t2.time
            AND t1.status_check = t2.next_status_check
        GROUP BY t1.date,
            t1.time,
            t1.status_check,
            t1.next_status_check
    )
SELECT date
FROM (
        SELECT date,
            MAX(success_count) AS max_success_count
        FROM (
                SELECT date,
                    count as success_count
                FROM data_prev_checks
                WHERE status_check
            ) success_checks
        GROUP BY date
    ) m
WHERE max_success_count >= N;
$$ LANGUAGE SQL;
--
-- 14) Определить пира с наибольшим количеством XP
-- Формат вывода: ник пира, количество XP
DROP FUNCTION IF EXISTS GetPeerWithMaxXp;
CREATE OR REPLACE FUNCTION GetPeerWithMaxXp() RETURNS TABLE(peer varchar, xp int) AS $$ 
SELECT peer,
    sum(xpamount) as xp
FROM xp
    JOIN checks c ON xp.check_ = c.id
GROUP BY peer
ORDER BY 2 DESC
LIMIT 1;
$$ LANGUAGE SQL;

--
-- 15) Определить пиров, приходивших раньше заданного времени не менее N раз за всё время
-- Параметры процедуры: время, количество раз N. 
-- Формат вывода: список пиров
DROP FUNCTION IF EXISTS GetPeerMaxTimeSpent;
CREATE OR REPLACE FUNCTION GetPeerMaxTimeSpent(TM time, N int) RETURNS TABLE(peer varchar, count int) AS $$ 
WITH t AS (SELECT DISTINCT peer,
            date,
            MIN(time) OVER (PARTITION BY peer, date) AS arrival_time
        FROM timetracking
        ORDER BY arrival_time
    )
SELECT t1.peer,
    count(t1.peer) AS count
FROM timetracking t1
    JOIN t t2 ON t1.time = t2.arrival_time
    AND t1.date = t2.date
    and t1.peer = t2.peer
WHERE arrival_time < TM
    AND t1.state = 1
GROUP BY t1.peer
HAVING count(t1.peer) > N;
$$ LANGUAGE SQL;

--
-- 16) Определить пиров, выходивших за последние N дней из кампуса больше M раз
-- Параметры процедуры: количество дней N, количество раз M.
-- Формат вывода: список пиров
DROP FUNCTION IF EXISTS GetPeersLeftCampus;
CREATE OR REPLACE FUNCTION GetPeersLeftCampus(N int,
        M int) RETURNS TABLE(peer varchar) AS $$ 
select timetracking.peer
from timetracking
where timetracking.state = 2
    and current_date - timetracking.date <= N
group by peer
HAVING count(*) > M;
$$ LANGUAGE SQL;

--
-- 17) Определить для каждого месяца процент ранних входов
-- Для каждого месяца посчитать, сколько раз люди, родившиеся в этот месяц, 
-- приходили в кампус за всё время (будем называть это общим числом входов).
-- Для каждого месяца посчитать, сколько раз люди, родившиеся в этот месяц, 
-- приходили в кампус раньше 12:00 за всё время (будем называть это числом ранних входов).
-- Для каждого месяца посчитать процент ранних входов в кампус относительно общего числа входов.
-- Формат вывода: месяц, процент ранних входов
DROP FUNCTION IF EXISTS PercentageOfEarlyEntries;
CREATE OR REPLACE FUNCTION PercentageOfEarlyEntries() RETURNS TABLE(month varchar, EarlyEntries int) AS $$ 
with gs as (
        select generate_series(1, 12) as month_
    ),
    all_count as (
        select date_part('month', timetracking.date) as month_,
            count(*) as count_
        from timetracking
            left join peers on timetracking.peer = peers.nickname
        where timetracking.state = '1'
            and date_part('month', peers.birthday) = date_part('month', timetracking.date)
        group by date,
            birthday
    ),
    early_count as (
        select date_part('month', timetracking.date) as month_,
            count(*) as count_
        from timetracking
            left join peers on timetracking.peer = peers.nickname
        where timetracking.state = '1'
            and date_part('month', peers.birthday) = date_part('month', timetracking.date)
            and timetracking.time < '12:00'
        group by date,
            birthday
    )
select to_char(to_timestamp(temp.month_::text, 'MM'), 'Mon') as month,
    case
        when temp.count1 = 0 then null
        else temp.count2 * 100 / temp.count1
    end as EarlyEntries
from (
        select gs.month_,
            case
                when all_count.count_ is null then 0
                else all_count.count_
            end as count1,
            case
                when early_count.count_ is null then 0
                else early_count.count_
            end as count2
        from gs
            left join all_count on all_count.month_ = gs.month_
            left join early_count on early_count.month_ = gs.month_
    ) as temp;
$$ LANGUAGE SQL;
