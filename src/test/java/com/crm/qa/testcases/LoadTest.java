package com.crm.qa.testcases;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

import com.crm.qa.base.TestBase;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadTest extends TestBase implements IAnnotationTransformer{
	

	public LoadTest() {
		super();
	}

	//test cases should be separated -- independent with each other
	//before each test case -- launch the browser and login
	//@test -- execute test case
	//after each test case -- close the browser
	FileWriter fw;
	List<String> loadList =new ArrayList<String>(); 
	int tcount=Integer.parseInt(prop.getProperty("threadCount"));  
	int timeout=Integer.parseInt(prop.getProperty("timeout"));  
	@BeforeTest
	public void openfilewriter(){
		
		try {
			File file = new File(System.getProperty("user.dir")+ "/test-output/load_test.csv"); 
			if(file.delete()) 
	        { 
	            System.out.println("File deleted successfully"); 
	        } 
	        else
	        { 
	            System.out.println("Failed to delete the file"); 
	        } 
	    
		 fw=new FileWriter(file);
		 fw.write("Thread_ID,Response_Code,RequestSentTime(ms),ResponsReceivedTime(ms),TimeDifference(ms)");    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void loginPageTitleTest() throws InterruptedException{
		try {
			
			Long id = Thread.currentThread().getId();
	     //   System.out.println("Test method executing on thread with id: " + id);
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				Request request = new Request.Builder()
				  .url(prop.getProperty("url"))
				  .method("GET", null)
				  .build();
					Response response = client.newCall(request).execute();
					long timeDifference = response.receivedResponseAtMillis() -response.sentRequestAtMillis();
					//System.out.println("Difference: "+timeDifference+" ms");
					
					loadList.add("\n"+id+","+response.code()+","+response.sentRequestAtMillis()+" ms"+","+response.receivedResponseAtMillis()+" ms"+","+timeDifference+" ms");
					
			          // System.out.println(id+","+response.code()+","+response.sentRequestAtMillis()+" ms"+","+response.receivedResponseAtMillis()+" ms"+","+timeDifference+" ms");

//System.out.println("Thread ID: " + id+",Response Code: "+response.code()+",sentRequestAtMillis: "+response.sentRequestAtMillis()+" ms"+",receivedResponseAtMillis: "+response.receivedResponseAtMillis()+" ms"+" Difference: "+timeDifference+" ms");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	
	
	@AfterTest
	public void closefilewriter(){
		
		try {
			
			 for(String load:loadList) {  
				  System.out.println(load);  
			fw.write(load);   
			 }
			fw.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		if ("loginPageTitleTest".equals(testMethod.getName())) {
		      annotation.setInvocationCount(tcount);
		      annotation.setThreadPoolSize(tcount);
		      annotation.setInvocationTimeOut(timeout);
		    }

		
	}
}
