package com.SecurityDemo.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.SecurityDemo.demo.model.ConfirmationToken;
import com.SecurityDemo.demo.model.User;
import com.SecurityDemo.demo.repository.ConfirmationTokenRepository;
import com.SecurityDemo.demo.repository.UserRepository;
import com.SecurityDemo.demo.service.EmailSenderService;
import com.SecurityDemo.demo.service.UserServiceImp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class PasswordController {

	@Autowired
	BCryptPasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	UserServiceImp userServiceImp;

	@RequestMapping(value = "/confirm-account", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		System.out.println("confirm");
		if (token != null) {
			User user = userRepository.findByEmail(token.getUser().getEmail());
			user.setEnabled(true);
			userRepository.save(user);
			modelAndView.setViewName("Password/accountVerified");
		} else {
			modelAndView.addObject("message", "The link is invalid or broken!");
			modelAndView.setViewName("error");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView displayLogin(ModelAndView modelAndView, User user) {
		modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

//	@RequestMapping(value = "/login", method = RequestMethod.POST)
//	public ModelAndView loginUser(ModelAndView modelAndView, User user) {
//		System.out.println("hhh");
//
//		User existingUser = userRepository.findByEmail(user.getEmail());
//		if (existingUser != null) {
//
//			if (encoder.matches(user.getPassword(), existingUser.getPassword()) && existingUser.isEnabled()==true) {
//System.out.println("in");
//				modelAndView.addObject("message", "Successfully logged in!");
//				modelAndView.setViewName("successLogin");
//			} else {
//
//				modelAndView.addObject("message", "Incorrect password. Try again.");
//				modelAndView.setViewName("login");
//			}
//		} else {
//			modelAndView.addObject("message", "The email provided does not exist!");
//			modelAndView.setViewName("login");
//
//		}
//
//		return modelAndView;
//	}

	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
	public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {

		modelAndView.addObject("user", user);
		modelAndView.setViewName("Password/forgotPassword");
		return modelAndView;
	}

	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
		User existingUser = userRepository.findByEmail(user.getEmail());
		if (existingUser != null) {

			ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

			confirmationTokenRepository.save(confirmationToken);

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(existingUser.getEmail());
			mailMessage.setSubject("Complete Password Reset!");
			mailMessage.setFrom("nairobley@gmail.com");
			mailMessage.setText("To complete the password reset process, please click here: "
					+ "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());

			emailSenderService.sendEmail(mailMessage);

			modelAndView.addObject("message",
					"Request to reset password received. Check your inbox for the reset link.");
			modelAndView.setViewName("Password/successForgotPassword");

		} else {
			modelAndView.addObject("message", "This email does not exist!");
			modelAndView.setViewName("Password/error");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/confirm-reset", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {

		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if (token != null) {
			User user = userRepository.findByEmail(token.getUser().getEmail());

			userRepository.save(user);
			modelAndView.addObject("user", user);
			modelAndView.addObject("emailId", user.getEmail());
			modelAndView.setViewName("Password/resetPassword");
		} else {
			modelAndView.addObject("message", "The link is invalid or broken!");
			modelAndView.setViewName("Password/error");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {

		if (user.getEmail() != null) {

			User tokenUser = userRepository.findByEmail(user.getEmail());
			tokenUser.setEnabled(true);
			tokenUser.setPassword(encoder.encode(user.getPassword()));

			userRepository.save(tokenUser);
			modelAndView.addObject("message",
					"Password successfully reset. You can now log in with the new credentials.");
			modelAndView.setViewName("Password/successResetPassword");
		}

		else {
			modelAndView.addObject("message", "The link is invalid or broken!");
			modelAndView.setViewName("error");
		}

		return modelAndView;
	}

}