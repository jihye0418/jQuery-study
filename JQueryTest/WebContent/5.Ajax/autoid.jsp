<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="kjh.board.*,java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>매개변수를 전달</title>
</head>
<body>
<%
   request.setCharacterEncoding("utf-8");//이름검색이라면 
   String userid=request.getParameter("userid");
   System.out.println("autoid.jsp의 userid=>"+userid);
   //DB연동 확인결과
   BoardDAO dbPro=new BoardDAO();
   List<String> name=dbPro.getArticleId(userid);
   //검색된 갯수만큼 li태그에 담아서 전송
   for(int i=0;i<name.size();i++){
	   String sname=name.get(i);//0~
	   out.println("<li>"+sname+"</li>");
   }

   /* (1)테스트용
   out.println("<li>testkim</li>");
   out.println("<li>test</li>");
   out.println("<li>test2</li>");
   out.println("<li>test3</li>");
	//=> 콜백함수가 매개변수로 받아서 화면에 뿌려줌.
*/
%>

</body>
</html>






