

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class MultiChatClient implements ActionListener, Runnable {
	private String ip;
	private String id;
	private Socket socket;
	private Socket socketFile;
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;
//	private FileInputStream fin;
	private FileOutputStream fout;
	private OutputStream out;
	private static InputStream in;
	
	private BufferedInputStream bis;
	private BufferedOutputStream bos;

	private JPanel loginPanel;
	private JButton loginButton;
	private JLabel label1;
	private JTextField idInput;

	private JPanel logoutPanel;
	private JLabel label2;
	private JButton logoutButton;

	private JPanel msgPanel;
	private JTextField msgInput;
	private JButton exitButton;

	

	private JFrame jframe;
//	private JTextArea msgOut;

	private Container tab;
	private CardLayout clayout;
	private Thread thread;
	private MultiChatServer clients;
	
	private JPanel functionPanel;
	private JButton emoticon;
	private JButton photo;
	private JRadioButton radio1 = new JRadioButton(); // 귀속말
	private JRadioButton radio2 = new JRadioButton(); // 귀속말 해제.
	private ButtonGroup group = new ButtonGroup();
	
	private JTextPane msgOut;
	
	private ImageIcon btImage;
	private ImageIcon btImage2;
	
	private JFileChooser fd;

	// 접속자 리스트
//	private JPanel listPanel;
//	private JLabel list;
//	private JTextArea clientlist;
//	private JLabel num;

	private JPanel panel2;
	
	

	// private List list=new List(); //닉네임 저장
	// int count=0; //인원수
	// HashMap<InetAddress,String> map=new HashMap<>();

	boolean status;
	ImageIcon icon;
	ImageIcon icon2;
	ImageIcon icon3;
	ImageIcon icon4;
	ImageIcon icon5;
	ImageIcon icon6;
	ArrayList userlist=new ArrayList();

	public MultiChatClient(String ip) {
		
		BufferedImage img=null;
		
		
		this.ip = ip;

		JFrame jframe = new JFrame("Silver Multi Chat");
		jframe.setLayout(new BorderLayout());

		// 접속자 목록
//		icon2=new ImageIcon("pink.jpg");
//		listPanel = new JPanel(){
//			public void paintComponent(Graphics g) {
//				//g.drawImage(icon.getImage(),0,0,null);//이미지 원래사이즈로 가져오기 
//				Dimension d = getSize();
//                g.drawImage(icon2.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
//                setOpaque(false);
//				super.paintComponent(g);
//			}
//			
//		};

//		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
//		list = new JLabel("접속자 목록");
//		clientlist = new JTextArea("", 10, 10);
//		clientlist.setText("");
//
//		//clientlist.append("dd");
//		JScrollPane jsp2 = new JScrollPane(clientlist);
//
//		clientlist.setEditable(false); // 편집못하게 막기
//		listPanel.add(list);
//		listPanel.add(jsp2);

		// login창,대화명 입력
		icon3=new ImageIcon("pink.jpg");
		loginPanel = new JPanel(){
			public void paintComponent(Graphics g) {
				//g.drawImage(icon.getImage(),0,0,null);//이미지 원래사이즈로 가져오기 
				Dimension d = getSize();
                g.drawImage(icon3.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
                setOpaque(false);
				super.paintComponent(g);
			}
			
		};

		idInput = new JTextField(13);
		loginButton = new JButton("로그인");
		loginButton.addActionListener(this);
		label1 = new JLabel("대화명");

		loginPanel.add(label1);
		loginPanel.add(idInput);
		loginPanel.add(loginButton);

		// 위쪽-로그인 됐을때
		icon4=new ImageIcon("pink.jpg");
		logoutPanel = new JPanel(){
			public void paintComponent(Graphics g) {
				//g.drawImage(icon.getImage(),0,0,null);//이미지 원래사이즈로 가져오기 
				Dimension d = getSize();
                g.drawImage(icon4.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
                setOpaque(false);
				super.paintComponent(g);
			}
			
		};

		logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.X_AXIS));
		label2 = new JLabel();
		logoutButton = new JButton("로그아웃");
		

		logoutButton.addActionListener(this);

		logoutPanel.add(label2);
		
		logoutPanel.add(logoutButton);
		
		
		//function 기능 패널
		icon6=new ImageIcon("pink.jpg");
		
		functionPanel=new JPanel(){
			public void paintComponent(Graphics g) {
		//		g.drawImage(icon.getImage(),0,0,null);//이미지 원래사이즈로 가져오기 
				Dimension d = getSize();
				
                g.drawImage(icon6.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
                setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		btImage=new ImageIcon("smile.png");
//		Image image=btImage.getImage();
//		Image changeImg=image.getScaledInstance(300, , Image.SCALE_SMOOTH);
//		
//		ImageIcon changeIcon=new ImageIcon(changeImg);
		
		emoticon=new JButton() {
			public void paintComponent(Graphics g) {
						g.drawImage(btImage.getImage(),30,2,null);//이미지 원래사이즈로 가져오기 
						Dimension d = getSize();
		          //      g.drawImage(btImage.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
		                
		                setOpaque(false);
						super.paintComponent(g);
					}
		};
		
		
		emoticon.addActionListener(this);
		emoticon.setBorderPainted(true);
		emoticon.setFocusPainted(true);
		emoticon.setContentAreaFilled(false);
		/*
		 emoticon.setBorderPainted(false);
		emoticon.setFocusPainted(false);
		emoticon.setContentAreaFilled(false);
		 */
		
		btImage2=new ImageIcon("camera.png");
		photo=new JButton() {
			public void paintComponent(Graphics g) {
					g.drawImage(btImage2.getImage(),0,4,null);//이미지 원래사이즈로 가져오기 
					Dimension d = getSize();
	            //    g.drawImage(btImage2.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
	                
	                setOpaque(false);
					super.paintComponent(g);
				}
		};
		photo.addActionListener(this);
		photo.setPreferredSize(new Dimension(5,5));
		photo.setBorderPainted(true);
		photo.setFocusPainted(true);
		photo.setContentAreaFilled(true);
		
		/*
		 photo.setBorderPainted(false);
		photo.setFocusPainted(false);
		photo.setContentAreaFilled(false);
		 * 
		 */
		
		
		functionPanel.setLayout(new BoxLayout(functionPanel,BoxLayout.X_AXIS));
		radio1.setText("귓속말");
		radio2.setText("귓속말 해제");
		radio2.setSelected(true);
		
		group.add(radio1);
		group.add(radio2);
		
		
		functionPanel.add(radio1);
		functionPanel.add(radio2);
		functionPanel.add(emoticon);
		functionPanel.add(photo);
		

		// text입력 영역
		icon5=new ImageIcon("pink.jpg");
		msgPanel = new JPanel(){
			public void paintComponent(Graphics g) {
				//g.drawImage(icon.getImage(),0,0,null);//이미지 원래사이즈로 가져오기 
				Dimension d = getSize();
                g.drawImage(icon5.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
                setOpaque(false);
				super.paintComponent(g);
			}
			
		};

		
		//아래영역 텍스트 작성하는 패널 
		msgPanel.setLayout(new BorderLayout());
		msgInput = new JTextField();
		JScrollPane scroll=new JScrollPane(msgInput);
		scroll.setPreferredSize(new Dimension(70,130));

		msgInput.addActionListener(this);
		exitButton = new JButton("종료");
		exitButton.addActionListener(this);

		// 아래 텍스트 작성하는 곳
		msgPanel.add(scroll, BorderLayout.CENTER);
		msgPanel.add(exitButton, BorderLayout.EAST);

		
		
		tab = new JPanel();
		clayout = new CardLayout();
		tab.setLayout(clayout);
		tab.add(loginPanel, "login");
		tab.add(logoutPanel, "logout");
		
		//텍스트 올라오는 영역 

	/*	msgOut = new JTextArea("", 23, 10);
		JScrollPane jsp = new JScrollPane(msgOut);
		msgOut.setEditable(false);
*/		
		msgOut=new JTextPane();
		msgOut.setPreferredSize(new Dimension(23,360));
		msgOut.setBackground(Color.PINK);
//		StyledDocument doc=msgOut.getStyledDocument();
//		SimpleAttributeSet keyWord = new SimpleAttributeSet();
	//	StyleConstants.setForeground(keyWord, Color.RED);
	//	StyleConstants.setBackground(keyWord, Color.YELLOW);
	//	StyleConstants.setBold(keyWord, true);
		
		JScrollPane jsp=new JScrollPane(msgOut);
		msgOut.setEditable(false);
		
		
		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		panel2.add(tab);
		panel2.add(jsp);
		panel2.add(functionPanel);
		panel2.add(msgPanel);
		
		clayout.show(tab, "login");
		
		
		//프레임 배경 
		icon=new ImageIcon("flower.jpg");
		JPanel back=new JPanel() {
			public void paintComponent(Graphics g) {
				Dimension d = getSize();
                g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null); //프레임에 맞춰서 가져오기 
                setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		back.add(panel2,BorderLayout.WEST);
//		back.add(listPanel,BorderLayout.EAST);
		
		jframe.pack();
		jframe.add(back,BorderLayout.CENTER);
		jframe.setVisible(true);		
		jframe.setSize(320,630);
		jframe.setResizable(false);
		
		
	}

	public void connectServer() {

		try {

			socket = new Socket(ip, 8888);

			System.out.println("[Client]Server 연결 성공!!");

			System.out.println();

			in = socket.getInputStream();
			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
			outMsg = new PrintWriter(socket.getOutputStream(), true);

			outMsg.println(id + "/" + "login");

			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			System.out.println("[MultiChatClient]connectServer() Exception 발생!!");
		}
	}

	// 서버에 데이터 전송
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();

		if (obj == exitButton) {
			System.exit(0);
		} else if (obj == loginButton) {
			
			id = idInput.getText();
			label2.setText("대화명 : " + id);
			clayout.show(tab, "logout");
			connectServer();			
		} else if (obj == logoutButton) {
//			clientlist.setText("");

			outMsg.println(id + "/" + "logout");
			

			msgOut.setText("");

			clayout.show(tab, "login");
			outMsg.close();
			try {
				inMsg.close();
				socket.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			status = false;
		} else if (obj == msgInput) {
			if (radio1.isSelected() == true) {				
				outMsg.println(id + "/" + "귓속말:" + msgInput.getText());
			} else if (radio2.isSelected() == true) {
				outMsg.println(id + "/" + msgInput.getText());
			} else {				
				outMsg.println(id + "/" + msgInput.getText());				
			}
			
			msgInput.setText("");
		}else if(obj==photo) {
			
			File savefile;
			String savepathname;
			String filename;
			StyledDocument doc=msgOut.getStyledDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			
			fd=new JFileChooser();
			fd.setDialogTitle("전송할 파일 선택");
			int ret=fd.showOpenDialog(null); //파일 열기 
			
			if(ret != JFileChooser.APPROVE_OPTION) {
	            JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.","알림",JOptionPane.WARNING_MESSAGE);
	            return;
	        }
			
			String filePath=fd.getSelectedFile().getPath();
			
			// 파일 입력 전송			
			try {
				fileTransfer(filePath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
		}//else if(obj==photo)
	}
	
	public void fileTransfer(String filePath) throws IOException {
		// 파일 전송 로직
		try {
			socketFile = new Socket(ip, 7777);
			System.out.println("[Client]File Transfer Server 연결 성공!!");
			
			outMsg.println(id+"/"+"파일전송@@"+filePath);
			
			out =socketFile.getOutputStream();                 //서버에 바이트단위로 데이터를 보내는 스트림을 개통합니다.
            DataOutputStream dout = new DataOutputStream(out); //OutputStream을 이용해 데이터 단위로 보내는 스트림을 개통합니다.            

//			fin = new FileInputStream(new File(filePath));	//FileInputStream - 파일에서 입력받는 스트림을 개통합니다.
            FileInputStream fin = new FileInputStream(filePath);	//FileInputStream - 파일에서 입력받는 스트림을 개통합니다.
            BufferedInputStream bis = new BufferedInputStream(fin);
            
			byte[] buffer = new byte[1024];	//바이트단위로 임시저장하는 버퍼를 생성합니다.
			int len;	//전송할 데이터의 길이를 측정하는 변수입니다.
			
			while ((len = bis.read(buffer)) != -1) {
				dout.write(buffer,0,len);
				dout.flush();
			}
	        
//			int data=0; //전송횟수, 용량을 측정하는 변수입니다.
//	        while((len = fin.read(buffer))>0){     //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
//	            data++;                        //데이터의 양을 측정합니다.
//	        }
//	        
//	        int datas = data;                      //아래 for문을 통해 data가 0이되기때문에 임시저장한다.
//	        
//	        outMsg.println(id+"/"+"파일전송@@"+filePath+"@@"+data);
//	 
//	        fin.close();
//	        fin = new FileInputStream(filePath);   //FileInputStream이 만료되었으니 새롭게 개통합니다.		        
//	        
//	        dout.writeInt(data);                   //데이터 전송횟수를 서버에 전송하고,
//	        dout.writeUTF(filePath);               //파일의 이름을 서버에 전송합니다.
////	        System.out.println(id+"/"+"파일전송:"+filePath+"@@"+data);		        
//
//	        len = 0;
//	        int count=1;
//	        
//	        for(;data>0;data--){                   //데이터를 읽어올 횟수만큼 FileInputStream에서 파일의 내용을 읽어옵니다.
//	            len = fin.read(buffer);        //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
//	            System.out.println("999999>>>>>>>>>>>>"+count+":"+len);
//	            count++;
////	            out.write(buffer,0,len);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
//	            out.write(buffer);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
//	        }
	        dout.close();
	        fin.close();
	        out.close();
	        socketFile.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		String msg;
		String[] rmsg;
		StyledDocument doc=msgOut.getStyledDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();

		status = true;

		while (status) {
			try {
				System.out.println("222222222");
				msg = inMsg.readLine();
				rmsg = msg.split("/");
				
//				if (rmsg[0].equals("접속자")) {
//					clientlist.setText(rmsg[1]+"\n");
//				}
				
				if(rmsg[1].contains("씨발")||rmsg[1].contains("시발")||rmsg[1].contains("병신")||rmsg[1].contains("미친")||rmsg[1].contains("새끼")) {
					
					try {
						
					
					doc.insertString(doc.getLength(),rmsg[0] + ">" + rmsg[1].replace("씨발", "**")
							.replace("시발","**")
							.replace("병신","**")
							.replace("미친","**")
							.replace("새끼","**")+"\n",keyWord);
					}catch(Exception e) { System.out.println(e); }
					
				}
//				else if(rmsg[1].contains("파일전송:")) {
//	
//					File savefile;
//					String savepathname = null;
//					String filename;
//					
//					fd=new JFileChooser();
//					//fd.setDialogTitle("전송할 파일 선택");
//					fd.setDialogTitle("받은 파일 저장");
//
//					int re=fd.showSaveDialog(null); //파일 저장
//					
//					if(re==JFileChooser.APPROVE_OPTION) {
//						savefile=fd.getSelectedFile();
//						savepathname=savefile.getAbsolutePath();
//			        }
//					
//					try {
//						OutputStream fout=new FileOutputStream(savepathname);
//						
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
				else if(rmsg[1].contains("파일전송@@")) {
					socketFile = new Socket(ip, 7777);
					System.out.println("[Client]File Transfer Server 연결 성공!!");
					
					File savefile;
					String savepathname = null;					
					
//					fd=new JFileChooser();
//					//fd.setDialogTitle("전송할 파일 선택");
//					fd.setDialogTitle("받은 파일 저장");
//					
//					int re=fd.showSaveDialog(null); //파일 저장
//					
//					if(re==JFileChooser.APPROVE_OPTION) {
//						savefile=fd.getSelectedFile();
//						savepathname=savefile.getAbsolutePath();
//			        }
					
					String[] str = rmsg[1].split("@@");	// 서버로 부터 입력 받은 파일 경로와 파일 사이즈 분리
					
			        in = socketFile.getInputStream();                //서버로 부터 바이트 단위로 입력을 받는 InputStream을 얻어와 개통합니다.
			        DataInputStream din = new DataInputStream(in);  //InputStream을 이용해 데이터 단위로 입력을 받는 DataInputStream을 개통합니다.			        
			        int data = Integer.parseInt(str[2]);           //Int형 데이터를 전송받습니다.
			        int datas = data;                            //전송횟수, 용량을 측정하는 변수입니다.
			        String filename = str[1]+"test";            //String형 데이터를 전송받아 filename(파일의 이름으로 쓰일)에 저장합니다.					        
			        File file = new File(filename);             //입력받은 File의 이름으로 복사하여 생성합니다.				        
			        out = new FileOutputStream(file);           //생성한 파일을 클라이언트로부터 전송받아 완성시키는 FileOutputStream을 개통합니다.
			        while(data > 0) {
				        byte[] buffer = new byte[1024];        //바이트단위로 임시저장하는 버퍼를 생성합니다.
				        int len,count=1;                               //전송할 데이터의 길이를 측정하는 변수입니다.
				        
				        for(;data>0;data--){                   //전송받은 data의 횟수만큼 전송받아서 FileOutputStream을 이용하여 File을 완성시킵니다.				        	
				            len = in.read(buffer);				            
				            System.out.println("999999>>>>>>>>>>>>"+count+":"+len);
				            count++;
				            out.write(buffer,0,len);
				        }
				        
				        System.out.println("약: "+datas+" kbps");				        
			        }
			        out.flush();
			        out.close();
			        socketFile.close();
				}
				else {
					
					try {
						doc.insertString(doc.getLength(),rmsg[0] + ">" + rmsg[1] +"\n",keyWord);
						
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 서버에서 받아온 데이터 출력
				}

//				msgOut.setCaretPosition(msgOut.getDocument().getLength());
			} catch (IOException e) {
				// e.printStackTrace();
				status = false;
			}
		}

		System.out.println("[MultiChatClient]" + thread.getName() + "종료됨");

	}

	public static void main(String[] args) {
		MultiChatClient mcc = new MultiChatClient("192.168.10.9");

	}
}