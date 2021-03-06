""" Datastore Interface Definition

The Datastore Interface specifies a set of functions required in order to
implement historical log storage.

See the full documentation for the Datastore Interface here:
https://sumnerevans.github.io/wireless-debugging/Datastore-Interface
"""

class DatastoreInterface(object):
    """ This class sets up the Datastore Interface. """

    def __init__(self, **kwargs):
        """ This constructor sets up Datastore Interface.

        Args:
            **kwargs: any key arguments needed to set up the Datastore Interface
        """
        pass

    def store_logs(self, api_key, device_name, app_name, start_time, os_type, log_entries):
        """ This function stores a set of log entries to the datastore.

        This function may be called multiple times per session, so it must
        append the log entries in the storage mechanism.

        Args
            api_key:     the API Key associated with the logs
            device_name: the name of the device associated with the logs
            app_name:    the name of the app associated with the logs
            start_time:  the time that the session started
            os_type:     the OS type (iOS or Android)
            log_entries: the log entries to store
        """
        raise NotImplementedError

    def set_session_over(self, api_key, device_name, app_name, start_time):
        """ This function is called to indicate to the datastore that the log
        session is over.

        This can set a flag on the session in the datastore indicating that it
        should not be modified, for example.

        Args:
            api_key:     the API Key associated with the logs
            device_name: the name of the device associated with the logs
            app_name:    the name of the app associated with the logs
            start_time:  the time that the session started
        """
        raise NotImplementedError

    def retrieve_logs(self, api_key, device_name, app_name, start_time):
        """ This function retrieves logs for the given session.

        Args:
            api_key:     the API Key to retrieve logs for
            device_name: the name of the device to retrieve logs for
            app_name:    the name of the app to retrieve logs for
            start_time:  the time that the session started

        Returns:
            osType:     the OS type
            logEntries: a list of log entries as Python dictionaries
        """
        raise NotImplementedError


    def retrieve_devices(self, api_key):
        """ This function retrieves a list of devices associated with the given
        API Key.

        Args:
            api_key: the API Key to retrieve devices for

        Returns:
            array: array of names of device names
        """
        raise NotImplementedError


    def retrieve_apps(self, api_key, device_name):
        """ This function retrieves apps given a device.

        Args:
            api_key:     the API Key to retrieve logs for
            device_name: the device name to retrieve logs for

        Returns:
            array: array of the names of the apps on the given device
        """
        raise NotImplementedError


    def retrieve_sessions(self, api_key, device_name, app_name):
        """ This function retrieves a list of sessions for a given API Key,
        device, and app.

        Args:
            api_key:     the API Key to retrieve sessions for
            device_name: the name of the device to retrieve sessions for
            app_name:    the name of the app to retrieve sessions for

        Returns:
            array: list of datetime objects, one for each of the session start
                   times associated with the given API Key, device, and app
        """
        raise NotImplementedError


    def add_device_app(self, api_key, device_name, app_name):
        """ This function adds a device/app combination to the device/app
        collection.

        Args:
            api_key:     the API Key
            device_name: the name of the device
            app_name:    the name of the app
        """
        raise NotImplementedError

    def update_device_alias(self, api_key, device_raw_name, device_alias):
        """ This function updates alias for a device.

        Args:
            api_key:         the API key
            device_raw_name: name being aliased
            device_alias:    new alias for device
        """
        raise NotImplementedError

    def update_app_alias(self, api_key, device_name, app_raw_name, app_alias):
        """ This function updates alias for an app.

        Args:
            api_key:      the API key
            device_name:  device connected to app
            app_raw_name: name being aliased
            app_alias:    new alias for app
        """
        raise NotImplementedError

    def get_raw_device_name_from_alias(self, api_key, device_alias):
        """ This function returns the raw device name based on an alias.

        Args:
            api_key:      the API key
            device_alias: alias

        Returns:
            string: raw device name
        """
        raise NotImplementedError

    def get_raw_app_name_from_alias(self, api_key, device_name, app_alias):
        """ This function returns raw app name based on an alias.

        Args:
            api_key:     the API key
            device_name: device connected to app
            app_alias:   alias for app

        Returns:
            string: raw app name
        """
        raise NotImplementedError

    def clear_datastore(self):
        """ This function clears datastore of records. """
        raise NotImplementedError
