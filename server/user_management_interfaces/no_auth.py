"""
The default User Management Interface where there is no login,
and all logs are broadcasted to all Web UI WebSockets.
"""
from user_management_interfaces import user_management_interface_base

class NoAuth(user_management_interface_base.UserManagementInterfaceBase):
    """A User Management Interface without Authentication.
       It broadcasts to all open Web UI WebSockets.
    """

    def get_login_ui(self, base_url):
        """Unused, but abstract so needs an implementation"""
        return ""

    def is_user_logged_in(self, request):
        """There are no users in a no auth environment

        Args:
            A Bottle request object
        Returns:
            Always returns a boolean true
        """
        return True

    def handle_login(self, form_data, request, response):
        """Function is unused, but is abstract so needs to be implemented"""
        return (True, "")

    def get_api_key_for_user(self, request):
        """Unused, but abstract so needs implementation"""
        return ""

    def find_associated_websockets(self, api_key, websocket_connections):
        """We can't tell the difference between users,
           so we just broadcast to everyone

        Args:
            The API Key, but this is an arbitrary value
            The list of WebSockets that go to web UIs
        Returns:
            The list of WebSockets that go to web UIs
        """
        return websocket_connections
