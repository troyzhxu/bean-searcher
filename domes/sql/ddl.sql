-- 学生表
CREATE TABLE `students` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL COMMENT '姓名',
  `age` int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
  `sex` int(11) NOT NULL DEFAULT '0' COMMENT '1:男, 2:女, 0: 未知',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 教师表
CREATE TABLE `teachers` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL DEFAULT '教师名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 课程表
CREATE TABLE `courses` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL COMMENT '课程名',
  `credit` int(11) NOT NULL DEFAULT '0' COMMENT '学分',
  `teacher_id` int(11) DEFAULT NULL COMMENT '教师ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 选课表
CREATE TABLE `student_course` (
  `course_id` int(11) NOT NULL COMMENT '课程ID',
  `student_id` int(11) NOT NULL COMMENT '学生ID',
  `score` float DEFAULT NULL COMMENT '分数',
  PRIMARY KEY (`student_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
