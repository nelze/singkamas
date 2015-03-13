package com.example.questionloader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class QuestionManager 
{
	private HashMap<Integer, List<Question>> questionMap = new HashMap<Integer, List<Question>>();
	
	
	public void loadQuestions(InputStream is, int difficulty)
	{
		QuestionParser parser = new QuestionParser();
    	List<Question> questionList = parser.parse(is, difficulty);
    	
    	//System.out.println(questionList);
    	
    	questionMap.put(difficulty, questionList);
	}
	
	
	public Question getQuestion(int difficulty, int index)
	{
		List<Question> list = questionMap.get(difficulty);
		
		//int randomIndex = (int) (Math.random() * list.size());
		
		return list.get(index);
		
	}
	
}
