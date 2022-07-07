# Next

优化带 `groupBy` 的统计查询，例如：

```sql
select count(*) s_count 
from (
    select avg(score) c_2, sum(score) c_1, course_id c_0 
    from student_course2 
    group by course_id 
    having (sum(score) > 100)
) t_
```

可优化为：

```sql
select count(*) s_count 
from (
    select 1 from student_course2 
    group by course_id 
    having (sum(score) > 100)
) t_
```

再如：

```sql
select count(*) s_count, sum(c_2) s_sum_c_2
from (
    select avg(score) c_2, sum(score) c_1, course_id c_0 
    from student_course2 
    group by course_id 
    having (sum(score) > 100)
) t_
```

可优化为：

```sql
select count(*) s_count, sum(c_2) s_sum_c_2
from (
    select avg(score) c_2 from student_course2 
    group by course_id 
    having (sum(score) > 100)
) t_
```