# YouToDo Version 1.0
YouToDo is a simple, single-user ToDo console app using Java, Maven, and PostgreSQL. 

It is an attempt to familiarize myself with the inner workings of how a MVC-style application from the ground up and intentionally uses as little third party libraries and frameworks as possible. This will also serve as the groundwork for a web app and Android version of this same kind of app.

## Table of contents
1. [Project Description](#Project-Description)
2. [Requirements and Dependencies](#Requirements-and-Dependencies)
3. [Setup](#Setup)
4. [Status](#Status)
5. [License](#License)


## Project Description
YouToDo follows a MVC framework to provide a simple, single-user ToDo application. The Model layer works in conjunction with a [CockroachDB](https://www.cockroachlabs.com/product/) serverless cluster, a PostgreSQL database with some modifications. The View layer is handled exclusively by the users CLI console with keyboard input. The Controller layer handles the business logic with error checking. When it comes together it is an extremely lightweight application that functions just as the user would expect intuitively.

## Requirements and Dependencies
- Java JDK 17 or newer
- Maven 4.0
  - [PostgreSQL JDBC Driver 42.2.6](https://jdbc.postgresql.org/)


## Setup
- Windows: Open a command prompt and enter
```bash
java –jar c:\path\to\jar\file.jar
```
- Linux: Open a terminal inside the YouToDo folder and enter
```bash
java -jar youtodo.jar
```
- Mac: Open a command prompt and enter
```bash
java -jar youtodo.jar
```

## Status
Further development on this project is not currently planned, as the project is more of a proof of concept and learning experience. Any further developments will be implemented in the webapp or Android version of this app.


## License

    Copyright [2023] [Dan Luoma]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
