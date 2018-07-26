package dev.aura.mplmanager.ftp;

import dev.aura.mplmanager.config.Config;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

@RequiredArgsConstructor
public class ConfigUserManager implements UserManager {
  private final Config.SectionFTP config;
  private final File homeDir;

  @Override
  public User authenticate(Authentication authentication) throws AuthenticationFailedException {
    if (!(authentication instanceof UsernamePasswordAuthentication))
      throw new AuthenticationFailedException("Authentication failed");

    try {
      UsernamePasswordAuthentication properAuthentification =
          (UsernamePasswordAuthentication) authentication;
      User user = getUserByName(properAuthentification.getUsername());

      if ((user == null) || !user.getPassword().equals(properAuthentification.getPassword()))
        throw new AuthenticationFailedException("Authentication failed");

      return user;
    } catch (FtpException e) {
      throw new AuthenticationFailedException("Authentication failed");
    }
  }

  @Override
  public void delete(String username) throws FtpException {
    config.getUsers().remove(username);
  }

  @Override
  public boolean doesExist(String username) throws FtpException {
    return config.getUsers().containsKey(username);
  }

  @Override
  public String getAdminName() throws FtpException {
    // No admin
    return null;
  }

  @Override
  public String[] getAllUserNames() throws FtpException {
    return config.getUsers().keySet().toArray(new String[0]);
  }

  @Override
  public User getUserByName(String username) throws FtpException {
    if (!doesExist(username)) return null;

    return new PrivilegedUser(username, config.getUsers().get(username), homeDir);
  }

  @Override
  public boolean isAdmin(String username) throws FtpException {
    // No admin
    return false;
  }

  @Override
  public void save(User user) throws FtpException {
    config.getUsers().put(user.getName(), user.getPassword());
  }
}
