# UPSTAC-Week3-assignmentProblem Statement and Instructions
In this course, you have learnt about module level implementation, unit testing and regression testing. You also saw the implementation of Test Request module and performed unit testing and regression testing for the same.


In this assignment, you will have to implement some methods of the Lab Result and Consultation modules.

 

**If you wish to understand how to implement these methods, please watch the video inside "Assignment Helper & More on UPSTAC module Implementation" segment of this module. The video will greatly help you to implement your own functions/methods which are being asked as a part of your assignment.**

 

# Goal of this Assignment
Through working on this assignment, you will get a chance to experience the actual development of a complete module of a software product. By the end of this assignment, you will have contributed in writing APIs for the UPSTAC application!

 

## Stub File
Let us discuss in brief the structure and working of the UPSTAC Application until now.

1. The frontend code runs by typing ‘yarn start’ command in command prompt by navigating to the UI folder.
2. The backend code runs simply by running the ‘UpstacApplication.java’ file.
3. The user can view the registration page as well as the login page.
4. Since default users and dummy data have been removed from the application, users would need to register a new account to log in.
5. Upon login, the user can login as a patient and request for new test, which we implemented in this module.
6. The login for tester and doctor profiles will work, but the functionalities involved with those users will not work since they are yet to be implemented.

## To-Do Tasks
Implement the Lab Results and Consultation Feature in the UPSTAC API application. In order to implement this, follow the instructions given below:

* With respect to the Tester, complete the code in 'LabRequestController' class to return the list of test requests assigned to current tester. Specifically, you need to implement the "getForTester()" method.
* With respect to the Doctor, complete the code in ‘ConsultationController’ class to view all the test requests, the test requests assigned to current doctor, assign consultations to themselves and update the doctor suggestions. Specifically, you need to implement the
"getForConsultations()", "getForDoctor()", "assignForConsultation()" and the "updateConsultation()" methods.

**HINT:
You should make use of the hints provided in the stub code to implement the methods & also watch Muthu's video inside the "More on UPSTAC application & Assignment Helper" segment of this module.**
 
Observe the output by running the code in IntelliJ IDE and running the UI by typing ‘yarn start’ in command prompt by navigating to the UI folder.
Once the above said classes are correctly implemented, the tester can login, view test requests, assign and update test results. The doctors should also be able to login, view the updated test results, assign and suggest actions to the patients.
