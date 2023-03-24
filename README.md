# MyMovieLibraryApp-Kotlin
It's a simple your movie library application. You can upload the movie pic and enter the other details of movie. App's homepage presents the movies that you saved.  In this way, you will not lose or forget your movies.

A "My Movie Library" application is a software program that allows users to organize and manage their personal movie collections. 

Here are some features that a "MyMovieLibrary" application:
1)Adding movies: Users should be able to add movies to their library manually by entering the movie details and application saves the movie to ROOM database.
2)Delete movies: Users can delete recorded movies. The deleted movie is also deleted from the ROOM database.

While developing the application, recyclerview was used to list the movies. ROOM database is used to save the movies and the data is stored on the phone. Fragment and Navigation are used. Data was exchanged between the fragments with a bundle. In this way, the data could be displayed on different pages. View binding is used. All of these used features have been added to the build.gradle file. Using the Bitmap, the aspect ratio and size of the photo of the movie added by the user are adjusted.

Here are some of images of "MyMovieLibrary":

![MyMovieLibrary-MovieList](https://user-images.githubusercontent.com/86521359/227533867-4541f469-c7fb-45ee-9dfc-594abf94f3a0.PNG)
![MyMovieLibrary-MovieDetail](https://user-images.githubusercontent.com/86521359/227533936-1a57b9e2-9fc6-4841-92fe-d1b44a93b618.PNG)
![MyMovieLibrary-AddMovie](https://user-images.githubusercontent.com/86521359/227533981-e8be9219-88b4-444b-8d61-60ca95197c7f.PNG)
