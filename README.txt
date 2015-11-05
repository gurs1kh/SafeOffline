SaveOnline README

CURRENT IMPLEMENTED FEATURES

User Login: - able to go online and syncronize your saved URL with your account.

	Clicking the "SIGN IN" button on the corner menu on the activity to be sent to the application sign in page. Inputting your user information will log you into the account and will syncronize your Main activity list of Saved URL with what we have already pushed online.

Add URL:
	using the button located on the bottom of the screen, user will be taken to a new Activity where user can input a title for this page and the URL corresponding to it. If user is signed into the application, then the url that the user input will be pushed to the database. Known bug: SQLite has not been impleented, therefor URL inputted will only be shown when user logged in and is connected to the internet.

User Logout:
	Clicking the "SIGN OFF" button on the corner menu (will only show when user is signed in), user will sign off from the application and will not be able to syncronize their saved URL with the webservice.

CURRENT IMPLEMENTED USE CASE

Login/Create New User/Use Offline:
	User can login using a premade account with these credentials:

	User: team1@example.com
	Password: Password1!

	Login page currently doubles as registration page

Insert Link:
	User save a URL witha custom title. Currently will only save the URL inside their database, Insert Link will only save the information and will not actually download the page for now.

Open Saved Link:
	NOT YET IMPEMENTED

Update Link:
	NOT YET IMPLEMENTED