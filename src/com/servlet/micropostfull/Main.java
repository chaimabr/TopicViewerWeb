package com.servlet.micropostfull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;

import tmm.Ngram;
import tmm.Topic;
import tmm.data.DataLoader;
import Utility.DateUtility;

import com.google.gson.Gson;
import com.twitter.Tweet;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String str;
	static String dataDirectory,tweetsFile;
	static List<String> stopWords = new LinkedList<String>();
	//private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
	//private static final String TWITTER_DATE_FORMAT ="MMM dd, yyyy HH:mm:ss a";

	static Gson gson = new Gson();

    /**
     * Default constructor. 
     */
    public Main() {
        // TODO Auto-generated constructor stub
    	System.out.println(new File(".").getAbsolutePath());
    	System.out.println(System.getProperty("java.classpath"));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}	
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try{
			
		
		String result="";
		
        String dat = request.getParameter("date");
        String timeslot = request.getParameter("timeslot");
        //System.out.println("\nType 1(LDA), 2(Doc-p), 3(GraphBased), 4(sfim), 5(BNgram)");
        String menu = request.getParameter("menu");
        String keyword = request.getParameter("keyword");
        
       // System.out.println(keyword+" "+dat+"  "+time1+"   "+time2+"  "+menu);
        
        System.out.println(timeslot+"  "+dat+"  "+menu);
        /***** Twitter Api  ****/
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("bJlsALLd7Gvaaeqp2a0ay5QZC")
		  .setOAuthConsumerSecret("vvBmATRDExtO9SjCHz56r21pEyNdJwgVVkcizVCe0JOdrMMQ4d")
		  .setOAuthAccessToken("925353571-frp0XXINItS3Udl1YxMjWsczgjXoX6tUOeIcqzzB")
		  .setOAuthAccessTokenSecret("Ur3RD1EaACazKLgtVmF48hSafQIwkcImtBm9qNBsBirDw");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		  final String OLD_FORMAT = "dd/MM/yyyy";
           final String NEW_FORMAT = "yyyy-MM-dd";
           String newDate;
           SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
           Date d=null;
		try {
			d = sdf.parse(dat);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
           sdf.applyPattern(NEW_FORMAT);
           newDate = sdf.format(d); 
	    Query query =(new  Query(keyword)).until(newDate);
	    QueryResult queryResult;
	    try{
	    new File("data.json").delete();
	    }catch(Exception e){}
	    FileWriter fileWriten = new FileWriter(new File("data.json"));
		BufferedWriter bufferWrn = new BufferedWriter(fileWriten);
		try {
			queryResult = twitter.search(query);
			for (Status status : queryResult.getTweets()) {
				bufferWrn.write(gson.toJson(status)+"\n");
		    }
			bufferWrn.close();	
			//System.out.println(result.getTweets());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			
			result = DoIt(Integer.parseInt(timeslot),dat,menu);
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /**********************************/
       
	        
	        response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        try {
	            /*  output   */
	
	            out.println(result);
	
	        } finally {
	            out.close();
	        }
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static String DoIt(int timeslot,String dat,String methodChoice) throws Exception{
		
		String result = "";
		String inputstream = "data.json";
		BufferedReader bufferRd = new BufferedReader(new FileReader(inputstream)); 
		String  str="";
		List<Tweet> tweets = new ArrayList<Tweet>();
		while((str = bufferRd.readLine()) != null) 
		{	
           //System.out.println(str);
           
           Tweet tweet = gson.fromJson(str, Tweet.class);
           tweets.add(tweet);
		}
		bufferRd.close();

		System.out.println("size "+tweets.size()+" tweets" );
		
		//System.out.println(tweets.get(0).getCreated_at());
		//System.exit(0);
		//Sorted list
		Collections.sort(tweets);
		
		//for(Tweet t : tweets) System.out.println(t.getCreated_at());
		//System.exit(0);
		
		
		//int i=0;
		Date startDate = DateUtility.stringToDate(tweets.get(0).getCreated_at());
		
		//System.out.println(startDate);
		//System.exit(1);
		
		while(
				startDate.compareTo( DateUtility.stringToDate(tweets.get(tweets.size()-1).getCreated_at())) <= 0
			){
			
			System.out.println(startDate);
			
			result += startDate.toString() + " : <br/>" +GetTweetsByTimeslot(tweets,startDate,timeslot,dat,methodChoice);
			startDate = DateUtils.addMinutes(startDate, timeslot);
		}
		
		
		return result;
	}
	private static String GetTweetsByTimeslot(List<Tweet> tweets,Date startdate,int timeslot,String dat,String methodChoice)
	throws Exception{
		
		
		ArrayList<Tweet> timeslotTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> prevTimeslotTweets = new ArrayList<Tweet>();
		
		System.out.println("start date "+startdate);
		//System.out.println(tweets.get(0).getCreated_at());
		//System.out.println(tweets.get(1).getCreated_at());
		System.out.println("Date + Timeslot"+ DateUtils.addMinutes(startdate,timeslot));
		
		int i=0;
		for(i=0;i < tweets.size();i++){
			if(
					DateUtility.stringToDate(tweets.get(i).getCreated_at()).compareTo(DateUtils.addMinutes(startdate,timeslot)) <= 0
					&& DateUtility.stringToDate(tweets.get(i).getCreated_at()).compareTo(startdate) >=0 ){
				
				timeslotTweets.add(tweets.get(i));
			}
		}
	
		System.out.println("Array size: "+timeslotTweets.size());
		String filename1 = CreateFileFromTweets(timeslotTweets,0);
		
		Date newdate = DateUtils.addMinutes(startdate,-1 * timeslot);
		for(i=0;i < tweets.size();i++){
			if(
				 DateUtility.stringToDate(tweets.get(i).getCreated_at()).compareTo(startdate) < 0  
				&& DateUtility.stringToDate(tweets.get(i).getCreated_at()).compareTo(newdate) >= 0){
				System.out.println(tweets.get(i).getCreated_at());
			prevTimeslotTweets.add(tweets.get(i));
		  }
		}
		String filename2="";
		if (prevTimeslotTweets.size()>0){
		     filename2 = CreateFileFromTweets(prevTimeslotTweets,timeslot);}
	
		// TMM
		
		
		String result = TMM(methodChoice,filename1,filename2);
		
		return result;
	}
	
	private static String CreateFileFromTweets(List<Tweet> tweets,int timeslot)
			throws ParseException, IOException{
		//String fileName="";
		
		String time1 = tweets.get(0).getCreated_at();
		//String time2 = tweets.get(tweets.size()-1).getCreated_at();
		
		Date datetime1 = DateUtility.stringToDate(time1);
		datetime1 = DateUtils.addMinutes(datetime1, timeslot);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String year = df.format(datetime1); 
		df = new SimpleDateFormat("dd");
		String day = df.format(datetime1);
		df = new SimpleDateFormat("MM");
		String month = df.format(datetime1);
		df = new SimpleDateFormat("hh");
		String hour = df.format(datetime1);
		df = new SimpleDateFormat("mm");
		String minute = df.format(datetime1);
		
		String filename = day+"_"+month+"_"+year+"_"+hour+"_"+minute+".json";
		//String filename = time1.substring(4,6)+"_"+time1.substring(3,5)+"_"+time1.substring(6)+"_"+time1.substring(0,2)+"_"+time1.substring(3)+".json";
		FileWriter fileWrite = new FileWriter(new File(filename));
		BufferedWriter bufferWr = new BufferedWriter(fileWrite);
		
		System.out.println("File Tweets size: "+tweets.size());
		for(Tweet t : tweets){

			bufferWr.write(gson.toJson(t)+"\n");
		}
		
		bufferWr.close();
		
		return filename;
	}
	
	private static String TMM(String choice,String filename,String filenamep){
		List<Topic> topics = null;
	    //String workingDir = System.getProperty("user.dir");
	    List<tmm.Tweet> tweets=DataLoader.loadData(filename);
	   
	    
	    String result="";
	    
		if ("1".equals(choice)) {
			//create the topic detector
			tmm.lda.TopicDetector topicDetectorLDA = new tmm.lda.TopicDetector();
			//get a list of topics by calling createTopics.
		     topics = topicDetectorLDA.createTopics(tweets);
		     if(topics == null) 
		    	 System.out.println("is null: true");
		     else 
		    	 System.out.println("is null: false");
			 // Printing topics
			  for(Topic t:topics) {
				  System.out.println("\t");
				  for(Ngram n:t.getKeywords()){
					  result += n.getTerm().toString().replaceAll(":", " ");
				  }
				  result+="<br>";  
		        }
        }
        if ("2".equals(choice)) {
        	tmm.documentpivot.TopicDetector topicDetectorDocPivot = new tmm.documentpivot.TopicDetector();
      
        	topics = topicDetectorDocPivot.createTopics(tweets);
        	// Printing topics
			  for(Topic t:topics) {
				  System.out.println("\t");
				  for(Ngram n:t.getKeywords())
					  result = n.getTerm().toString().replaceAll(":", " ");
			          result += "<br>";
		        }
        }
        if ("3".equals(choice)) {
        	tmm.graphbased.TopicDetector topicDetectorGraph = new tmm.graphbased.TopicDetector();
        	topics = topicDetectorGraph.createTopics(tweets);
        	// Printing topics
			  for(Topic t:topics) {
				  System.out.println("\t");
				  for(Ngram n:t.getKeywords())
					  result = n.getTerm().toString().replaceAll(":", " ");
			          result += "<br>";
		        }
        }
        if ("4".equals(choice)) {
        	tmm.sfim.TopicDetector topicDetectorSFIM = new tmm.sfim.TopicDetector();
	        topics = topicDetectorSFIM.createTopics(tweets);
        	// Printing topics
			  for(Topic t:topics) {
				  System.out.println("\t");
				  for(Ngram n:t.getKeywords())
					  result = n.getTerm().toString().replaceAll(":", " ");
			          result += "<br>";
		        }
        }
        if ("5".equals(choice)) {
        	tmm.bngram.TopicDetector topicDetectorBNgram = new tmm.bngram.TopicDetector("",filenamep);
        	topics = topicDetectorBNgram.createTopics(tweets);
        	// Printing topics
			  for(Topic t:topics) {
				  System.out.println("\t");
				  for(Ngram n:t.getKeywords())
					  result = n.getTerm().toString().replaceAll(":", " ");
			          result += "<br>";
		        }
        }
        
        return result;
	}

}

