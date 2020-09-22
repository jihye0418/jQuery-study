/**
 * 회원가입 처리용 jQuery
 */
$(function(){
	//에러 메세지는 입력하지 않으면 나와야함. 처음에 화면에 보이지 않게 해야 한다.
	//show() <-> hide()
	$('#id_error').hide();
	$('#age_error1').hide();
	$('#age_error2').hide();
	$('#p_error1').hide();
	$('#p_error2').hide();
	
	
	$('#btnSend').click(function(){ //전송버튼을 눌렀을 때 할 일
		//1. id값 입력 체크 ->name이나 id 사용 (주로 id 사용)
		var id = $('#userid').val(); //input의 값을 가져와서 변수로 저장
		if(id.length<1){ //입력하지 않았다면
			$('#id_error').show();
			return false; // 전송 불가능하게 함
		}else{ //한글자라도 입력한 경우
			$('#id_error').hide();
		}
		
		//2.age 값 입력 체크
		var age = $('#age').val();
		if(age.length<1){
			$('#age_error1').show();
			return false;
		}else{
			$('#age_error1').hide();
		}
		
		//3. age 숫자 입력 체크 -> 0~9사이가 아니면 문자로 취급.
		//0 -> 아스키코드 : 48   / 9 -> 아스키코드 : 57
		for(var i = 0; i<age.length;i++){
			var data = age.charAt(i).charCodeAt(0); //문자열(String)을 문자로 가져오기(charAt)
			//charCodeAt(0) : 문자 하나하나를 아스키코드 값으로 변환시켜줌
			alert(data); // 아스키값인지 확인해보기
			if(data < 48 || data >57){
				$('#age_error2').show();
				$('#age').val(''); //입력한 값 지우기
				$('#age').focus();
				return false; //전송 불가능하게
				break; //더 이상 진행하지 않도록.(for문을 끝내기 위해 사용)
			}else{
				$('#age_error2').hide();
			}
		}
		
		//4. password 값 입력 체크
		var pwd1 = $('#pwd1').val();
		if(pwd1.length<1){ 
			$('#pwd1').next().show(); // = $('#p_error1').show();
			return false;
		}else{
			$('#pwd1').next().hide();// = $('#p_error1').hide();
		}
		
		//5. password 값 일치 확인
		var pwd2 = $('#pwd2').val();
		//-----입력했는지 안했는지------
		if(pwd2.length<1){ 
			$('#p_error2').show();
			return false;
		}else{
			$('#p_error2').hide();
		}
		//-----암호입력과 일치하는지 안하는지-----
		if(pwd1 != pwd2){
			$('#p_error2').show();
			$('#pwd2').val('');
			$('#pwd2').focus();
			return false;
		}
		
		//다 입력했다면 전송 (form에 action이 없으므로 속성 추가를 해준다.)
		$('#signup').attr('action','register.jsp').submit();
		return true; // 조건을 다 만족하면 return true -> 전송이 가능하게 설정
	});
});