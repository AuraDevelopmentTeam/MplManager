package dev.aura.mplmanager.ftp;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class PrivilegedUser extends BaseUser {
  private static final List<Authority> AUTHORITIES =
      Arrays.asList(
          new WritePermission(),
          new ConcurrentLoginPermission(Integer.MAX_VALUE, Integer.MAX_VALUE),
          new TransferRatePermission(Integer.MAX_VALUE, Integer.MAX_VALUE));

  public PrivilegedUser(String username, String password, File homeDir) {
    setName(username);
    setPassword(password);
    setHomeDirectory(homeDir.getAbsolutePath());
    setAuthorities(AUTHORITIES);
  }
}
