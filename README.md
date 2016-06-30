# DeltaTaskThree

## Synopsis
This is a Contact Manager app and is similar to the one you can find in your phone. In fact, it is based on the contact manager app on my phone. Basic functionalities such as adding, editing, deleting and searching are present. Along with that, the user can set a picture to go with the contact. And the user can call the number (if set) or email the person, if the email id is set. 

To set the image and also to call the contact's number, the app requires permission from the user.

##Tests
Right now, it is still in debug mode, meaning some bugs are to be expected. 

To add a new contact, click on the menu at the top right corner of the screen.

To edit or delete a contact, hold a contact in the list till the menu pops up asking if you want to edit or delete.

To search for a contact, click on the magnifying glass icon in the toolbar and enter a person's name or phone number. The search result will produce a list of contacts that had something in common with the search string entered.

By clicking on a contact in the list, you can view the full details of the contact. In this details view, a menu is present that will allow the user to edit or delete the contact.

In this detail activity, if the image is big, then it will take a while to go back to the main activity when you go back.

By holding down on the image, a menu appears asking if you want to choose a new photo or remove the existing one. 

By clicking the number (shown if provided) a caller intent is used to dial the number. The app is not to be blamed in any case where the call goes wrong after the intent is called.

By clicking the email id (shown if provided) the user can select her preferred app to compose an email to the contact.

