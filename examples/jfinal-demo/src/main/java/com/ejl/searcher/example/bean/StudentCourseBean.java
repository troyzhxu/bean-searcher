package com.ejl.searcher.example.bean;

import com.ejl.searcher.bean.DbField;
import com.ejl.searcher.bean.SearchBean;

@SearchBean(tables="student_course sc", groupBy="sc.student_id")
public class StudentCourseBean {

	@DbField("sc.course_id")
	private Long courseId;

	@DbField("avg(sc.score)")
	private double score;
	
	
	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
