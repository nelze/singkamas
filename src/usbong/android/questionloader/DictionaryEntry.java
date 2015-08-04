package usbong.android.questionloader;

import java.io.Serializable;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

public class DictionaryEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String definition;
	private String word;
	private int color;
	private int start;
	private int end;
	public DictionaryEntry(String word, String definition)
	{
		this.definition = definition;
		this.word = word;
		this.color = -1;
	}
	public DictionaryEntry(String word, String definition, int color)
	{
		this.definition = definition;
		this.word = word;
		this.color = color;
	}
	public DictionaryEntry(String word, String definition, int color, int start, int end)
	{
		this.definition = definition;
		this.word = word;
		this.color = color;
		this.start = start;
		this.end = end;
	}
	public DictionaryEntry(String word, String definition, int start, int end)
	{
		this.definition = definition;
		this.word = word;
		this.start = start;
		this.end = end;
		this.color = -1;
	}
	public void setColor(int color)
	{
		this.color = color;
	}
	public int getColor()
	{
		return color;
	}
	public String getDefinition()
	{
		return definition;
	}
	public String getWord()
	{
		return word;
	}
	public SpannableStringBuilder getSpannableString()
	{
		String temp = word+"\n"+definition;
		SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
		ssb.setSpan(new RelativeSizeSpan(2f), 0,word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new ForegroundColorSpan(0x93CCEA00), 0,word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}
	public int start()
	{
		return start;
	}
	public int end()
	{
		return end;
	}
}
