package jetbrains.rsynk.settings.server

import org.apache.sshd.server.Command
import org.apache.sshd.server.CommandFactory

class ExplicitCommandFactory : CommandFactory {
  override fun createCommand(command: String): Command {
    throw UnsupportedOperationException("not implemented")
  }
}
