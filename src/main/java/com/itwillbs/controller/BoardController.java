package com.itwillbs.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwillbs.domain.BoardBean;
import com.itwillbs.domain.OneRoomBean;
import com.itwillbs.domain.PageBean;
import com.itwillbs.service.BoardService;

@Controller
public class BoardController {
	@Inject
	private BoardService boardService;

	@Resource(name = "uploadPath")
	private String uploadPath;

//	http://localhost:8080/myweb2/board/write
	@RequestMapping(value = "/board/write", method = RequestMethod.GET)
	public String write() {
		// /WEB-INF/views/board/writeForm.jsp
		return "board/writeForm";
	}

	@RequestMapping(value = "/writePro", method = RequestMethod.POST)
	public String writePro(BoardBean bb) {
		OneRoomBean testBean = new OneRoomBean();
		testBean.setSeller_id("admin@gmail.com");

//		Map<String, Object> options = new HashMap();
//		options.put("에어컨", "Y");
//		options.put("냉장고", "N");
//		Map<String, Object> include_fees = new HashMap();
//		include_fees.put("전기세", "Y");
		String[] include_fees = new String[] {"전기세", "수도세"};
		String[] options = new String[] {"에어컨", "냉장고", "TV"};
		
		testBean.setInclude_fees(include_fees);
		testBean.setOption(options);

		boardService.insertRoom(testBean);

		return "redirect:/";
	}


	@RequestMapping(value = "/findRooms", method = RequestMethod.GET)
	public String list(HttpServletRequest request, Model model) {
		PageBean pb = new PageBean();
		if (request.getParameter("pageNum") != null) {
			pb.setPageNum(request.getParameter("pageNum"));
		} else {
			pb.setPageNum("1");
		}
		pb.setPageSize(10);

		List<OneRoomBean> roomList = boardService.getBoardList(pb);

		// count(*) 구하기 => set메서드 호출 => pageBlock, startPage, endPage, pageCount구하기
		pb.setCount(boardService.getBoardCount());

		model.addAttribute("roomList", roomList);
		model.addAttribute("pb", pb);

		// /WEB-INF/views/board/list.jsp
		return "findRooms";
	}

//	http://localhost:8080/myweb2/board/fwrite
	@RequestMapping(value = "/board/fwrite", method = RequestMethod.GET)
	public String fwrite() {
		// /WEB-INF/views/board/fwriteForm.jsp
		return "board/fwriteForm";
	}

	@RequestMapping(value = "/board/fwritePro", method = RequestMethod.POST)
	public String fwritePro(HttpServletRequest request, MultipartFile file) throws Exception {
		// 업로드
		System.out.println(uploadPath);
		System.out.println("원본파일이름" + file.getOriginalFilename());
		// 월본실제파일 file.getBytes()

		// 중복을 피하기 위해서 업로드파일이름 => 램덤문자_업로드파일이름
		UUID uid = UUID.randomUUID();
		String saveName = uid.toString() + "_" + file.getOriginalFilename();
		System.out.println(saveName);

		// upload폴더에 복사
		File target = new File(uploadPath, saveName);
		FileCopyUtils.copy(file.getBytes(), target);

		// 자바빈 저장
		BoardBean bb = new BoardBean();
		bb.setName(request.getParameter("name"));
		bb.setPass(request.getParameter("pass"));
		bb.setSubject(request.getParameter("subject"));
		bb.setContent(request.getParameter("content"));
//		bb.setFile(업로드된 파일이름);
		bb.setFile(saveName);

		// 디비 insertBoard()
		boardService.insertBoard(bb);

		return "redirect:/board/list";
	}

	// /board/content?num=${bb.num}
//	http://localhost:8080/myweb2/board/content?num=${bb.num}
	@RequestMapping(value = "/board/content", method = RequestMethod.GET)
	public String content(HttpServletRequest request, Model model) {
		int num = Integer.parseInt(request.getParameter("num"));
		// num 글번호에 해당하는 게시판 글 조회 => BoardBean 담아서 옴
		BoardBean bb = boardService.getBoard(num);
		// bb를 담아서 content.jsp 이동
		model.addAttribute("bb", bb);
		// /WEB-INF/views/board/content.jsp
		return "board/content";
	}

	@RequestMapping(value = "/board/update", method = RequestMethod.GET)
	public String update(HttpServletRequest request, Model model) {
		int num = Integer.parseInt(request.getParameter("num"));
		// num 글번호에 해당하는 게시판 글 조회 => BoardBean 담아서 옴
		BoardBean bb = boardService.getBoard(num);
		// bb를 담아서 content.jsp 이동
		model.addAttribute("bb", bb);
		// /WEB-INF/views/board/content.jsp
		return "board/updateForm";
	}

	@RequestMapping(value = "/board/updatePro", method = RequestMethod.POST)
	public String updatePro(BoardBean bb, Model model) {
		BoardBean bb2 = boardService.numCheck(bb);

		if (bb2 != null) {
			boardService.updateBoard(bb);
			return "redirect:/board/list";
		} else {
			// 입력하신 정보가 틀립니다.
			model.addAttribute("msg", "입력하신 정보가 틀립니다.");
			// /WEB-INF/views/member/msg.jsp
			return "member/msg";
		}
	}

	@RequestMapping(value = "/board/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, Model model) {
		int num = Integer.parseInt(request.getParameter("num"));

		model.addAttribute("num", num);

		return "board/deleteForm";
	}

	@RequestMapping(value = "/board/deletePro", method = RequestMethod.POST)
	public String deletePro(BoardBean bb, Model model) {
		BoardBean bb2 = boardService.numCheck(bb);

		if (bb2 != null) {
			boardService.deleteBoard(bb);
			return "redirect:/board/list";
		} else {
			// 입력하신 정보가 틀립니다.
			model.addAttribute("msg", "입력하신 정보가 틀립니다.");
			// /WEB-INF/views/member/msg.jsp
			return "member/msg";
		}
	}

}
