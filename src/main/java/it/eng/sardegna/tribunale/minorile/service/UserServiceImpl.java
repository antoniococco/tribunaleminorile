package it.eng.sardegna.tribunale.minorile.service;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.eng.sardegna.tribunale.minorile.model.Role;
import it.eng.sardegna.tribunale.minorile.model.User;
import it.eng.sardegna.tribunale.minorile.repository.RoleRepository;
import it.eng.sardegna.tribunale.minorile.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);

		String role = "CITIZEN";
		String name = user.getName();
		if (name.startsWith("instr")) {
			role = "INSTRUCTOR";
		} else if (name.startsWith("resp")) {
			role = "RESPONSIBLE";
		} else if (name.startsWith("superv")) {
			role = "SUPERVISOR";
		}

		Role userRole = roleRepository.findByRole(role);
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}

}