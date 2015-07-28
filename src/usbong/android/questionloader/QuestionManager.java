package usbong.android.questionloader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class QuestionManager 
{
	private HashMap<Integer, List<Question>> questionMap = new HashMap<Integer, List<Question>>();
	
	List<Question> list;
	public void loadQuestions(InputStream is, int difficulty)
	{
		QuestionParser parser = new QuestionParser();
    	List<Question> questionList = parser.parse(is, difficulty);
    	//System.out.println(questionList);    	
    	System.out.println("loading..."+difficulty+questionList);
    	questionMap.put(difficulty, questionList);
	}
	public Question getQuestion(int difficulty, int index)
	{
		list = questionMap.get(difficulty);
		
		//int randomIndex = (int) (Math.random() * list.size());
		
		return list.get(index);	
	}
	public int getCount()
	{
		return list.size();
	}
	public int getCountMap()
	{
		return questionMap.size();
	}
}
