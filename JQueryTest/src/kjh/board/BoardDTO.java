package kjh.board; //기능별로 분리

import java.sql.Timestamp;

//게시판
public class BoardDTO {
	//-------------자유게시판--------------
	private int num; //게시물 번호
	private String writer; //작성자
	private String subject; //글 제목
	private String email; //이메일
	private String content; //글 내용
	private String passwd; //암호 -> 글쓰기할 때 필요(본인만 수정, 삭제 가능)
	private Timestamp regdate; //작성 날짜
	private int readcount; //조회수 -> 글을 쓰면 처음에는 자동으로 0이 되어야 함
	private String ip; //작성자의 ip주소
	//---------------------------------------
	
	//-----------------댓글기능---------------
	private int ref; //그룹번호
	private int re_step; //답변글의 순서
	private int re_level; //답변변글의 답변에 대한 depth(트리구조 / 간격)
	//----------------------------------------
	
	//파일첨부를 하고 싶다면
	//private String file; //파일의 정보
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Timestamp getRegdate() {
		return regdate;
	}
	public void setRegdate(Timestamp regdate) {
		this.regdate = regdate;
	}
	public int getReadcount() {
		return readcount;
	}
	public void setReadcount(int readcount) {
		this.readcount = readcount;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getRef() {
		return ref;
	}
	public void setRef(int ref) {
		this.ref = ref;
	}
	public int getRe_step() {
		return re_step;
	}
	public void setRe_step(int re_step) {
		this.re_step = re_step;
	}
	public int getRe_level() {
		return re_level;
	}
	public void setRe_level(int re_level) {
		this.re_level = re_level;
	}
}
