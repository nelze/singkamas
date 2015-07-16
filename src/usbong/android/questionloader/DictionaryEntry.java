package usbong.android.questionloader;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

public class DictionaryEntry {
	private String definition;
	private String word;
	public DictionaryEntry(String word, String definition)
	{
		this.definition = definition;
		this.word = word;
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
}
