To be able to push updates to devices you must sign up for C2DM.
http://code.google.com/android/c2dm/signup.html

Once signed up, run auth.sh with the email you registered with.
Copy the Auth value and paste it into a dataMessagingToken.txt and put this in your war directory.

Once the value is set initially, the appengine will retain the latest value in the datastore (latest version is returned in response headers).