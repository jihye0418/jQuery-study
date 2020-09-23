package kjh.board; //��ɺ��� �и�

import java.sql.Timestamp;

//�Խ���
public class BoardDTO {
	//-------------�����Խ���--------------
	private int num; //�Խù� ��ȣ
	private String writer; //�ۼ���
	private String subject; //�� ����
	private String email; //�̸���
	private String content; //�� ����
	private String passwd; //��ȣ -> �۾����� �� �ʿ�(���θ� ����, ���� ����)
	private Timestamp regdate; //�ۼ� ��¥
	private int readcount; //��ȸ�� -> ���� ���� ó������ �ڵ����� 0�� �Ǿ�� ��
	private String ip; //�ۼ����� ip�ּ�
	//---------------------------------------
	
	//-----------------��۱��---------------
	private int ref; //�׷��ȣ
	private int re_step; //�亯���� ����
	private int re_level; //�亯������ �亯�� ���� depth(Ʈ������ / ����)
	//----------------------------------------
	
	//����÷�θ� �ϰ� �ʹٸ�
	//private String file; //������ ����
	
	
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
