from pymongo import MongoClient

import datastore_interfaces

class DatastoreInterface(object):
    """ Sets up the datastore interface. """
    """TO DO: Pydoc and API Spec """
    
    def __int__(self, **kwargs):
        """ Set up datastore interface 

        Args:
            **kwargs: any key arguments to be passed in

        """
           
    def store_logs(self, api_key, device_name, app_name, start_time, os_type, log_entries):
        """Store a set of log entries to the datastore. This function may be called multiple times per session, so it must append the log entries in the storage mechanism.

        Args
            api_key: the API Key associated with the logs
            device_name: the name of the device associated with the logs
            app_name: the name of the app associated with the logs
            start_time: the time that the session started
            os_type: the OS type (iOS or Android)
            log_entries: the log entries to store
        """

    def set_session_over(self, api_key, device_name, app_name, start_time):
        """Called to indicate to the datastore that the session is over. This can set a flag on the session in the datastore indicating that it should not be modified, for example.

        Args:
            api_key: the API Key associated with the logs
            device_name: the name of the device associated with the logs
            app_name: the name of the app associated with the logs
            start_time: the time that the session started

        """
        
    def retrieve_logs(self, api_key, device_name, app_name, start_time):
        """Retrieve logs for given session

        Args:
            api_key: the API Key to retrieve logs for
            device_name: the name of the device to retrieve logs for
            app_name: the name of the app to retrieve logs for
            start_time: the time that the session started

        Returns:
            osType: the OS type
            logEntries: a list of log entries as Python dictionaries

        """
        
    def retrieve_devices(self, api_key):
        """Retrieve a list of devices associated with the given API Key

        Args:
            api_key: the API Key to retrieve devices for

        Returns:
            array: array of names of device names 

        """

    def retrieve_apps(self, api_key, device):
        """Retrieves apps given a device

        Args:
            api_key: the API Key to retrieve logs for
            device: the device name to retrieve logs for  

        Returns:
            array: array of the names of the apps on the given device

        """

    def retrieve_sessions(self, api_key, device, app):
        """Retrieve a list of sessions for a given API Key, device, and app.

        Args:
            api_key: the API Key to retrieve sessions for
            device_name: the name of the device to retrieve sessions for
            app_name: the name of the app to retrieve sessions for

        Returns:
            array: list of datetime objects, one for each of the session start times associated with the given API Key, device, and app
        """

    def add_device_app(self, api_key, device, app):
        """Add a device/app combination to the device/app collection

        Args:
            api_key: the API Key 
            device_name: the name of the device 
            app_name: the name of the app 
        
        """

    def get_user(self, webIdToken):
        """
        placeholder for tests (working without UMI): remove when done

        gets api key for user (simply returns a simple string)
        """

    
    
    