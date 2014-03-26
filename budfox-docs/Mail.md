# Homework

## Goal

Create well-working, headless micro-lending app that satisfies both technical
and business requirements.


## Business requirements

* Client can apply for loan by providing his personal data, amount and term
* Loan application risk analysis is performed. Risk is considered too high if:
  - the attempt to take loan is made after from 00:00 to 6:00 AM
    with max possible amount.
  - reached max applications (e.g. 3) per day from a single IP.
* Loan is issued if there are no risks associated with the application.
  If so, client receives a reference to newly created loan. However, if risk
  is surrounding the application, client receives "rejection" message.
* Client should be able to extend a loan. Loan term gets extended for one week,
  interest gets increased by a factor of 1.5.
* Client should be able to retrieve whole history of his loans,
  including loan extensions.


## Technical requirements

* Backend in Java 6+, XML-less Spring, Hibernate.
* RESTful API
* Code is production-ready, covered with unit tests
* Automated acceptance tests that exercises application using RESTful API


## What gets evaluated

* Requirements
* Code quality (both production and test)
* Is API easy to use and according to RESTful architectural style
* How simple it is to run the application (embedded DB/embedded container)