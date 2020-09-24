package kjh.board;

import java.sql.*;
import java.util.*; //ArrayList, List�� ���

//DB�� �����ؼ� ���󿡼� ȣ���� �޼��带 �ۼ��ϴ� Ŭ����.
public class BoardDAO {
	private DBConnectionMgr pool = null; //1. ������ ��ü�� ��������� �����Ѵ�.
	Connection con = null; //���Ӱ�ü
	PreparedStatement pstmt = null; //sql���� ���� ��
	ResultSet rs = null; // �ʱⰪ (select���� ���̺� ���·� �ޱ� ���� �ʿ�)
	String sql = ""; //sql������ ������ ����
	
	public BoardDAO(){//2. ������ - �ʱⰪ ����
		//DB�� �������� ���ߴٸ�
		try {
			pool = DBConnectionMgr.getInstance(); //�����޼���� Ŭ������.�����޼����(); ���� ȣ��
			System.out.println("pool=>" + pool);
		}catch(Exception e) {
			System.out.println("DB���� ����(pool�� ������ ����)=>" + e);
		}
	}
	
	//1. �� ���ڵ���� �����ִ� �޼��� �ʿ�
		//�޼��� ����� ��Ģ -> ���� ���� SQL���� �����.
		//����) select count(*) from board;
	//���󿡼��� ���������ڴ� ���� �� public, select�� ��ȯ���� �ִ�., where������ ������ �Ű����� ����. 
	public int getArticleCount() {
		//1. DB���� ����
		int x = 0;
		//2. SQL���� - ������ �߻��� �� �ذ��ϱ� ����.
		try {
			con = pool.getConnection(); //con�� pool��ü���� ���´�. (10�� �߿��� �� ���� �����ش�) -> default���� 10�̴�.
			                                      // -> Ŀ�ؼ� Ǯ (�ʿ��� ������ ��ü(connection)�� �����ְ�, �ʿ� ������ �ݳ��Ѵ�.)
			                                     //�̸� ���� �����ش�. (�ణ ������ũ�� �������� ���� ����...?)
			System.out.println("con=>" + con);
			sql = "select count(*) from board"; //count(*)�� �ʵ尡 �ƴϴ�. 
			pstmt = con.prepareStatement(sql);//sql�������
			rs = pstmt.executeQuery(); //select���̱� ������ executeQuery()�� ����.
			
			//�� ���ڵ� ���� ���ߴٸ�
			if(rs.next()) {
				x = rs.getInt(1); //count(*)�� �ʵ尡 �ƴϱ� ������ �ʵ������ �ҷ��� ���� ����. (�׷��Լ��� ������̱� ������)
			}
		}catch(Exception e) {
			System.out.println("getArticleCount()�޼��� ȣ�� ���� =>" + e);
		}
		finally{//3. �޸� ����, ����
			pool.freeConnection(con, pstmt, rs); //con.close(), pstmt.close(), rs.close()
		}
		return x; //��ȯ���� �����ϱ� ����!
	}
	
	
	
	
	//2. �� ��� ���⿡ ���� �޼��� ���� -> ������ ���� / zipcode�� ������ �����غ� ����
	                         //���ڵ� ���۹�ȣ, �ҷ��� ���ڵ��� ����
	public List getArticles (int start, int end){//���ڵ尡 �������̱� ������
			//1. DB���� ����
			List articleList = null; // = ArrayList articleList = null; 
			
			//2. SQL���� - ������ �߻��� �� �ذ��ϱ� ����.
			try {
				con = pool.getConnection(); //con�� pool��ü���� ���´�. (10�� �߿��� �� ���� �����ش�) -> default���� 10�̴�.
				                                      // -> Ŀ�ؼ� Ǯ (�ʿ��� ������ ��ü(connection)�� �����ְ�, �ʿ� ������ �ݳ��Ѵ�.)
				                                     //�̸� ���� �����ش�. (�ణ ������ũ�� �������� ���� ����...?)
				System.out.println("con=>" + con);
				sql = "select * from board order by ref desc, re_step asc limit ?,?"; //ref -> �׷��ȣ, desc->�������� / limit ?,? -> ?���� ?���� �ش��ϴ� �͸� �����޶�~
				pstmt = con.prepareStatement(sql);//sql�������
				pstmt.setInt(1, start-1); //sql���忡 ?�� ������ ���� �־�����  / MySQL�� ���ڵ������ ���������� 0���� �����ϱ� ������ -1�� ���ش�.
				pstmt.setInt(2, end);
				rs = pstmt.executeQuery(); //select���̱� ������ executeQuery()�� ����.
				
				//�� ���ڵ� ���� ���ߴٸ�
				if(rs.next()) { //���ڵ尡 �Ѱ��� �����Ѵٸ�
					//articleList = new List(); �������̽��� �ڱ� �ڽ��� ��ü�� ������ �� ����. 
					articleList = new ArrayList(end); //ȭ�鿡 ������ �Խñ��� end���� ��ŭ.(end������ŭ �����͸� ���� ���� ����)
					do { //ã�� ���ڵ��� �ʵ庰�� ����
						/*
						BoardDTO article = new BoardDTO();
						article.setNum(rs.getInt("num"));//�Խù� ��ȣ�� �ҷ��´�. 
						article.setWriter(rs.getString("writer"));
						article.setEmail(rs.getString("email"));
						article.setSubject(rs.getString("subject"));
						article.setPasswd(rs.getString("passwd"));
						article.setContent(rs.getString("content"));
						article.setIp(rs.getString("ip"));
						
						article.setRegdate(rs.getTimestamp("reg_date"));//�ۼ���¥ (���ó�¥)
						article.setReadcount(rs.getInt("readcount")); //��ȸ��
						
						//-----------------���----------------
						article.setRef(rs.getInt("ref")); //�׷��ȣ
						article.setRe_step(rs.getInt("re_step")); //�亯�� ����
						article.setRe_level(rs.getInt("re_level")); //�亯�� ���� 
						//-------------------------------------
						
						article.setContent(rs.getString("content"));
						article.setIp(rs.getNString("ip"));
						*/
						BoardDTO article = makeArticleFromResult();
						
						//�߰��ϱ�
						articleList.add(article); //���� ���ڵ���� articleList�� �߰�����. 
					}while(rs.next());
				}
			}catch(Exception e) {
				System.out.println("getArticles()�޼��� ȣ�� ���� =>" + e);
			}
			finally{//3. �޸� ����, ����
				pool.freeConnection(con, pstmt, rs); //con.close(), pstmt.close(), rs.close()
			}
			return articleList; //list.jsp���� articleList�� for���� ���� ����.
	}
	
	
	
	//�۾���
	//insert into board values()
	public void insertArticle(BoardDTO article) { //�űԱ����� �亯������ �����ִ� ����� ����.
		//�޾ƾ��ϴ� �Ű������� ���� ������ BoardDTO�� article�̶�� �̸����� �޾ƿ´�.
		//writePro.jsp���� ȣ���ϴµ� �űԱ����� �亯������ ��� Ȯ���� �ұ�? => article�� ������ Ȯ���Ѵ�.
		int num = article.getNum();//�Խñ۹�ȣ ---> �űԱ��̳� �亯���̳� ������ ����
		int ref = article.getRef();
		int re_step = article.getRe_step();
		int re_level = article.getRe_level();
		//���̺� �Է��� �Խù� ��ȣ�� ������ ����
		int number = 0; //--> �����͸� �ֱ� ���� �ʿ�
		System.out.println("insertArticle�޼����� ������ num=>" + num);
		System.out.println("ref=>"+ ref + ", re_step=>" + re_step + ", re_level=>" + re_level);
		
	//�Խù� ��ȣ + 1 -> �������� �Խñ��� ���� ��ȣ�� +1�� �Ǵ� ��.
		try {
			con = pool.getConnection(); //�׻� DB�� ���� �����Ѵ�.
			//SQL����
			sql = "select max(num) from board"; //board���� ���� ū num�� ���ؿͶ� 
			pstmt = con.prepareStatement(sql); //pstmt�� ���;� sql������ ������ �� �ִ�.
			//select�̱� ������ result���� ���´�., executeQuery()
			//insert���ٸ� executeUpdate
			rs = pstmt.executeQuery();
			
			if(rs.next()) { //���� ���̺��� �����Ͱ� �Ѱ��� �����ϸ�
				number = rs.getInt(1)+1; //1-->�ִ밪.  +1 --> ������ ���� ���� ���ϱ� ���� +�� ��
			}else {
				number = 1; //�Խñ��� �ϳ��� ���� ���¶�� ���� ���� ���� 1�� �Ǿ�� �Ѵ�. 
			}
			
			
			//���࿡ �亯���̶��
			if(num != 0) {
				//���߿��
				//���� �� �ڸ��� �����. set re_step + 1
				sql = "update board set re_step=re_step+1 where ref=? and re_step > ?"; //������ �ִ� �����͵��� �� �ڷ� �и��� �ؾ� �Ѵ�.
						//���� �׷� ��ȣ�̸鼭 ������ step���� ū �Խù��� ã�Ƽ� �� step���� 1 �������Ѷ�
				pstmt = con.prepareStatement(sql); //sql���� ������ ���̴�~
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_step); //���� ������� �ִ´�.
				int update = pstmt.executeUpdate();//sql���� �����غ���.
				System.out.println("��ۼ�������(update)=>" + update);

				re_step = re_step+1; //����� �� ������ �������� �ϳ��� �����Ѵ�.
				re_level = re_level+1;
			}else { //�űԱ��̶��
				ref=number; //ref=1,2,3,.... (�׷��ȣ���� �Խñ� ������ �Ѵ�.)-> ref�� �Խù� �����ڷ� ��� ����
				re_step=0;
				re_level=0;
			}
			//12�� �����͸� ������ �ȴ�. �ٸ� �� �߿��� num, reg_date, readcount�� �Է� ���� �ʴ´�.
			//mysql�� ��¥�� �ۼ��� �� (now())�� ����. / SQL�� sysdate
			sql = "insert into board(writer,email,subject,passwd,reg_date,ref, re_step, re_level, content,ip)values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			//article�� ������ �����͸� �״�� �ִ°�.
			//article�� ���� ���� �׶� �׶� ����ؼ� �ֱ� ������ ���� �ٲ��.
			pstmt.setString(1, article.getWriter()); //�ۼ���
			pstmt.setString(2, article.getEmail());
			pstmt.setString(3, article.getSubject());
			pstmt.setString(4, article.getPasswd());
			pstmt.setTimestamp(5, article.getRegdate());
			//article�� ������ �� �״�� ����� �� ���.
			//�̷��� ���� �����Ÿ� values(?,?...)�� �κп��� 5��°��(?,?,?,?,now(),?,?....)�� �� �� �ִ�.
			 
			//------�ڡ�ref, re_step, re_level�ڡ�
			pstmt.setInt(6, ref); //article.getRef()�� �ƴ� ref��� �� ����!  => �߰��� �ű�, �亯�ۿ� ���� ���� ����Ǳ� ����
			pstmt.setInt(7, re_step);
			pstmt.setInt(8, re_level);
			//------------------------------------------
			
			pstmt.setString(9,article.getContent()); //�� ���� �״�� �ҷ��´�.
			pstmt.setString(10,article.getIp()); //request.getRemoteAddr(); --> Ŭ���̾�Ʈ�� ip�� ������ ���
			
			int insert = pstmt.executeUpdate(); //sql���� insert�̱� ������ executeUpdate()!
			System.out.println("�Խ����� �۾��� ��������(insert)=>" + insert);
			
		}catch(Exception e) {
			System.out.println("insertArticle()�޼��� ���� ����=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
	}
	
	//�� �󼼺��� ������ �ϴ� ����~
	//1. �Խù��� �ش�Ǵ� ��ȸ���� ������ų �� �ִ�.
	//2. ������ ��ȸ���� ���� ���ڵ带 �˻��ؼ� ȭ�鿡 ����� �� �ִ�.
	
	//�޼��带 ������ -> sql���� ���� �Ẹ��!
	//select * from board where num='ã�����ϴ� �Խñ۹�ȣ';
	//(ȸ������) select * from member where id = 'ã�����ϴ� ��� id';
	//���� ȣ���ϸ� �� public
	public BoardDTO getArticle(int num){ 
		BoardDTO article = null;//��ȯ���� �ִ�.(�Խù���ȣ�� �ش��ϴ� ���ڵ� �Ѱ��� ���� ���� �ʿ�)
		try {
			con = pool.getConnection(); //connection��ü�� ���� ���´�.
			//sql����
			sql = "update board set readcount = readcount + 1 where num=?"; 	//���� ������ �� ���� ��ȸ���� �������Ѷ�

			pstmt = con.prepareStatement(sql); //SQL���� ������ ���� �ʿ�.
			pstmt.setInt(1,num);//?�� ���� �־��ش�.
			
			//������ �ϴ� ���̱� ������ ��ȯ���� �ش�.
			int update = pstmt.executeUpdate();
			System.out.println("��ȸ�� ���� ����(update) =>" + update);
			
			//2��° sql����
			sql = "select * from board where num=?"; //�Խù��� �ش�Ǵ� ���ڵ� ��ü�� ���Ͷ�
			pstmt = con.prepareStatement(sql); //sql������ �����ϱ� ���� �ʿ�
			//?�� �����Ƿ� ���� �־��ش�.
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();//select�� ��������Ƿ� �ʿ��ϴ�, select�̱� ������ executeQuery
			
			//if�� ���ڵ尡 �� �� ���� �� ���
			if(rs.next()) {
				//---------------article�� ���ڵ带 ��´�.---------------
				article = makeArticleFromResult();
				/*
				article = new BoardDTO(); //��ü ����
				
				article.setNum(rs.getInt("num"));//�Խù� ��ȣ�� �ҷ��´�. 
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				
				article.setRegdate(rs.getTimestamp("reg_date"));//�ۼ���¥ (���ó�¥)
				article.setReadcount(rs.getInt("readcount")); //��ȸ��
				
				//-----------------���----------------
				article.setRef(rs.getInt("ref")); //�׷��ȣ
				article.setRe_step(rs.getInt("re_step")); //�亯�� ����
				article.setRe_level(rs.getInt("re_level")); //�亯�� ���� 
				//-------------------------------------
				
				article.setContent(rs.getString("content"));
				article.setIp(rs.getNString("ip"));
				*/
			}
		}catch(Exception e) {
			System.out.println("getArticle()ȣ�� ����=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return article;//��ȯ��
	}
	
	
	//------------------�� ����-------------------
	//1. �� �����ϱ� ���� �����͸� ���� ����� ������ �޼���
	public BoardDTO updateGetArticle(int num) {
		BoardDTO article = null;//��ȯ���� �ִ�.(�Խù���ȣ�� �ش��ϴ� ���ڵ� �Ѱ��� ���� ���� �ʿ�)
		try {
			con = pool.getConnection(); //connection��ü�� ���� ���´�.
			
			sql = "select * from board where num=?"; //�Խù��� �ش�Ǵ� ���ڵ� ��ü�� ���Ͷ�
			pstmt = con.prepareStatement(sql); //sql������ �����ϱ� ���� �ʿ�
			//?�� �����Ƿ� ���� �־��ش�.
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();//select�� ��������Ƿ� �ʿ��ϴ�, select�̱� ������ executeQuery
			
			//if�� ���ڵ尡 �� �� ���� �� ���
			if(rs.next()) {
				article = makeArticleFromResult();//�Ϲ�Ŭ����->�Ϲ�Ŭ���� ȣ���� ���� �޼����();
				//---------------article�� ���ڵ带 ��´�.---------------
				/*
				article = new BoardDTO(); //��ü ����
				
				article.setNum(rs.getInt("num"));//�Խù� ��ȣ�� �ҷ��´�. 
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				
				article.setRegdate(rs.getTimestamp("reg_date"));//�ۼ���¥ (���ó�¥)
				article.setReadcount(rs.getInt("readcount")); //��ȸ��
				
				//-----------------���----------------
				article.setRef(rs.getInt("ref")); //�׷��ȣ
				article.setRe_step(rs.getInt("re_step")); //�亯�� ����
				article.setRe_level(rs.getInt("re_level")); //�亯�� ���� 
				//-------------------------------------
				
				article.setContent(rs.getString("content"));
				article.setIp(rs.getNString("ip"));
				*/
			}
		}catch(Exception e) {
			System.out.println("getArticle()ȣ�� ����=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return article;//��ȯ��
	}
	
	//�ߺ��� ���ڵ� �Ѱ��� ���� �� �ִ� �޼��带 ���� �ۼ��غ���.
	//���ο����� �ҷ��� ����ؾ� �ϱ� ������ private!!
	//private�̱� ������ ��� ��Ƽ� ������ �� �ִ� ���� �ʿ��ϴ�.
	private BoardDTO makeArticleFromResult() throws Exception{ //����ó�� �ʿ�
		BoardDTO article = new BoardDTO(); //��ü ����
		
		article.setNum(rs.getInt("num"));//�Խù� ��ȣ�� �ҷ��´�. 
		article.setWriter(rs.getString("writer"));
		article.setEmail(rs.getString("email"));
		article.setSubject(rs.getString("subject"));
		article.setPasswd(rs.getString("passwd"));
		article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));
		
		article.setRegdate(rs.getTimestamp("reg_date"));//�ۼ���¥ (���ó�¥)
		article.setReadcount(rs.getInt("readcount")); //��ȸ��
		
		//-----------------���----------------
		article.setRef(rs.getInt("ref")); //�׷��ȣ
		article.setRe_step(rs.getInt("re_step")); //�亯�� ����
		article.setRe_level(rs.getInt("re_level")); //�亯�� ���� 
		//-------------------------------------
		
		article.setContent(rs.getString("content"));
		article.setIp(rs.getNString("ip"));
		
		return article;
	}
	
	
	
	
	//2. �� �������ִ� �޼��� �ʿ�(insertArticle�޼���� ���� ���)
	public int updateArticle(BoardDTO article){//�ڼ��� �����ߴ�vs���ߴ١� -> boolean������ int�� �غ� 
		//�����ϴ� ������, �������� �ʴ� ������ ��� ������ ��
		//��ü�� �޾ƿ�, ��ȯ�� ����
		
		String dbpasswd = null; //DB���� ã�� ��ȣ�� ������ ���� (��ȣ�� ������ ����, Ʋ���� ����X)
		int x = -1; //�Խù��� �������� ����
		
		
	//�Խù� ��ȣ + 1 -> �������� �Խñ��� ���� ��ȣ�� +1�� �Ǵ� ��.
			try {
				con = pool.getConnection(); //�׻� DB�� ���� �����Ѵ�.
				//SQL����
				sql = "select passwd from board where num=?"; //�Խñۿ� �ش��ϴ� passwd�� ������ ��
				pstmt = con.prepareStatement(sql); //pstmt�� ���;� sql������ ������ �� �ִ�.
				//?�� �����Ƿ� 
				pstmt.setInt(1, article.getNum()); //article���� ������ �Խñ� ��ȣ�� ������ ��.
				//select�̱� ������ result���� ���´�., executeQuery()
				//insert���ٸ� executeUpdate
				rs = pstmt.executeQuery();
				
				if(rs.next()) { //���� ���̺��� �Է��� ��ȣ�� ã�Ҵٸ�
					dbpasswd = rs.getString("passwd");
					//��ȣ Ȯ��
					System.out.println("dbpasswd=>" + dbpasswd);
				
				
				//db���� ��ȣ�� ���� �Է��� ��ȣ�� ���ٸ�
				if(dbpasswd.contentEquals(article.getPasswd())) {
					
				//�����ϴ� sql����
				sql = "update board set writer=?,email=?,subject=?,passwd=?,";
				sql+="content=? where num=?";
				//������ �� �ִ� �͵��� ��� ?�� ���δ�. �׸��� �� �ٲ�� ���� �ƴ϶� �Խñ� ��ȣ�� �ش�Ǵ� �͸� �ٲ���ϹǷ� where!
				
				pstmt = con.prepareStatement(sql);
				//article�� ������ �����͸� �״�� �ִ°�.
				//article�� ���� ���� �׶� �׶� ����ؼ� �ֱ� ������ ���� �ٲ��.
				pstmt.setString(1, article.getWriter()); //�ۼ���
				pstmt.setString(2, article.getEmail());
				pstmt.setString(3, article.getSubject());
				pstmt.setString(4, article.getPasswd());
				pstmt.setString(5,article.getContent()); //�� ���� �״�� �ҷ��´�.
				pstmt.setInt(6, article.getNum());

				//pstmt.setTimestamp(5, article.getRegdate()); -> �������� ����.
				//article�� ������ �� �״�� ����� �� ���.
				//�̷��� ���� �����Ÿ� values(?,?...)�� �κп��� 5��°��(?,?,?,?,now(),?,?....)�� �� �� �ִ�.
				
				
				int update = pstmt.executeUpdate(); //sql���� insert�̱� ������ executeUpdate()!
				System.out.println("�Խ����� �� ���� ��������(update)=>" + update);
				
				x=1; //����
				}else {
					x=0; //��������
				}
				}//--if(rs.next())�� ��ȣ
			}catch(Exception e) {
				System.out.println("insertArticle()�޼��� ���� ����=>" + e);
			}finally {
				pool.freeConnection(con, pstmt, rs);
			}
			return x;//x�� ���� ��ȯ
	}
	
	
	
	
//�� ���� �����ִ� �޼��� => ȸ��Ż�� �޼���� ����(���� �� ��ȣ�� �����!)
	public int deleteArticle(int num, String passwd){
		
		String dbpasswd = null; //DB���� ã�� ��ȣ�� ������ ���� (��ȣ�� ������ ����, Ʋ���� ����X)
		int x = -1; //�Խù��� ���� ���� ����
		
		
	//�Խù� ��ȣ + 1 -> �������� �Խñ��� ���� ��ȣ�� +1�� �Ǵ� ��.
			try {
				con = pool.getConnection(); //�׻� DB�� ���� �����Ѵ�.
				//SQL����
				sql = "select passwd from board where num=?"; //�Խñ�(num)�� �ش��ϴ� passwd�� ������ ��
				pstmt = con.prepareStatement(sql); //pstmt�� ���;� sql������ ������ �� �ִ�. -> �̰� �ۼ����� ������ NullPointerException ������ ����.
				//?�� �����Ƿ� 
				pstmt.setInt(1, num);
				//select�̱� ������ result���� ���´�., executeQuery()
				//insert, update, delete��� executeUpdate
				rs = pstmt.executeQuery();
				
				if(rs.next()) { //���� ���̺��� �Է��� ��ȣ�� ã�Ҵٸ�
					dbpasswd = rs.getString("passwd");
					//��ȣ Ȯ��
					System.out.println("dbpasswd=>" + dbpasswd);
				
				
				//db���� ��ȣ�� ���� �Է��� ��ȣ�� ���ٸ�
				if(dbpasswd.contentEquals(passwd)) {
					
				//�����ϴ� sql����
				sql = "delete from board where num=?";
				//������ �Ѵ�. �����Ѵٰ� �� �Խñ� ��ȣ��.
				//sql���� ���� = pstmt����
				pstmt = con.prepareStatement(sql);
				
				//?�� �����Ƿ� set �ʿ�.
				pstmt.setInt(1, num); //�Խñ� ��ȣ

				int delete = pstmt.executeUpdate(); //sql���� insert�̱� ������ executeUpdate()!
				System.out.println("�Խ����� �� ���� ��������(delete)=>" + delete);
				
				x=1; //���� ����    --> ��ȯ�� ����!!   (boolean���� �ߴٸ� check = true;)
				
				}else { //��ȣ�� Ʋ�ȴٸ�
					x=0; //���� ����      (boolean���� �ߴٸ� check=false;)
				}
			}//--if(rs.next())�� ��ȣ
		}catch(Exception e) {
			System.out.println("deleteArticle()�޼��� ���� ����=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs); //�޸� ����
		}
		return x;//x�� ���� ��ȯ
	}


//실시간 검색어 -> 회원id 대신에 writer(게시판의 작성자)
//sql = select writer from board where writer like '%id%';  ==> 몇 개 나올지 모른다. 
	public List<String> getArticleId(String name){
		List<String> nameList = new ArrayList(); //검색된 데이터를 담을 변수
		try {
			con= pool.getConnection();
			sql="select writer from board where writer like '%"+name+"%'";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()) { //찾은 레코드가 있으면 넣어라~(있을동안 넣어라)
				String writer=rs.getString("writer");
				nameList.add(writer); //값이 있을 때마다 writer에 넣어야 함. List니까 add
			}
;		}catch(Exception e) {
			System.out.println("getArticleId()메서드 에러유발=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return nameList; //검색된 항목들 -> autoid.jsp가 받아서 autoid.html에게 전송
	}
}