package com.itwillbs.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwillbs.domain.BoardBean;
import com.itwillbs.domain.MemberBean;
import com.itwillbs.domain.OneRoomBean;
import com.itwillbs.domain.PageBean;
import com.itwillbs.service.BoardService;
import com.itwillbs.service.BoardServiceImpl;
import com.itwillbs.service.MemberService;

@RestController
public class AjaxController {
	
	@Inject
	private MemberService memberService;
	@Inject
	private BoardService boardService;
	
	@RequestMapping(value = "/zzim", method = RequestMethod.GET)
	public ResponseEntity<String> zzim(MemberBean mb, HttpSession session) {
		ResponseEntity<String> entity=null;
		String result="";
		
		try {
			String id=(String) session.getAttribute("id");
			mb.setId(id);
			
			 MemberBean mb2=memberService.getWish(mb);			 
			System.out.println("id : " +id);

			if(id != null) {
				if(mb2 != null) {
					memberService.deleteWish(mb);
					System.out.println("deleteWish(mb)일 때 아이디 : " + mb.getId() + ", 위시 번호:" + mb.getWish());	
					result="nozzim";
					
				} else if(mb2 == null) {
					memberService.insertWish(mb);
					System.out.println("insertWish(mb)일 때 아이디 : " + mb.getId() + ", 위시 번호:" + mb.getWish());
					result="zzim";
				}
			}else {
				result="로그인하세요";
			}
			entity=new ResponseEntity<String>(result,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity=new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}


}
