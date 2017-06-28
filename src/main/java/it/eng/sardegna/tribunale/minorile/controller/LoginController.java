package it.eng.sardegna.tribunale.minorile.controller;

import java.util.Iterator;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import it.eng.sardegna.tribunale.minorile.model.Role;
import it.eng.sardegna.tribunale.minorile.model.User;
import it.eng.sardegna.tribunale.minorile.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = { "/403" }, method = RequestMethod.GET)
	public ModelAndView error403() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("403");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");

		}
		return modelAndView;
	}

	@PreAuthorize("hasRole('ROLE_CITIZEN') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_RESPONSIBLE') or hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/authenticated/home", method = RequestMethod.GET)
	public ModelAndView home() {
		return buildHomeModelAndView();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/authenticated/admin", method = RequestMethod.GET)
	public ModelAndView admin() {
		return buildHomeModelAndView();
	}

	@PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
	@RequestMapping(value = "/authenticated/instr", method = RequestMethod.GET)
	public ModelAndView instr() {
		return buildHomeModelAndView();
	}

	@PreAuthorize("hasRole('ROLE_SUPERVISOR')")
	@RequestMapping(value = "/authenticated/superv", method = RequestMethod.GET)
	public ModelAndView superv() {
		return buildHomeModelAndView();
	}

	@PreAuthorize("hasRole('ROLE_RESPONSIBLE')")
	@RequestMapping(value = "/authenticated/resp", method = RequestMethod.GET)
	public ModelAndView resp() {
		return buildHomeModelAndView();
	}

	private ModelAndView buildHomeModelAndView() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Benvenuto " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");

		Iterator<Role> iterator = user.getRoles().iterator();
		modelAndView.addObject("userMessage",
				"Contenuto disponibile solo per gli utenti con ruolo " + iterator.next().getRole() + "");
		modelAndView.setViewName("authenticated/home");
		return modelAndView;
	}

}