import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.math.BigInteger;

class Client
{

		static int port;
		static InetAddress address;

		static DatagramSocket socket;
	
		static DatagramPacket packet;		
		static byte[] sendData;
		static String message;
		static boolean withPort = true;
		static boolean askPort = true;


	public static void main(String args[]) throws IOException
	{
		while(true){
		System.out.print("Port:");
			BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
			try{

				port =  Integer.parseInt(inputFromUser.readLine());
				withPort = true;
				
			}catch(IOException e){
				withPort = false;

			}

		while(withPort){

			try{
				address = InetAddress.getByName("127.0.0.1");
			}catch(UnknownHostException e){
				System.out.println("Unknown Address");
			}
		
			
			
			try{

				socket = new DatagramSocket();
				socket.setSoTimeout(4000);  

			}catch(SocketException e){
				System.out.println("Failure to initialize socket.");
			}

			
			// initialize headers
			byte[] ackNumber = new byte[4];
				ackNumber[0] = 0;
				ackNumber[1] = 0;
				ackNumber[2] = 0;
				ackNumber[3] = 0;			
			byte[] seqNumber = new byte[4];
				new Random().nextBytes(seqNumber);
			System.out.println("  SEQ NUMBER SENT TO SERVER:"+Math.abs(new BigInteger(seqNumber).longValue()));
			BigInteger seqNum = new BigInteger(seqNumber).abs();
			System.out.println();

			byte flags = 2;
			byte[] winSize = new byte[2];
				new Random().nextBytes(winSize);

			byte[] messageData = new byte[1024];
			System.out.println("(ENTER 'disconnect' TO DISCONNECT): ");
			System.out.print("Your message: ");
			//client waiting for user input and then sends it to server
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			
			try{
				message = inFromUser.readLine();
				if(message.equals("disconnect")){
					
					try{
						fwdisconnect();
					}catch(Exception ex){}
					
					break;
				}
			}catch(IOException e){

			}

			
			messageData = message.getBytes();
			//System.out.println("data length:"+messageData.length);


			//==================================

			//* integrate into 1 packet
			sendData = new byte[messageData.length+11];
			sendData[0] = seqNumber[0];
			sendData[1] = seqNumber[1];
			sendData[2] = seqNumber[2];
			sendData[3] = seqNumber[3];
			sendData[4] = ackNumber[0];
			sendData[5] = ackNumber[1];
			sendData[6] = ackNumber[2];
			sendData[7] = ackNumber[3];
			sendData[8] = flags;
			sendData[9] = winSize[0];
			sendData[10] = winSize[1];
			
			int u=11;
			for(int i=0; i<messageData.length;i++){
				sendData[u] = messageData[i];
				u++;
			}
	
			//===========================

			
			packet = new DatagramPacket(sendData, sendData.length, address, port);
				
				try {
					socket.send(packet);					
			    } catch (SocketTimeoutException e) {
			       // resend
			       System.out.println("No server was found in this port.");
			       withPort = false;
			       break;
			      // socket.close();
			      
			    }
			

			//	
			//	wait for ACK to SYN
			//
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);					
						
						try{
							socket.receive(packet);	
						}catch(SocketTimeoutException e){
							System.out.println("No server was found in this port2.");	
							withPort = false;
							break;					    
						   
						   
						}

						try{								
								TimeUnit.SECONDS.sleep(2);
								//System.out.println("A Packet is received.");
						}catch (InterruptedException i) {
						        System.out.println("Error in TimeUnit.");
						}			

						InetAddress address = packet.getAddress();
						int port = packet.getPort();	

						//* DISSECTION OF THE PACKET
						byte[] receivedData = new byte[packet.getLength()];
						receivedData = packet.getData();


							seqNumber = new byte[4];
							seqNumber[0] = receivedData[0];
							seqNumber[1] = receivedData[1];
							seqNumber[2] = receivedData[2];
							seqNumber[3] = receivedData[3];	
						System.out.println("  SEQ NUMBER FROM SERVER:"+Math.abs(new BigInteger(seqNumber).longValue()));
						

						ackNumber = new byte[4];
							ackNumber[0] = receivedData[4];
							ackNumber[1] = receivedData[5];
							ackNumber[2] = receivedData[6];
							ackNumber[3] = receivedData[7];	
						System.out.println("  ACK NUMBER FROM SERVER:"+Math.abs(new BigInteger(ackNumber).longValue()));

						//System.out.println("  WIN SIZE:"+Math.abs(new BigInteger(winSize).longValue()));
						if(seqNum.compareTo(new BigInteger(ackNumber).abs().subtract(BigInteger.valueOf(1)))==0 ){
							System.out.println("  ACK NUMBER ACCEPTED. SENDING ACK NUMBER TO SERVER.");
							
							BigInteger ackNum = new BigInteger(seqNumber).abs().add(BigInteger.valueOf(1));
							ackNumber = ackNum.toByteArray();
							System.out.println("  ACK NUMBER SENT TO SERVER:"+Math.abs(new BigInteger(ackNumber).longValue()));
							System.out.println();
							sendData = new byte[messageData.length+11];
							sendData[0] = ackNumber[0];
							sendData[1] = ackNumber[1];
							sendData[2] = ackNumber[2];
							sendData[3] = ackNumber[3];
							sendData[4] = 0;
							sendData[5] = 0;
							sendData[6] = 0;
							sendData[7] = 0;
							sendData[8] = 16;
							sendData[9] = winSize[0];
							sendData[10] = winSize[1];
							u=11;
							for(int i=0; i<messageData.length;i++){
								sendData[u] = messageData[i];
								u++;
							}

							packet = new DatagramPacket(sendData, sendData.length, address, port);
							
							try{
								socket.send(packet);
							}catch(IOException e){

							}
							

						}

						
						System.out.println();
						socket.close();
		}

	}
	}
	public static void fwdisconnect() throws Exception{
		// initialize headers
			byte[] ackNumber = new byte[4];
				ackNumber[0] = 0;
				ackNumber[1] = 0;
				ackNumber[2] = 0;
				ackNumber[3] = 0;			
			byte[] seqNumber = new byte[4];
				new Random().nextBytes(seqNumber);
			System.out.println("  DISCONNECTION SEQ NUMBER SENT TO SERVER:"+Math.abs(new BigInteger(seqNumber).longValue()));
			BigInteger seqNum = new BigInteger(seqNumber).abs();
			System.out.println();

			byte flags = 1;
			byte[] winSize = new byte[2];
				new Random().nextBytes(winSize);

			byte[] messageData = new byte[4];
				messageData[0] = 0;
				messageData[1] = 0;
				messageData[2] = 0;
				messageData[3] = 0;
			//System.out.println("data length:"+messageData.length);


			//==================================

			//* integrate into 1 packet
			byte[] sendData = new byte[messageData.length+11];
			sendData[0] = seqNumber[0];
			sendData[1] = seqNumber[1];
			sendData[2] = seqNumber[2];
			sendData[3] = seqNumber[3];
			sendData[4] = ackNumber[0];
			sendData[5] = ackNumber[1];
			sendData[6] = ackNumber[2];
			sendData[7] = ackNumber[3];
			sendData[8] = flags;
			sendData[9] = winSize[0];
			sendData[10] = winSize[1];
			
			int u=11;
			for(int i=0; i<messageData.length;i++){
				sendData[u] = messageData[i];
				u++;
			}
	
			//===========================

			
			DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
				
			try {
					socket.send(packet);					
			} catch (IOException e) {
			       // resend
			      System.out.println("No server was found in this port.");
			     
			      // socket.close();		      
		    }


			    //** FOUR WAY DIS
			    //** WAITING FOR ACK FROM SERVER 


						byte[] data = new byte[1024];
						packet = new DatagramPacket(data, data.length);					
						
						try{
							socket.receive(packet);	
						}catch(IOException e){
							System.out.println("No server was found in this port.");						    
						    socket.close();
						   
						}

						try{								
								TimeUnit.SECONDS.sleep(2);
								//System.out.println("A Packet is received.");
						}catch (InterruptedException i) {
						        System.out.println("Error in TimeUnit.");
						}			

						InetAddress address = packet.getAddress();
						int port = packet.getPort();	

						//* DISSECTION OF THE PACKET
						byte[] receivedData = new byte[packet.getLength()];
						receivedData = packet.getData();


							seqNumber = new byte[4];
							seqNumber[0] = receivedData[0];
							seqNumber[1] = receivedData[1];
							seqNumber[2] = receivedData[2];
							seqNumber[3] = receivedData[3];	
						System.out.println("  DISCONNECTION SEQ NUMBER FROM SERVER:"+Math.abs(new BigInteger(seqNumber).longValue()));
						

						ackNumber = new byte[4];
							ackNumber[0] = receivedData[4];
							ackNumber[1] = receivedData[5];
							ackNumber[2] = receivedData[6];
							ackNumber[3] = receivedData[7];	
						System.out.println("  DISCONNECTION ACK NUMBER FROM SERVER:"+Math.abs(new BigInteger(ackNumber).longValue()));

						//System.out.println("  WIN SIZE:"+Math.abs(new BigInteger(winSize).longValue()));
						

					//* IF ACK FROM SERVER IS ACCEPTED OR NOT
						if(seqNum.compareTo(new BigInteger(ackNumber).abs().subtract(BigInteger.valueOf(1)))==0 ){
							System.out.println("  ACK FROM SERVER ACCEPTED. WAITING FOR SERVER. ");
							data = new byte[1024];
							packet = new DatagramPacket(data, data.length);					
							
							try{
								socket.receive(packet);	
							}catch(IOException e){
								System.out.println("No server was found in this port.");						    
							    socket.close();
							   
							}

							try{								
									TimeUnit.SECONDS.sleep(2);
									//System.out.println("A Packet is received.");
							}catch (InterruptedException i) {
							        System.out.println("Error in TimeUnit.");
							}			

							
							//* DISSECTION OF THE PACKET
							receivedData = new byte[packet.getLength()];
							receivedData = packet.getData();


								ackNumber = new byte[4];
								ackNumber[0] = receivedData[0];
								ackNumber[1] = receivedData[1];
								ackNumber[2] = receivedData[2];
								ackNumber[3] = receivedData[3];	
							System.out.println("  DISCONNECTION SEQ NUMBER FROM SERVER:"+Math.abs(new BigInteger(seqNumber).longValue()));
							

							seqNumber = new byte[4];
								seqNumber[0] = receivedData[4];
								seqNumber[1] = receivedData[5];
								seqNumber[2] = receivedData[6];
								seqNumber[3] = receivedData[7];	
							//System.out.println("  DISCONNECTION ACK NUMBER FROM SERVER:"+Math.abs(new BigInteger(ackNumber).longValue()));
							

							//Generate Client sequence number then send ACK to Server's Disconnection SYN
							seqNumber = new byte[4];
							new Random().nextBytes(seqNumber);
							System.out.println("  DISCONNECTION SEQ NUMBER SENT TO SERVER: "+Math.abs(new BigInteger(seqNumber).longValue()));
							seqNum = new BigInteger(seqNumber).abs();
							
							BigInteger ackNum = new BigInteger(ackNumber).abs().add(BigInteger.valueOf(1));
							ackNumber = ackNum.toByteArray();
							System.out.println("  DISCONNECTION ACK NUMBER SENT TO SERVER: "+Math.abs(new BigInteger(ackNumber).longValue()));
							flags = 17;

							//INTEGRATION INTO RESPONSE PACKET
							//* integrate into 1 packet
							sendData = new byte[messageData.length+11];
							sendData[0] = seqNumber[0];
							sendData[1] = seqNumber[1];
							sendData[2] = seqNumber[2];
							sendData[3] = seqNumber[3];
							sendData[4] = ackNumber[0];
							sendData[5] = ackNumber[1];
							sendData[6] = ackNumber[2];
							sendData[7] = ackNumber[3];
							sendData[8] = 17;
							sendData[9] = winSize[0];
							sendData[10] = winSize[1];
							
							u=11;
							for(int i=0; i<messageData.length;i++){
								sendData[u] = messageData[i];
								u++;
							}

							packet = new DatagramPacket(sendData, sendData.length, address, port);
							try{								
									TimeUnit.SECONDS.sleep(10);
									//System.out.println("A Packet is received.");
							}catch (InterruptedException i) {
							        System.out.println("Error in TimeUnit.");
							}	
							socket.send(packet);
							
							System.out.println("  FOURWAY DISCONNECTION SUCCESSFUL. ");

						}
						else{
							System.out.println("  ACK FROM SERVER NOT ACCEPTED. FOURWAY DISCONNECTION UNSUCCESSFUL. ");
						}

			//*
			//*		WAITING FOR SERVER TO SEND SYN
			//*

						
	}
}