# Jarvis

- An Android application designed to allow users to view, save, and edit notes.
- This application supports API levels 15 to 23 with Material Design for the UI and UX. 
- Architecturally, this application is developed with the MVC architectural pattern with various object-oriented design patterns like Repository, Factory, and Builder for the model layer. 
- Abstracted the Repository in the model layer with RxJava to implement an Observable API to trivialize concurrency to interact with the SQLite database.
- Utilized Facebook's Stetho to expose application to Google Chrome Developer Tools for debugging the UI and the SQLite database.
