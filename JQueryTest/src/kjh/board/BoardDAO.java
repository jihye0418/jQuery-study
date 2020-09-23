package kjh.board;

import java.sql.*;
import java.util.*; //ArrayList, List를 사용

//DB에 접속해서 웹상에서 호출할 메서드를 작성하는 클래스.
public class BoardDAO {
	private DBConnectionMgr pool = null; //1. 가져올 객체를 멤버변수로 선언한다.
	Connection con = null; //접속객체
	PreparedStatement pstmt = null; //sql문을 넣을 곳
	ResultSet rs = null; // 초기값 (select값을 테이블 형태로 받기 위해 필요)
	String sql = ""; //sql구문을 저장할 변수
	
	public BoardDAO(){//2. 생성자 - 초기값 설정
		//DB를 연결하지 못했다면
		try {
			pool = DBConnectionMgr.getInstance(); //정적메서드는 클래스명.정적메서드명(); 으로 호출
			System.out.println("pool=>" + pool);
		}catch(Exception e) {
			System.out.println("DB연결 실패(pool을 얻어오지 못함)=>" + e);
		}
	}
	
	//1. 총 레코드수를 구해주는 메서드 필요
		//메서드 만드는 규칙 -> 제일 먼저 SQL문을 적어본다.
		//형식) select count(*) from board;
	//웹상에서의 접근지정자는 거의 다 public, select는 반환형이 있다., where조건이 없으면 매개변수 없다. 
	public int getArticleCount() {
		//1. DB접속 구문
		int x = 0;
		//2. SQL구문 - 오류가 발생할 때 해결하기 위함.
		try {
			con = pool.getConnection(); //con은 pool객체에서 얻어온다. (10개 중에서 한 개를 빌려준다) -> default값이 10이다.
			                                      // -> 커넥션 풀 (필요할 때마다 객체(connection)를 빌려주고, 필요 없으면 반납한다.)
			                                     //미리 만들어서 빌려준다. (약간 워터파크의 구명조끼 같은 느낌...?)
			System.out.println("con=>" + con);
			sql = "select count(*) from board"; //count(*)는 필드가 아니다. 
			pstmt = con.prepareStatement(sql);//sql문장실행
			rs = pstmt.executeQuery(); //select문이기 때문에 executeQuery()를 쓴다.
			
			//총 레코드 수를 구했다면
			if(rs.next()) {
				x = rs.getInt(1); //count(*)는 필드가 아니기 때문에 필드명으로 불러올 수가 없다. (그룹함수의 결과물이기 때문에)
			}
		}catch(Exception e) {
			System.out.println("getArticleCount()메서드 호출 에러 =>" + e);
		}
		finally{//3. 메모리 해제, 종료
			pool.freeConnection(con, pstmt, rs); //con.close(), pstmt.close(), rs.close()
		}
		return x; //반환값이 있으니까 리턴!
	}
	
	
	
	
	//2. 글 목록 보기에 대한 메서드 구현 -> 범위를 지정 / zipcode를 가지고 구현해볼 것임
	                         //레코드 시작번호, 불러올 레코드의 개수
	public List getArticles (int start, int end){//레코드가 여러개이기 때문에
			//1. DB접속 구문
			List articleList = null; // = ArrayList articleList = null; 
			
			//2. SQL구문 - 오류가 발생할 때 해결하기 위함.
			try {
				con = pool.getConnection(); //con은 pool객체에서 얻어온다. (10개 중에서 한 개를 빌려준다) -> default값이 10이다.
				                                      // -> 커넥션 풀 (필요할 때마다 객체(connection)를 빌려주고, 필요 없으면 반납한다.)
				                                     //미리 만들어서 빌려준다. (약간 워터파크의 구명조끼 같은 느낌...?)
				System.out.println("con=>" + con);
				sql = "select * from board order by ref desc, re_step asc limit ?,?"; //ref -> 그룹번호, desc->내림차순 / limit ?,? -> ?부터 ?까지 해당하는 것만 보여달라~
				pstmt = con.prepareStatement(sql);//sql문장실행
				pstmt.setInt(1, start-1); //sql문장에 ?가 있으니 값을 넣어주자  / MySQL은 레코드순번이 내부적으로 0부터 시작하기 때문에 -1을 빼준다.
				pstmt.setInt(2, end);
				rs = pstmt.executeQuery(); //select문이기 때문에 executeQuery()를 쓴다.
				
				//총 레코드 수를 구했다면
				if(rs.next()) { //레코드가 한개라도 존재한다면
					//articleList = new List(); 인터페이스는 자기 자신은 객체를 생성할 수 없다. 
					articleList = new ArrayList(end); //화면에 보여질 게시글은 end개수 만큼.(end개수만큼 데이터를 담을 공간 생성)
					do { //찾은 레코드의 필드별로 저장
						/*
						BoardDTO article = new BoardDTO();
						article.setNum(rs.getInt("num"));//게시물 번호를 불러온다. 
						article.setWriter(rs.getString("writer"));
						article.setEmail(rs.getString("email"));
						article.setSubject(rs.getString("subject"));
						article.setPasswd(rs.getString("passwd"));
						article.setContent(rs.getString("content"));
						article.setIp(rs.getString("ip"));
						
						article.setRegdate(rs.getTimestamp("reg_date"));//작성날짜 (오늘날짜)
						article.setReadcount(rs.getInt("readcount")); //조회수
						
						//-----------------댓글----------------
						article.setRef(rs.getInt("ref")); //그룹번호
						article.setRe_step(rs.getInt("re_step")); //답변글 순서
						article.setRe_level(rs.getInt("re_level")); //답변의 깊이 
						//-------------------------------------
						
						article.setContent(rs.getString("content"));
						article.setIp(rs.getNString("ip"));
						*/
						BoardDTO article = makeArticleFromResult();
						
						//추가하기
						articleList.add(article); //위의 레코드들을 articleList에 추가해줌. 
					}while(rs.next());
				}
			}catch(Exception e) {
				System.out.println("getArticles()메서드 호출 에러 =>" + e);
			}
			finally{//3. 메모리 해제, 종료
				pool.freeConnection(con, pstmt, rs); //con.close(), pstmt.close(), rs.close()
			}
			return articleList; //list.jsp에서 articleList를 for문을 만들 것임.
	}
	
	
	
	//글쓰기
	//insert into board values()
	public void insertArticle(BoardDTO article) { //신규글인지 답변글인지 정해주는 기능을 가짐.
		//받아야하는 매개변수가 많기 때문에 BoardDTO를 article이라는 이름으로 받아온다.
		//writePro.jsp에서 호출하는데 신규글인지 답변글인지 어떻게 확인을 할까? => article을 가지고 확인한다.
		int num = article.getNum();//게시글번호 ---> 신규글이냐 답변글이냐 구분을 위해
		int ref = article.getRef();
		int re_step = article.getRe_step();
		int re_level = article.getRe_level();
		//테이블에 입력할 게시물 번호를 저장할 변수
		int number = 0; //--> 데이터를 넣기 위해 필요
		System.out.println("insertArticle메서드의 내부의 num=>" + num);
		System.out.println("ref=>"+ ref + ", re_step=>" + re_step + ", re_level=>" + re_level);
		
	//게시물 번호 + 1 -> 다음번에 게시글을 쓰면 번호가 +1이 되는 것.
		try {
			con = pool.getConnection(); //항상 DB를 먼저 연결한다.
			//SQL문장
			sql = "select max(num) from board"; //board에서 가장 큰 num을 구해와라 
			pstmt = con.prepareStatement(sql); //pstmt를 얻어와야 sql문장을 실행할 수 있다.
			//select이기 때문에 result값을 얻어온다., executeQuery()
			//insert였다면 executeUpdate
			rs = pstmt.executeQuery();
			
			if(rs.next()) { //현재 테이블에서 데이터가 한개라도 존재하면
				number = rs.getInt(1)+1; //1-->최대값.  +1 --> 기존의 값에 값을 더하기 위해 +를 씀
			}else {
				number = 1; //게시글이 하나도 없는 상태라면 지금 들어가는 값이 1이 되어야 한다. 
			}
			
			
			//만약에 답변글이라면
			if(num != 0) {
				//★중요★
				//값이 들어갈 자리를 만든다. set re_step + 1
				sql = "update board set re_step=re_step+1 where ref=? and re_step > ?"; //기존에 있던 데이터들을 다 뒤로 밀리게 해야 한다.
						//같은 그룹 번호이면서 나보다 step값이 큰 게시물을 찾아서 그 step값을 1 증가시켜라
				pstmt = con.prepareStatement(sql); //sql문을 실행할 것이다~
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_step); //값을 순서대로 넣는다.
				int update = pstmt.executeUpdate();//sql문을 실행해본다.
				System.out.println("댓글수정유무(update)=>" + update);

				re_step = re_step+1; //답글을 달 때마다 기존값이 하나씩 증가한다.
				re_level = re_level+1;
			}else { //신규글이라면
				ref=number; //ref=1,2,3,.... (그룹번호지만 게시글 역할을 한다.)-> ref도 게시물 구분자로 사용 가능
				re_step=0;
				re_level=0;
			}
			//12개 데이터를 넣으면 된다. 다만 그 중에서 num, reg_date, readcount는 입력 받지 않는다.
			//mysql은 날짜를 작성할 때 (now())를 쓴다. / SQL은 sysdate
			sql = "insert into board(writer,email,subject,passwd,reg_date,ref, re_step, re_level, content,ip)values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			//article은 웹상의 데이터를 그대로 넣는것.
			//article이 없는 것은 그때 그때 계산해서 넣기 때문에 값이 바뀐다.
			pstmt.setString(1, article.getWriter()); //작성자
			pstmt.setString(2, article.getEmail());
			pstmt.setString(3, article.getSubject());
			pstmt.setString(4, article.getPasswd());
			pstmt.setTimestamp(5, article.getRegdate());
			//article은 저장한 값 그대로 출력할 때 사용.
			//이렇게 하지 않을거면 values(?,?...)쓴 부분에서 5번째에(?,?,?,?,now(),?,?....)로 쓸 수 있다.
			 
			//------★★ref, re_step, re_level★★
			pstmt.setInt(6, ref); //article.getRef()가 아닌 ref라는 것 주의!  => 중간에 신규, 답변글에 대한 값이 변경되기 때문
			pstmt.setInt(7, re_step);
			pstmt.setInt(8, re_level);
			//------------------------------------------
			
			pstmt.setString(9,article.getContent()); //글 내용 그대로 불러온다.
			pstmt.setString(10,article.getIp()); //request.getRemoteAddr(); --> 클라이언트의 ip를 얻어오는 방법
			
			int insert = pstmt.executeUpdate(); //sql문이 insert이기 때문에 executeUpdate()!
			System.out.println("게시판의 글쓰기 성공유무(insert)=>" + insert);
			
		}catch(Exception e) {
			System.out.println("insertArticle()메서드 에러 유발=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
	}
	
	//글 상세보기 구현을 하는 것은~
	//1. 게시물에 해당되는 조회수를 증가시킬 수 있다.
	//2. 증가된 조회수를 가진 레코드를 검색해서 화면에 출력할 수 있다.
	
	//메서드를 만들어보자 -> sql문장 먼저 써보기!
	//select * from board where num='찾고자하는 게시글번호';
	//(회원수정) select * from member where id = '찾고자하는 멤버 id';
	//웹상에 호출하면 다 public
	public BoardDTO getArticle(int num){ 
		BoardDTO article = null;//반환값이 있다.(게시물번호에 해당하는 레코드 한개를 담을 변수 필요)
		try {
			con = pool.getConnection(); //connection객체를 먼저 얻어온다.
			//sql문장
			sql = "update board set readcount = readcount + 1 where num=?"; 	//글을 누르면 그 글의 조회수를 증가시켜라

			pstmt = con.prepareStatement(sql); //SQL문장 실행을 위해 필요.
			pstmt.setInt(1,num);//?에 값을 넣어준다.
			
			//수정을 하는 것이기 때문에 반환값을 준다.
			int update = pstmt.executeUpdate();
			System.out.println("조회수 증가 유무(update) =>" + update);
			
			//2번째 sql문장
			sql = "select * from board where num=?"; //게시물에 해당되는 레코드 전체를 얻어와라
			pstmt = con.prepareStatement(sql); //sql문장을 실행하기 위해 필요
			//?가 있으므로 값을 넣어준다.
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();//select를 사용했으므로 필요하다, select이기 때문에 executeQuery
			
			//if는 레코드가 한 개 있을 때 사용
			if(rs.next()) {
				//---------------article에 레코드를 담는다.---------------
				article = makeArticleFromResult();
				/*
				article = new BoardDTO(); //객체 만듦
				
				article.setNum(rs.getInt("num"));//게시물 번호를 불러온다. 
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				
				article.setRegdate(rs.getTimestamp("reg_date"));//작성날짜 (오늘날짜)
				article.setReadcount(rs.getInt("readcount")); //조회수
				
				//-----------------댓글----------------
				article.setRef(rs.getInt("ref")); //그룹번호
				article.setRe_step(rs.getInt("re_step")); //답변글 순서
				article.setRe_level(rs.getInt("re_level")); //답변의 깊이 
				//-------------------------------------
				
				article.setContent(rs.getString("content"));
				article.setIp(rs.getNString("ip"));
				*/
			}
		}catch(Exception e) {
			System.out.println("getArticle()호출 에러=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return article;//반환값
	}
	
	
	//------------------글 수정-------------------
	//1. 글 수정하기 위한 데이터를 웹에 출력할 목적의 메서드
	public BoardDTO updateGetArticle(int num) {
		BoardDTO article = null;//반환값이 있다.(게시물번호에 해당하는 레코드 한개를 담을 변수 필요)
		try {
			con = pool.getConnection(); //connection객체를 먼저 얻어온다.
			
			sql = "select * from board where num=?"; //게시물에 해당되는 레코드 전체를 얻어와라
			pstmt = con.prepareStatement(sql); //sql문장을 실행하기 위해 필요
			//?가 있으므로 값을 넣어준다.
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();//select를 사용했으므로 필요하다, select이기 때문에 executeQuery
			
			//if는 레코드가 한 개 있을 때 사용
			if(rs.next()) {
				article = makeArticleFromResult();//일반클래스->일반클래스 호출할 때는 메서드명();
				//---------------article에 레코드를 담는다.---------------
				/*
				article = new BoardDTO(); //객체 만듦
				
				article.setNum(rs.getInt("num"));//게시물 번호를 불러온다. 
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				
				article.setRegdate(rs.getTimestamp("reg_date"));//작성날짜 (오늘날짜)
				article.setReadcount(rs.getInt("readcount")); //조회수
				
				//-----------------댓글----------------
				article.setRef(rs.getInt("ref")); //그룹번호
				article.setRe_step(rs.getInt("re_step")); //답변글 순서
				article.setRe_level(rs.getInt("re_level")); //답변의 깊이 
				//-------------------------------------
				
				article.setContent(rs.getString("content"));
				article.setIp(rs.getNString("ip"));
				*/
			}
		}catch(Exception e) {
			System.out.println("getArticle()호출 에러=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return article;//반환값
	}
	
	//중복된 레코드 한개를 담을 수 있는 메서드를 따로 작성해보자.
	//내부에서만 불러서 사용해야 하기 때문에 private!!
	//private이기 때문에 대신 담아서 리턴할 수 있는 것이 필요하다.
	private BoardDTO makeArticleFromResult() throws Exception{ //예외처리 필요
		BoardDTO article = new BoardDTO(); //객체 만듦
		
		article.setNum(rs.getInt("num"));//게시물 번호를 불러온다. 
		article.setWriter(rs.getString("writer"));
		article.setEmail(rs.getString("email"));
		article.setSubject(rs.getString("subject"));
		article.setPasswd(rs.getString("passwd"));
		article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));
		
		article.setRegdate(rs.getTimestamp("reg_date"));//작성날짜 (오늘날짜)
		article.setReadcount(rs.getInt("readcount")); //조회수
		
		//-----------------댓글----------------
		article.setRef(rs.getInt("ref")); //그룹번호
		article.setRe_step(rs.getInt("re_step")); //답변글 순서
		article.setRe_level(rs.getInt("re_level")); //답변의 깊이 
		//-------------------------------------
		
		article.setContent(rs.getString("content"));
		article.setIp(rs.getNString("ip"));
		
		return article;
	}
	
	
	
	
	//2. 글 수정해주는 메서드 필요(insertArticle메서드와 거의 비슷)
	public int updateArticle(BoardDTO article){//★수정 성공했다vs못했다★ -> boolean이지만 int로 해봄 
		//수정하는 데이터, 수정하지 않는 데이터 모두 가지고 옴
		//전체를 받아옴, 반환값 있음
		
		String dbpasswd = null; //DB에서 찾은 암호를 저장할 변수 (암호가 맞으면 수정, 틀리면 수정X)
		int x = -1; //게시물의 수정성공 유무
		
		
	//게시물 번호 + 1 -> 다음번에 게시글을 쓰면 번호가 +1이 되는 것.
			try {
				con = pool.getConnection(); //항상 DB를 먼저 연결한다.
				//SQL문장
				sql = "select passwd from board where num=?"; //게시글에 해당하는 passwd를 가지고 옴
				pstmt = con.prepareStatement(sql); //pstmt를 얻어와야 sql문장을 실행할 수 있다.
				//?가 있으므로 
				pstmt.setInt(1, article.getNum()); //article에서 꺼내서 게시글 번호를 가지고 옴.
				//select이기 때문에 result값을 얻어온다., executeQuery()
				//insert였다면 executeUpdate
				rs = pstmt.executeQuery();
				
				if(rs.next()) { //현재 테이블에서 입력한 암호를 찾았다면
					dbpasswd = rs.getString("passwd");
					//암호 확인
					System.out.println("dbpasswd=>" + dbpasswd);
				
				
				//db상의 암호와 웹상에 입력한 암호가 같다면
				if(dbpasswd.contentEquals(article.getPasswd())) {
					
				//수정하는 sql문장
				sql = "update board set writer=?,email=?,subject=?,passwd=?,";
				sql+="content=? where num=?";
				//수정할 수 있는 것들을 골라 ?를 붙인다. 그리고 다 바뀌는 것이 아니라 게시글 번호에 해당되는 것만 바꿔야하므로 where!
				
				pstmt = con.prepareStatement(sql);
				//article은 웹상의 데이터를 그대로 넣는것.
				//article이 없는 것은 그때 그때 계산해서 넣기 때문에 값이 바뀐다.
				pstmt.setString(1, article.getWriter()); //작성자
				pstmt.setString(2, article.getEmail());
				pstmt.setString(3, article.getSubject());
				pstmt.setString(4, article.getPasswd());
				pstmt.setString(5,article.getContent()); //글 내용 그대로 불러온다.
				pstmt.setInt(6, article.getNum());

				//pstmt.setTimestamp(5, article.getRegdate()); -> 수정하지 않음.
				//article은 저장한 값 그대로 출력할 때 사용.
				//이렇게 하지 않을거면 values(?,?...)쓴 부분에서 5번째에(?,?,?,?,now(),?,?....)로 쓸 수 있다.
				
				
				int update = pstmt.executeUpdate(); //sql문이 insert이기 때문에 executeUpdate()!
				System.out.println("게시판의 글 수정 성공유무(update)=>" + update);
				
				x=1; //성공
				}else {
					x=0; //수정실패
				}
				}//--if(rs.next())의 괄호
			}catch(Exception e) {
				System.out.println("insertArticle()메서드 에러 유발=>" + e);
			}finally {
				pool.freeConnection(con, pstmt, rs);
			}
			return x;//x의 값을 반환
	}
	
	
	
	
//글 삭제 시켜주는 메서드 => 회원탈퇴 메서드와 동일(삭제 전 암호를 물어본다!)
	public int deleteArticle(int num, String passwd){
		
		String dbpasswd = null; //DB에서 찾은 암호를 저장할 변수 (암호가 맞으면 삭제, 틀리면 삭제X)
		int x = -1; //게시물의 삭제 성공 유무
		
		
	//게시물 번호 + 1 -> 다음번에 게시글을 쓰면 번호가 +1이 되는 것.
			try {
				con = pool.getConnection(); //항상 DB를 먼저 연결한다.
				//SQL문장
				sql = "select passwd from board where num=?"; //게시글(num)에 해당하는 passwd를 가지고 옴
				pstmt = con.prepareStatement(sql); //pstmt를 얻어와야 sql문장을 실행할 수 있다. -> 이걸 작성하지 않으면 NullPointerException 오류가 난다.
				//?가 있으므로 
				pstmt.setInt(1, num);
				//select이기 때문에 result값을 얻어온다., executeQuery()
				//insert, update, delete라면 executeUpdate
				rs = pstmt.executeQuery();
				
				if(rs.next()) { //현재 테이블에서 입력한 암호를 찾았다면
					dbpasswd = rs.getString("passwd");
					//암호 확인
					System.out.println("dbpasswd=>" + dbpasswd);
				
				
				//db상의 암호와 웹상에 입력한 암호가 같다면
				if(dbpasswd.contentEquals(passwd)) {
					
				//삭제하는 sql문장
				sql = "delete from board where num=?";
				//삭제를 한다. 삭제한다고 한 게시글 번호만.
				//sql구문 개수 = pstmt개수
				pstmt = con.prepareStatement(sql);
				
				//?가 있으므로 set 필요.
				pstmt.setInt(1, num); //게시글 번호

				int delete = pstmt.executeUpdate(); //sql문이 insert이기 때문에 executeUpdate()!
				System.out.println("게시판의 글 삭제 성공유무(delete)=>" + delete);
				
				x=1; //삭제 성공    --> 반환값 저장!!   (boolean으로 했다면 check = true;)
				
				}else { //암호가 틀렸다면
					x=0; //삭제 실패      (boolean으로 했다면 check=false;)
				}
			}//--if(rs.next())의 괄호
		}catch(Exception e) {
			System.out.println("deleteArticle()메서드 에러 유발=>" + e);
		}finally {
			pool.freeConnection(con, pstmt, rs); //메모리 해제
		}
		return x;//x의 값을 반환
	}
}