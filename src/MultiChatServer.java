

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class MultiChatServer {

	private ServerSocket ss = null;
	private ServerSocket ssf = null;
	private Socket s = null;
	private Socket sf = null;
	ArrayList<ChatThread> chatlist = new ArrayList<ChatThread>();
	HashMap<String,Thread> map=new HashMap<>();	
	HashMap<String,String> map2=new HashMap<>();
	ArrayList<String> list=new ArrayList<>();
	ArrayList<byte[]> flist = new ArrayList<>();
	
	InputStream in = null;                       
    FileOutputStream out = null;
	
    private FileInputStream fin;
	private FileOutputStream fout;
	
	private OutputStream out2;
	

	public void start() {

		try {
			ss = new ServerSocket(8888);
			System.out.println("server start");
			
			ssf = new ServerSocket(7777);	// 파일 전송에 사용할 포트
			System.out.println("File Transfer server start");

			while (true) {
				s = ss.accept();

				ChatThread chat = new ChatThread(s);

				chatlist.add(chat);

				chat.start();
			}
		} catch (Exception e) {

			System.out.println("[MultiChatServer]start() Exception 발생!!");
		}
	}

	public static void main(String[] args) {
		MultiChatServer server = new MultiChatServer();
		server.start();
	}

	void msgSendAll(String msg) {
		for (ChatThread ct : chatlist) {
			ct.outMsg.println(msg);
			ct.outMsg.flush(); //현재 buffer에 저장된 내용을 client에 전송하고 buffer을 비어줌.
		}
	}
	

	public class ChatThread extends Thread {
		Socket s;

		String msg;
		String[] rmsg;

		public ChatThread(Socket s) {
			this.s = s;

		}

		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;
		
		public void fileTransfer(String filename,String id,int datas) throws IOException {
			// 파일 입력 로직
			int data = datas;
			try {
				for(ChatThread ct:chatlist) {		        	
		        	Thread getFromName=(Thread)map.get(rmsg[0]);
		        	if(!getFromName.equals(ct)) {
			        	ct.outMsg.println(rmsg[0]+"/"+"파일전송@@"+filename+"@@"+datas);
			        	sf = ssf.accept();
			        	
			        	out2 =sf.getOutputStream();                 //서버에 바이트단위로 데이터를 보내는 스트림을 개통합니다.
			        	DataOutputStream dout = new DataOutputStream(out2); //OutputStream을 이용해 데이터 단위로 보내는 스트림을 개통합니다.
			        	
			        	fin = new FileInputStream(new File(filename));	//FileInputStream - 파일에서 입력받는 스트림을 개통합니다.
			        	
			        	dout.writeInt(data);                   //데이터 전송횟수를 서버에 전송하고,
			        	dout.writeUTF(filename);               //파일의 이름을 서버에 전송합니다.
			        	System.out.println(id+"/"+"파일전송:"+filename+"@@"+data);
			        	
			        	byte[] buffer = new byte[1024];	//바이트단위로 임시저장하는 버퍼를 생성합니다.
			        	int len=0;	//전송할 데이터의 길이를 측정하는 변수입니다.
			        	
			        	outMsg.println(id+"/"+"파일전송:"+filename+"@@"+data);
			        	
//			        	for(;data>0;data--){                   //데이터를 읽어올 횟수만큼 FileInputStream에서 파일의 내용을 읽어옵니다.
//			        		len = fin.read(buffer);        //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
//			        		out2.write(buffer,0,len);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
//			        	}
			        	
			        	for(byte[] bt:flist) {
			        		out2.write(bt,0,len);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.			        		
			        	}
			        	
			        	System.out.println("약 "+datas+" kbyte");
			        	fin.close();
			        	dout.close();
			        	out2.close();
			        	sf.close();
			        	
		        	}else {								
						ct.outMsg.print("");
						ct.outMsg.flush();
					}
		        }
				
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		public void run() {

			boolean status = true;
			System.out.println("##ChatThread start...");
			
			try {

				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);

				while (status) {
			        msg = inMsg.readLine();
					System.out.println("1234>>>>>>>>>"+msg);
					rmsg = msg.split("/");
					
					map.put(rmsg[0],Thread.currentThread());
					map2.put(Thread.currentThread().getName(),rmsg[0]);
					//map2.put(Thread.currentThread(),rmsg[0]);

					if (rmsg[1].equals("logout")) {
						chatlist.remove(this);
						msgSendAll("server/" + rmsg[0] + "님이 종료했습니다."); // rmsg 클라이언트에서 데이터 가져옴
						map.remove(rmsg[0]);
						list.remove(rmsg[0]);

						status = false;
					}

					else if (rmsg[1].equals("login")) {  
						
						/*Thread getName = (Thread) map.get(rmsg[0]);
				        System.out.println(getName);
				        닉네임으로 스레드를 찾음.
				        */
						int i;
						list.add(rmsg[0]);
						msgSendAll("Server/" + rmsg[0] + "님이 로그인했습니다.");
						String getFromName=rmsg[0];
						
					}else if(rmsg[1].contains("파일전송@@")){
						/*** 받은 파일 서버에 임시 저장(현재 테스트 하기 위해 입력 받은 파일 경로와 동일하게 저장) ***/
						String[] str = rmsg[1].split("@@");	// 받은 메시지 분리하여 str 문자열 배열에 각각 저장
						
						sf = ssf.accept();
						
				        in = sf.getInputStream();                //클라이언트로 부터 바이트 단위로 입력을 받는 InputStream을 얻어와 개통합니다.				        
				        DataInputStream din = new DataInputStream(in);  //InputStream을 이용해 데이터 단위로 입력을 받는 DataInputStream을 개통합니다.
				        String filename="";
				        filename = din.readUTF()+"test";            //String형 데이터를 전송받아 filename(파일의 이름으로 쓰일)에 저장합니다.		
				        FileOutputStream fos = new FileOutputStream(filename);
				        BufferedOutputStream bos = new BufferedOutputStream(fos);
						
						byte[] bytes = new byte[8192];
						
						int len;
						
						while ((len = din.read(bytes))!=-1) {
							bos.write(bytes,0,len);
							bos.flush();
						}
						
						bos.close();
				        				        
//				        int data = din.readInt();           //Int형 데이터를 전송받습니다.
//				        int datas = data;                            //전송횟수, 용량을 측정하는 변수입니다.
//				        String filename="";
//				        filename = din.readUTF()+"test";            //String형 데이터를 전송받아 filename(파일의 이름으로 쓰일)에 저장합니다.					        
//				        File file = new File(filename);             //입력받은 File의 이름으로 복사하여 생성합니다.
//				        out = new FileOutputStream(file);           //생성한 파일을 클라이언트로부터 전송받아 완성시키는 FileOutputStream을 개통합니다.				        
//				        while(data > 0) {					        
//					        byte[] buffer = new byte[1024];        //바이트단위로 임시저장하는 버퍼를 생성합니다.
//					        int len,count=1;                               //전송할 데이터의 길이를 측정하는 변수입니다.
//					        
//					        for(;data>0;data--){                   //전송받은 data의 횟수만큼 전송받아서 FileOutputStream을 이용하여 File을 완성시킵니다.
//					            len = in.read(buffer);
//					            flist.add(buffer);
//					            System.out.println("1111111>>>>>>>>>>>>"+count+":"+len);
//					            count++;
//					            out.write(buffer,0,len);
//					        }					        
//				        }
				        din.close();
				        in.close();
				        out.flush();					        
				        out.close();				        
				        sf.close();
				        /*** 받은 파일 서버에 임시 저장 완료 ***/
				        
				        /*** 각 접속자에게 파일 전송 ***/
//				        fileTransfer(filename, rmsg[0],datas);	// 파일 보낸 접속자 이외의 접속자에게 파일 전송
				        /*** 각 접속자에게 파일 전송 완료 ***/
					}
					else if(rmsg[1].contains("귓속말:")) {   //substring(0,4)
					
						String to_user=rmsg[1].substring(4,rmsg[1].indexOf('~')); //받는사람 닉네임.
						Thread getName=(Thread)map.get(to_user);
						
						//from_user=rmsg[0]
						Thread getFromName=(Thread)map.get(rmsg[0]);
						
						for(ChatThread ct:chatlist) {
							if(ct.equals(getFromName)) {
								ct.outMsg.println(rmsg[0]+"/"+rmsg[1].substring(rmsg[1].indexOf('-')+1));  //클라이언트 한테 데이터 전달 할 때
								ct.outMsg.flush();
	
							}
						}
						for(ChatThread ct:chatlist) {
							if(ct.equals(getName)) {
								ct.outMsg.println(rmsg[0]+"/"+rmsg[1].substring(rmsg[1].indexOf('-')+1));
								ct.outMsg.flush();
								//ct.outMsg.println(msg);
							}
						}						
					}
					else {
						msgSendAll(msg);
					}
				}

				this.interrupt();
				System.out.println("##" + this.getName() + "stop!!");
			} catch (IOException e) {
				chatlist.remove(this);
				list.remove(rmsg[0]);
				map.remove(rmsg[0]);
				// e.printStackTrace();
				System.out.println("[ChatThread]run() IOException 발생!!");
			}
		}
	}

}