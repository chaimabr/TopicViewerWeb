package Utility;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtility {
	private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
	public static Date stringToDate(String datestring) throws ParseException{
		Date twiDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        if (!(datestring == null)){
       	twiDate = df.parse(datestring);
       	 }
		return twiDate;
	}

}
