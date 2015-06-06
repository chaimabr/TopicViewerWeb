package eu.socialsensor.twcollect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.json.DataObjectFactory;
import eu.socialsensor.twcollect.util.FileUtil;



/**
 * Servlet implementation class Main
 */
@WebServlet("/MainFetch")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	

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
		doPost(request,response);
	}	
	
	
	static long lStartTime;
	static Object obj = new Object();
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	static String durationParam="";
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService es = Executors.newFixedThreadPool(processors);
		synchronized (obj) {
		// TODO Auto-generated method stub
		try{
			
        String keyword = request.getParameter("keyword");
        String fileNameParam = request.getParameter("filename");
        String userNameParam = request.getParameter("username");
        durationParam = request.getParameter("duration");
        lStartTime = new Date().getTime();
        
        
        System.out.println(keyword);
        /***** Twitter Api  ****/
        /*try{
    	    new File("data.json").delete();
    	    }catch(Exception e){}*/
        
		try {

			DoIt(keyword,fileNameParam,userNameParam);

			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /**********************************/
       
			obj.wait();
			    //obj.wait();
			
			
	        response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        try {
	            /*  output   */
	        	String fileName = "tweetsFiles/"+fileNameParam+"_by_"+userNameParam+"_"+Integer.parseInt(durationParam)/60+".json";
	        	BufferedReader bufferRd = new BufferedReader(new FileReader(fileName)); 
	        	String str="";
	    		while((str = bufferRd.readLine()) != null) 
	    		{
	    			out.println(str);
	    		}
	    		
	    		bufferRd.close();
	            
	
	        } finally {
	            out.close();
	        }
		} catch(Exception e){
			e.printStackTrace();
		}
		}
	}

	private static void DoIt(String keyword,String fileName,String userNameParam) throws Exception{
		//Main collector = new Main();
		Main collector = new Main();
		collector.setMaxJsonFileSize(100*1024); // 100MB batches
		//fileName = "tweetsFiles/"+fileName+"_by_"+userNameParam+".json";
		fileName = "tweetsFiles/"+fileName+"_by_"+userNameParam+"_"+Integer.parseInt(durationParam)/60+".json";
    	
		collector.open(fileName);
		
		long[] seeds = FileUtil.convertStringToLongs(
				FileUtil.readTokensFromFile("seeds.txt"));
		String[] keywords = keyword.split(",");
		
		FilterQuery filter = new FilterQuery(seeds);
		filter.track(keywords);
		
		collector.startFilter(filter);
	}
	
	
	protected BufferedWriter writer = null;
	protected StatusListener listener = null;
	protected long maxJsonFileSize = 0; // (in KB) if >0, then the collector tries to create output json files 
								       // of approximately that size (i.e. by creating multiple files)
	protected long currentFileSize = 0; // counts the size of the currently opened file
	protected int fileCounter = 0; // counts the number of files written so far
	
	// set maximum JSON file size (in KB)
	public void setMaxJsonFileSize(long fileSize){
		this.maxJsonFileSize = fileSize;
	}

	
	protected void open(final String tweetDump) {
		openWriter(tweetDump);
		
		listener = new StatusListener(){
			public void onStatus(Status status) {
				synchronized(obj){
				try {
					//System.out.println("Tweet collected ...");
					long lEndTime = new Date().getTime();
					 
					long difference = lEndTime - lStartTime;
					if(difference <= Integer.parseInt(durationParam)*1000){
					String line = DataObjectFactory.getRawJSON(status);
					//openWriter("tweets.json");
					writer.append(line);
					writer.newLine();
					currentFileSize += line.length();
					if (maxJsonFileSize > 0){
						if (currentFileSize >= maxJsonFileSize*1024){
							closeWriter();
							fileCounter++;
							currentFileSize = 0;
							openWriter(tweetDump + "." + fileCounter);
						}
					}
					}
					else {
						closeWriter();
					
						obj.notifyAll();
						//System.exit(0);
						
					}
				} catch (IOException e){
					e.printStackTrace();
					closeWriter();
				}
				}
			}
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
			@Override
			public void onScrubGeo(long arg0, long arg1) {
			}
			@Override
			public void onStallWarning(StallWarning arg0) {
				//System.out.println(arg0.toString());
			}
		};
		//Runtime.getRuntime().addShutdownHook(new Shutdown(this));
	}
	
	protected void startFilter(FilterQuery filter){
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		twitterStream.filter(filter);
	}
	
	protected void close(){
		closeWriter();
	}
	
	
	protected void openWriter(String file){
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file,true), FileUtil.UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void closeWriter(){
		if (writer != null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Class in case system is shutdown: Responsible to close all services 
	 * that are running at the time being.
	 */
	/*protected class Shutdown extends Thread {
		Main process = null;

		public Shutdown(Main main) {
			this.process = main;
		}

		public void run() {
			System.out.println("Shutting down collector...");
			if (process != null) {
				process.close();
			}
			System.out.println("Done...");
		}
	}*/
	
	
		
		
		
	}

	


