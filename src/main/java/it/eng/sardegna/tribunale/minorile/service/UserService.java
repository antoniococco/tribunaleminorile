package it.eng.sardegna.tribunale.minorile.service;

import it.eng.sardegna.tribunale.minorile.model.User;

public interface UserService {
	public User findUserByEmail(String email);

	public void saveUser(User user);
}