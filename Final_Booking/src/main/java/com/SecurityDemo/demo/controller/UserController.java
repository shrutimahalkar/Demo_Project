package com.SecurityDemo.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.SecurityDemo.demo.model.Date;
import com.SecurityDemo.demo.model.Room;
import com.SecurityDemo.demo.model.RoomBookingDetails;
import com.SecurityDemo.demo.model.Tl;
import com.SecurityDemo.demo.model.User;
import com.SecurityDemo.demo.model.mail_request;
import com.SecurityDemo.demo.repository.RoomRepository;
import com.SecurityDemo.demo.repository.tlRepository;
import com.SecurityDemo.demo.service.MailRequestService;
import com.SecurityDemo.demo.service.RoomBookingService;
import com.SecurityDemo.demo.service.RoomService;
import com.SecurityDemo.demo.service.UserService;

@Controller
public class UserController {

	@Autowired
	RoomService roomSertvice;

	@Autowired
	RoomBookingService roomBookingService;

	@Autowired
	UserService userService;

	Authentication authentication;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	MailRequestService mailRequestService;

	@Autowired
	tlRepository repo;

	@RequestMapping(value="/book_room_date", method=RequestMethod.POST) 

	public String view2(Model m, Date date) {
	String status="CONFIRM";
	String date1=date.getDate1();	
	String date2=date.getDate2();	


	List<Room> list=roomSertvice.availableRoomByDate(date1,date2,status);
	System.out.println(list);
	m.addAttribute("date1", date.getDate1());
	m.addAttribute("date2", date.getDate2());
	
	System.out.println(date.getDate1());
	System.out.println(date.getDate2());

	m.addAttribute("list", list);
	System.out.println("hello");
	System.out.println(list);
	return "newUser/viewAvailableRoom";

	}
	@RequestMapping(value = "/viewAvailableRoom", method = RequestMethod.GET)
	public ModelAndView register1() {
		System.out.println("in user");
		ModelAndView modelAndView = new ModelAndView();
		Date date = new Date();
		modelAndView.addObject("date", date);
		modelAndView.setViewName("newUser/viewAvailableRoom"); // resources/template/register.html
		return modelAndView;
	}

	@RequestMapping(value = "/pendingStatus", method = RequestMethod.GET)

	public String pendingStatus(Authentication authentication, Model m) {
		
		String user = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.pendingStatus(user);
		m.addAttribute("list", list);

		return "newUser/pendingRequest";

	}

	@RequestMapping(value = "/confirmStatus", method = RequestMethod.GET)

	public String confirmStatus(Authentication authentication, Model m) {

		String user = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.confirmStatus(user);
		m.addAttribute("list", list);

		return "newUser/confirmRequest";

	}

	@RequestMapping(value = "/cancelStatus", method = RequestMethod.GET)

	public String cancelStatus(Authentication authentication, Model m) {
		System.out.println("cancel");
		String user = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.cancelStatus(user);
		m.addAttribute("list", list);

		return "newUser/cancelRequest";

	}

	@RequestMapping(value = "/Change_emailuser", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView changeemail() {
		ModelAndView modelAndView = new ModelAndView();

		org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userService.findByM(auth.getName());

		String role1 = user.getRoles().toString();
		String dept = user.getDepartment();

		modelAndView.addObject("role", role1);
		modelAndView.addObject("dept", dept);

		modelAndView.setViewName("newUser/edit_emailForm"); // resources/template/admin.html
		return modelAndView;
	}

	@RequestMapping(value = "/edit_mailuser", method = RequestMethod.POST)

	public String bookRoomForm(mail_request mail) {
		System.out.println(mail);
		mailRequestService.savemailreq(mail);

		return "userHome";

	}

	@RequestMapping(value = "/tl", method = RequestMethod.GET)

	public String findTl(Model m) {
		System.out.println("tl");

		org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userService.findByM(auth.getName());
		System.out.println(user);

		String dept = user.getDepartment();
		System.out.println(dept);

		List<Tl> list = repo.getTl(dept);
		System.out.println(list);
		m.addAttribute("list", list);

		return "tlPages/tlname";

	}

//	@RequestMapping(value = "/see_status", method = RequestMethod.GET)
//
//	public String userProfile(Authentication authentication, Model m) {
//
//		String USER = authentication.getName();
//
//		User userProfile = userService.findByEmail(USER);
//		m.addAttribute("userProfile", userProfile);
//
//		return "userPages/profile";
//
//	}

//	@RequestMapping(value = "/allStatus", method = RequestMethod.GET)
//
//	public String allStatus(Authentication authentication, Model m) {
//
//		String user = authentication.getName();
//
//		List<RoomBookingDetails> list = roomBookingService.allStatus(user);
//		m.addAttribute("list", list);
//
//		return "newUser/roomstatus";
//
//	}

//	@RequestMapping(value = "/changeEmailRequest", method = { RequestMethod.GET, RequestMethod.POST })
//	public ModelAndView changeemail() {
//	ModelAndView modelAndView = new ModelAndView();
//
//	org.springframework.security.core.Authentication auth =
//	SecurityContextHolder.getContext().getAuthentication();
//
//	User user = userService.findByEmail(auth.getName());
//
//	String role1=	user.getRoles().toString();
//
//	modelAndView.addObject("role", role1);
//
//	modelAndView.setViewName("newUser/edit_emailForm"); // resources/template/admin.html
//	return modelAndView;
//	}
//	
//	@RequestMapping(value = "/book_room_date/{page}", method = RequestMethod.POST)
//	public ModelAndView roomManagement(@PathVariable(value = "page") int page,
//			@RequestParam(defaultValue = "id") String sortBy, Date date) 
//	{
//		ModelAndView modelAndView = new ModelAndView();
//		System.out.println("in");
//		String status="CONFIRM";
//		String date1=date.getDate();
//
//		PageRequest pageable = PageRequest.of(page - 1, 5, Sort.Direction.DESC, sortBy);
//		
////		long offset=pageable.getOffset();
////		int size=pageable.getPageSize();
//		
//		long offset=1;
//		int size=5;
//		
//		//Page<Room> userPage = roomRepository.findAll(pageable);
//		Page<Room> userPage = roomSertvice.availableRoomByDate(date1,status,size,offset);
//		int totalPages = userPage.getTotalPages();
//		if (totalPages > 0) {
//			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
//			modelAndView.addObject("pageNumbers", pageNumbers);
//		}
//		modelAndView.addObject("activeUserList", true);
//		modelAndView.addObject("list", userPage.getContent());
//
//		modelAndView.setViewName("newUser/viewAvailableRoom");
//		return modelAndView;
//	}

//	@RequestMapping("/viewAvailableRoom")
//	public String viewAllRoom(Model m) {
//
//		List<Room> listRoom = roomSertvice.selectAvailable();
//		m.addAttribute("listRoom", listRoom);
//		return "newUser/viewAvailableRoom";
//	}

}
