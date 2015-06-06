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
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.time.DateUtils;

import tmm.Ngram;
import tmm.Topic;
import tmm.data.DataLoader;

import com.google.gson.Gson;
import com.timeline.Asset;
import com.timeline.Timeline;
import com.timeline.Timeline_;
import com.twitter.Tweet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String str;
	static String dataDirectory,tweetsFile;
	static List<String> stopWords = new LinkedList<String>();
	private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
	private static final String New_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss Z";
	private static SimpleDateFormat df = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
	private static SimpleDateFormat new_df = new SimpleDateFormat(New_DATE_FORMAT, Locale.ENGLISH);
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
	s */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doPost(request,response);
	}	
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try{
			
			Asset asset = new Asset();
			Timeline timeline = new Timeline();
			timeline.setTimeline(new Timeline_());
			timeline.getTimeline().setType("default");
			timeline.getTimeline().setAsset(asset);
	
		  String timeslot = request.getParameter("timeslot");
        //System.out.println("\nType 1(LDA), 2(Doc-p), 3(GraphBased), 4(sfim), 5(BNgram)");
        String menu = request.getParameter("menu");
        String nameOffile = request.getParameter("nameOffile");
        
		try {
			
			List<com.timeline.Date> timelineElements = DoIt(Integer.parseInt(timeslot),menu,nameOffile);
			timeline.getTimeline().setDate(timelineElements);
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /**********************************/
       
	        
	        response.setContentType("text/xml");
	        PrintWriter out = response.getWriter();
	       //out.println("Hello");
	        try {
	            /*  output   */
	        //String timelineJson = gson.toJson(timeline);
	        	//System.out.println(timelineJson);
	        	
	        	
	        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	    		
	    		// root elements
	    		Document doc = docBuilder.newDocument();
	    		Element rootElement = doc.createElement("data");
	    		rootElement.setAttribute("wiki-url", "http://simile.mit.edu/shelf/");
	    		rootElement.setAttribute("wiki-section", "Simile JFK Timeline");
	    		doc.appendChild(rootElement);
	    		
	    		System.out.println(timeline.getTimeline().getDate().size());
	    		int i=0;
	    		for(com.timeline.Date date : timeline.getTimeline().getDate()){
		    		Element event = doc.createElement("event");
		    		event.setAttribute("start", date.getStartDate());
		    		event.setAttribute("title", date.getHeadline());
		    		event.setAttribute("isDuration", "false");
		    		event.setAttribute("id", ""+i);
		    		rootElement.appendChild(event);
		    		i++;
	    		}
	    		
	    		//System.out.println("XML:"+doc.getTextContent());
	            //out.println(doc.getTextContent());
	    		
	    		// write the content into xml file
	    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    		Transformer transformer = transformerFactory.newTransformer();
	    		DOMSource source = new DOMSource(doc);
	    		StreamResult result1 = new StreamResult(out);
	     
	    		// Output to console for testing
	    		// StreamResult result = new StreamResult(System.out);
	     
	    		transformer.transform(source, result1);
	
	        } finally {
	            out.close();
	        }
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static List<com.timeline.Date> DoIt(int timeslot,String methodChoice,String nameOffile) throws Exception{
		
		List<com.timeline.Date> resultsDate=  new ArrayList();
		String inputstream = "tweetsFiles/"+nameOffile;
		BufferedReader bufferRd = new BufferedReader(new FileReader(inputstream)); 
		String  str="";
		List<Tweet> tweets = new ArrayList<Tweet>();
		while((str = bufferRd.readLine()) != null) 
		{	
          // System.out.println(str);
           Tweet tweet = gson.fromJson(str, Tweet.class);
           tweets.add(tweet);
		}
		bufferRd.close();

		System.out.println("size "+tweets.size()+" tweets" );
	
		Collections.sort(tweets);
	
		System.out.println("date = "+tweets.get(0).getCreated_at());
		if (!(tweets.get(0).getCreated_at()==null)){
		Date startDate = df.parse(tweets.get(0).getCreated_at());
		
		while(
				startDate.compareTo( df.parse(tweets.get(tweets.size()-1).getCreated_at())) <= 0
			){
			
			System.out.println(startDate);
			String txt = GetTweetsByTimeslot(tweets,startDate,timeslot,methodChoice,nameOffile);
			System.out.println("txt = "+txt);
			startDate = DateUtils.addMinutes(startDate, timeslot);
			com.timeline.Date newDate = new com.timeline.Date();
			newDate.setHeadline(txt);
			newDate.setStartDate(new_df.format(startDate));
			resultsDate.add(newDate);
		}
		}
		
		return resultsDate;
	}
	private static String GetTweetsByTimeslot(List<Tweet> tweets,Date startdate,int timeslot,String methodChoice,String nameOffile)
	throws Exception{
		
		
		ArrayList<Tweet> timeslotTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> prevTimeslotTweets = new ArrayList<Tweet>();
		
		System.out.println("start date "+startdate);
		System.out.println("Date + Timeslot"+ DateUtils.addMinutes(startdate,timeslot));
		String result="";
		int i=0;
		for(i=0;i < tweets.size();i++){
			if(
					df.parse(tweets.get(i).getCreated_at()).compareTo(DateUtils.addMinutes(startdate,timeslot)) <= 0
					&& df.parse(tweets.get(i).getCreated_at()).compareTo(startdate) >=0 ){
				
				timeslotTweets.add(tweets.get(i));
			}
		}
	
		System.out.println("Array size: "+timeslotTweets.size());
	
		String filename1 = CreateFileFromTweets(timeslotTweets,0,nameOffile);
		
		Date newdate = DateUtils.addMinutes(startdate,-1 * timeslot);
		for(i=0;i < tweets.size();i++){
			if(
					df.parse(tweets.get(i).getCreated_at()).compareTo(startdate) < 0  
				&& df.parse(tweets.get(i).getCreated_at()).compareTo(newdate) >= 0){
				System.out.println(tweets.get(i).getCreated_at());
			prevTimeslotTweets.add(tweets.get(i));
		  }
		}
		String filename2="";
		if (prevTimeslotTweets.size()>0){
		     filename2 = CreateFileFromTweets(prevTimeslotTweets,timeslot,nameOffile);}
	
		// TMM
		 result = TMM(methodChoice,filename1,filename2);
		
		return result;
	}
	
	private static String CreateFileFromTweets(List<Tweet> tweets,int timeslot,String nameOffile)
			throws ParseException, IOException{
		
		Date datetime1 = df.parse(tweets.get(0).getCreated_at());
		datetime1 = DateUtils.addMinutes(datetime1, timeslot);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(datetime1); 
		sdf = new SimpleDateFormat("dd");
		String day = sdf.format(datetime1);
		sdf = new SimpleDateFormat("MM");
		String month = sdf.format(datetime1);
		sdf = new SimpleDateFormat("hh");
		String hour = sdf.format(datetime1);
		sdf = new SimpleDateFormat("mm");
		String minute = sdf.format(datetime1);
		
		String filename = nameOffile+"_"+day+"_"+month+"_"+year+"_"+hour+"_"+minute+".json";
		//String filename = time1.substring(4,6)+"_"+time1.substring(3,5)+"_"+time1.substring(6)+"_"+time1.substring(0,2)+"_"+time1.substring(3)+".json";
		FileWriter fileWrite = new FileWriter(new File(filename));
		BufferedWriter bufferWr = new BufferedWriter(fileWrite);
		
		System.out.println("File Tweets size: "+tweets.size());
		for(Tweet t : tweets){

			bufferWr.write(gson.toJson(t)+"\n");
		}
		
		bufferWr.close();
		fileWrite.close();
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
				  result+=" \\ ";  
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
			          result += " \\ ";
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
			          result += " \\ ";  
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
			          result += " \\ ";  
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
			          result += " \\ ";  
		        }
        }
        
        return result;
	}

}

