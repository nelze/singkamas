package com.example.questionloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question implements Cloneable
{
	public static final int DIFFICULTY_EASY 	= 1;
	public static final int DIFFICULTY_MEDIUM 	= 2;
	public static final int DIFFICULTY_HARD 	= 3;
	
	private int difficulty;
	private String questionText;
	private int correctIndex;
	private List<String> answers = new ArrayList<String>();
	
	
	/*public boolean isTrueFalseQuestion()
	{
		if (answers.size()==2)
		{
			// check values
			return true;
		}
		return false;
	}*/
	
	public String getCorrectAnswer()
	{
		return answers.get(correctIndex);
	}
	
	
	public void addAnswer(String answer)
	{
		answers.add(answer);
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public int getCorrectIndex() {
		return correctIndex;
	}

	public void setCorrectIndex(int correctIndex) {
		this.correctIndex = correctIndex;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public String toString() {
		return "Question [difficulty=" + difficulty + ", questionText="
				+ questionText + ", correctIndex=" + correctIndex
				+ ", answers=" + answers + "]\n";
	}
	
	public Question clone()
	{
		try
		{
			Question q = (Question) super.clone();
			q.answers = new ArrayList(answers);
			return q;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
