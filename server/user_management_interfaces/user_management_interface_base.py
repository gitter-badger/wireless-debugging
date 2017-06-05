"""
Defines the interface for user management interfaces
"""

class UserManagementInterfaceBase:
    """The user management inferface definition.
        
        The set of necessary functions a user needs to implement in order
        to use user authentication on their server.
    """

    # TODO: Change base_url to be relative URL
    def get_login_ui(self, base_url):
        """Generates an HTML UI to show on the login page.

        Args:
            base_url: String, the base URL for the app.
        Returns:
            String, contains the HTML for the login UI. This HTML should inlude
            an HTML form which posts to the login URL.
        """ 
        raise NotImplementedError

    def is_user_logged_in(self, request):
        """Determines if the user is already authenticated.

        Args:
            request: An HTTP request context from Bottle.
        Returns:
            Boolean, returns true if the user is authenticated, false otherwise.
        """
        raise NotImplementedError

    def handle_login(self, form_data, request, response):
        """Perform server-side user authentication.

        Args:
            form_data: Dict/Bottle form object, the form data from the form
            generated by get_login_ui.
            request: An HTTP request context from Bottle.
            response: An HTTP response context from Bottle.
        Returns: 
            A tuple containing:
                login_successful: Boolean, returns true if the login was
                    successful. Returns false otherwise.
                error_message: String, returns a message describing why the 
                    login failed if login_successful is false. Empty if
                    login_successful is true.
        """
        raise NotImplementedError

    def get_api_key_for_user(self, request):
        """Returns the API Key associated with the request.

        Args:
            request: the HTTP request context from Bottle.
        Returns:
            The API Key associated with the given request.
            The request parameter can be used to access any cookies that may
            have been set in handle_login.
        """
        raise NotImplementedError

    def find_associated_websockets(self, api_key, websocket_connections):
        """Determines which WebSocket connections to send parsed logs to.

            Args:
                api_key: The API Key for which to find associated sessions.
                websocket_connections: a list of all Websocket connections to
                Web UI's connected to the Web App Backend.
            Returns:
                A list of WebSocket connections to send the logs to.
                (This should be a subset of websocket_connections)
        """
        raise NotImplementedError
