import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;
import java.util.*;
import java.util.Date;

class Server{ 


    public static void main(String[] args) throws Exception{
			
			ServerThread serverThread = new ServerThread();
    		serverThread.start();
    }

    private static class ServerThread extends Thread
    {

    		static ServerSocket socket = null;
    		static int port = 54345;
    		
			ServerThread() {
				super("Server");
			  	try{
			  		socket = new ServerSocket(port);
			        //socket.setSoTimeout(4000);
			       
			  	}catch(IOException e){

			  	}
			  	 System.out.println("Server listening on port: " + socket.getLocalPort());
			        
			  
			}  


			public void run(){

		
				try{
				  
				  String newLine = new String("\n");
				  Socket clientSocket = socket.accept();
				  InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream()); 
				  BufferedReader reader = new BufferedReader(isr); 
				  String line = reader.readLine(); 
				  FileOutputStream fileAppend = new FileOutputStream("file.html", true);
				  
				  String html = new String("<html><head><title>WEB CLIENT</title><table style=\"border:1px solid black;\">");
				  String tableRow = new String("<tr ><td style=\"border:1px solid black;\">");
				  String tableRowEnd = new String("</td></tr>");
				  String table = new String("<table style=\"border:1px solid black;\">");
				  String tableEnd = new String("<table style=\"border:1px solid black;\">");

				  clientSocket.getOutputStream().write(html.getBytes("UTF-8"));
				  fileAppend.write(table.getBytes("UTF-8"));
				  while (!line.isEmpty()) { 
				  					System.out.println(line); 
				  					clientSocket.getOutputStream().write(newLine.getBytes("UTF-8"));
				  					fileAppend.write(newLine.getBytes("UTF-8"));
				  					clientSocket.getOutputStream().write(tableRow.getBytes("UTF-8"));
				  					fileAppend.write(tableRow.getBytes("UTF-8"));
				  					clientSocket.getOutputStream().write(line.getBytes("UTF-8"));
				  					fileAppend.write(line.getBytes("UTF-8"));
				  					clientSocket.getOutputStream().write(tableRowEnd.getBytes("UTF-8"));
				  					fileAppend.write(tableRowEnd.getBytes("UTF-8"));
				  					line = reader.readLine(); 
				  				}

				  fileAppend.write(tableEnd.getBytes("UTF-8"));
				  String htmlEnd = new String("</table></html>");
		
				 




				}catch(IOException e){

				}
			
			}
    }

}