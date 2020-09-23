<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.sql.*,kjh.board.*"%>
    
    <!-- DB상의 데이터를 JSON으로 가져와서 출력하기 -->
[
	<%
		Connection con = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null; //select구문(표형태)
		DBConnectionMgr pool = null; //DB연동
		String sql = "";
		
		
		try{
			pool=DBConnectionMgr.getInstance();
			con=pool.getConnection();
			System.out.println("con=" + con);
			sql="select num,writer,subject,content from board order by num asc"; //asc : 오름차순
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){ //다음 값이 있을 때까지 가져온다(다음 값이 있으면 가져온다)
				int num = rs.getInt("num");
				String writer = rs.getString("writer");
				String subject = rs.getString("subject");
				String content = rs.getString("content");
				//[{num:1,writer:'노답',~},{num:2,writer:'에휴',~}...]
				if(rs.getRow() > 1){ //행의 수를 얻어옴 = 테이블의 수를 가져옴 = select count(*) from ~
					out.print(",");		
				}%>
				{
					"num" : <%=num %>,<br>
					"writer" : <%=writer %>,<br>
					"subject" : <%=subject %>,<br>
					"content" : <%=content %><br>
				}
	<%
			}
		}catch(Exception e){
			System.out.println("getBoardJson.jsp에러유발=>" + e);
		}finally{
			pool.freeConnection(con, pstmt, rs);
		}
	%>
]