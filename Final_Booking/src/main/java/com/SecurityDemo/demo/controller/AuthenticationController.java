package com.SecurityDemo.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.SecurityDemo.demo.model.ConfirmationToken;
import com.SecurityDemo.demo.model.RoomBookingDetails;
import com.SecurityDemo.demo.model.User;
import com.SecurityDemo.demo.repository.ConfirmationTokenRepository;
import com.SecurityDemo.demo.service.EmailSenderService;
import com.SecurityDemo.demo.service.RoomBookingService;
import com.SecurityDemo.demo.service.UserService;

@Controller
public class AuthenticationController {

	@Autowired
	UserService userService;

	@Autowired
	RoomBookingService roomBookingService;

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	@RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = "/userHome", method = RequestMethod.GET)
	public ModelAndView home(Authentication authentication) {

		ModelAndView mv = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		mv.addObject("name", "Welcome " + user.getName());

		String user1 = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.allStatus(user1);
		// mv.addAttribute("list", list);

		mv.addObject("list", list);
		mv.setViewName("userHome");
		return mv;
	}

	@RequestMapping(value = "/adminHome", method = RequestMethod.GET)
	public ModelAndView adminHome() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("adminHome");
		return modelAndView;
	}
	
	@RequestMapping(value = "/tlHome", method = RequestMethod.GET)
	public ModelAndView tlHome(Authentication authentication) {
		ModelAndView mv = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		mv.addObject("name", "Welcome " + user.getName());

		String user1 = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.allStatus(user1);
		// mv.addAttribute("list", list);

		mv.addObject("list", list);
		mv.setViewName("tlHome");
		return mv;

	}
	
	@RequestMapping(value = "/pmHome", method = RequestMethod.GET)
	public ModelAndView pmHome(Authentication authentication) {
		ModelAndView mv = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		mv.addObject("name", "Welcome " + user.getName());

		String user1 = authentication.getName();

		List<RoomBookingDetails> list = roomBookingService.allStatus(user1);
		// mv.addAttribute("list", list);

		mv.addObject("list", list);
		mv.setViewName("pmHome");
		return mv;

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView();
		if (bindingResult.hasErrors()) {
			modelAndView.addObject("successMessage", "Please correct the errors in form!");
			modelMap.addAttribute("bindingResult", bindingResult);

		} else if (userService.isUserAlreadyPresent(user)) {
			modelAndView.addObject("successMessage", "user already exists!");
			modelAndView.setViewName("error");

		} else {
			userService.saveUser(user);

			ConfirmationToken confirmationToken = new ConfirmationToken(user);

			confirmationTokenRepository.save(confirmationToken);

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getEmail());
			mailMessage.setSubject("Complete Registration!");
			mailMessage.setFrom("nairobley@gmail.com");
			mailMessage.setText("To confirm your account, please click here : "
					+ "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

			emailSenderService.sendEmail(mailMessage);

			modelAndView.addObject("email", user.getEmail());

			modelAndView.setViewName("Password/successfulRegisteration");

		}

		return modelAndView;

	}

}