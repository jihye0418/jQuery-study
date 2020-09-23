<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="lys.board.*,java.util.List" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>userid를 받아서 처리</title>
</head>
<body>
<%
    request.setCharacterEncoding("utf-8");//이름검색
    String userid=request.getParameter("userid");
    System.out.println("userid=>"+userid);
    
    BoardDAO dbPro=new BoardDAO();
    List<String> name=dbPro.getArticleId(userid);
    //검색된 갯수만큼 li태그에 담아서 전송
    for(int i=0;i<name.size();i++){
    	String sname=name.get(i);//0~
    	out.println("<li>"+sname+"</li>");
    }
    /*
      out.println("<li>testkim</li>");
      out.println("<li>test</li>");
      out.println("<li>test2</li>");
      out.println("<li>test3</li>");
    */

%>
</body>
</html>



