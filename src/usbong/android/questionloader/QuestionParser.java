package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuestionParser 
{
	
	
	
	public List<Question> parse(InputStream is, int difficulty)
	{
		List<Question> questionList = new ArrayList<Question>();
        try
        {
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String currentLine;
        	Question currentQuestion = null;
        	while((currentLine=br.readLine())!=null)
        	{
        		currentLine = currentLine.trim();
        		
        		if (currentLine.equals("") && currentQuestion!=null)
        		{
        			questionList.add(currentQuestion);
        			currentQuestion = null;
        		}        		
        		else if (Character.isDigit(currentLine.charAt(0)))
        		{
        			// digit, new question
        			currentQuestion = new Question();
        			int dotIndex = currentLine.indexOf('.');
        			String questionString = currentLine.substring(dotIndex+1).trim();
        			
        			currentQuestion.setQuestionText(questionString);
        			currentQuestion.setDifficulty(difficulty);
        		}
        		else if (currentLine.charAt(0)=='*')
        		{
        			// add answer
        				// remove letter 
        			int dotIndex = currentLine.indexOf('*');
        			String answerString = currentLine.substring(dotIndex+1).trim();       			
        			
        			currentQuestion.addAnswer(answerString); 
        		}
        	}
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        System.out.println(questionList);
        return questionList;
	}
}
