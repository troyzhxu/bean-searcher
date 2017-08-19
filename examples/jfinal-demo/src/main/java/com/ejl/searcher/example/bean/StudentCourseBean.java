package com.ejl.searcher.example.bean;

import com.ejl.searcher.bean.DbField;
import com.ejl.searcher.bean.SearchBean;

@SearchBean(tables="student_course sc", groupBy="sc.student_id")
public class StudentCourseBean {

	@DbField("sc.student_id")
	private Long studentId;

	@DbField("avg(sc.score)")
	private double score;
	

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
