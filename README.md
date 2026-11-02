WorldExplorer
World Explorer is a Java desktop application that creates quiz questions using the REST Countries API about countries, capitals and flags. It also includes an integration with the Fish API for a “Count the Fish” bonus round, to show how a client app can call multiple REST services from different engine classes and GUI classes.

 A Java Quiz Game where players can discover the world.World Explorer – Quiz Game in Java.

World Explorer is a quiz game that is developed using java swing that is an interactive game that shows the world explorer. It lets you put your geographical knowledge to the test by answering questions about countries, capital cities and national flags.

The application fetches the real world country details from REST Countries API and it also has a bonus round, called Count the Fish, based on a Fish API.

The project showcases object-oriented programming, integration of REST API, handling of JSON, event-driven programming, user authentication, session management, and graphical user interface development.

---

 📌 Project Overview

World Explorer is an exciting, interactive geography quiz programme that aims to engage pupils' curiosity in learning geography.

The player logs in, chooses a game mode, answers a series of random questions, scores points, level-up and compete for the best scores on a leaderboard.

There is also a bonus round of this game to recognize the quantity of fish in a picture.

 Project Information

| Information | Details |
|---|---|
Project Part 1 | World Explorer |
Platform / Language | Java Desktop Application |
| Programming Language | Java |
Viewing Mode | Browser |
Development IDE | IntelliJ IDEA |
Object-Oriented/Event-Driven |
External APIs | REST Countries API, Fish API |
Data Storage (Local File Based Storage) |
Version Control - Git and GitHub |
Project type: University Coursework |

---

 ✨ Key Features

 🔐 1. User Authentication

The application has a login system to control players.

Features:
- User name and password logon.
- Use PBKDF2 to secure password hashing.
- User login and error correction.
- Remember Me functionality.
- Session management.
- User logout.

 🌎 2. Geography Quiz Modes

There are 3 main quiz modes offered by World Explorer.

Guess the Country

The players guess the capital of a country.

Example:

Which country is the capital of Tokyo?

Correct Answer: Japan

Guess the Capital

Players are given a list of all countries and they have to guess which countries starting with a letter are their capital cities.

Example:

Answer: The capital of France is Paris.

Correct Answer: Paris

Identify the Flag

Players determine which country the national flag is from.

Example:

Answer: This flag is the flag of this country:

Correct Answer: Japan

 🐟 3. Fish Bonus Round

Special bonus round with an external Fish API.

Features:
- Fetches the images for the fish from the API.
- Presents a question about the number of fish.
- Offers 10 possible answers (0-9)
- Give extra points for right answers.
- Gives feedback on answers right after they are given.

A bonus round is available after every 5 normal questions (if not stated otherwise).

 🏆 4. Scoring and Level System

World Explorer is scored by points.

| Activity | Points |
|---|---|
| Correct Quiz Answer | +10 |
| Incorrect Quiz Answer | 0 |
| Correct Bonus Answer | +20 |
| Incorrect Bonus Answer | 0 |

Level Progression

The game will follow a process of leveled play, depending on the scores accrued.

| Score | Level |
|---|---|
| 0–49 | Level 1 |
| 50–99 | Level 2 |
| 100–149 | Level 3 |
| 150–199 | Level 4 |

The game also makes questions more difficult as it adds more answer options to the question.

 ⏱️ 5. Countdown Timer

Time limits can be set for each quiz question.

Features:
- Countdown timer.
- Warning display for the end of time.
- Time-expiry handling.
- Next question will be displayed automatically.

 📊 6. Results and Performance Summary

Players will be able to see how they did on a quiz at the end of it.

The results screen will show:
- Player username.
- Selected game mode.
- Final score.
- Level reached.
- Correct answers.
- Incorrect answers.
- Answer accuracy.

 🥇 7. Leaderboard

World Explorer has a leaderboard for comparing player performance.

Features:
- Local score storage.
Ranking based on the scores.
- Top-player display.
- JSON score export.
- Player score comparison.

 🎨 8. Interactive User Interface

Using the Java Swing package the application offers an interactive desktop application.

UI Features:
- Dark-themed interface.
- Custom rounded buttons.
- Responsive layouts.
- Styled input fields.
- Interactive quiz cards.
- Answer feedback indicators.
- Progress bars.
- Navigation controls.
- Results screens, leaders screens.

---

 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
Lift | Application development |
Developing a GUI application with desktop using java swing.
Draw graphics and images using the AWT |
Communication over HTTP with REST API.
Process JSON API response |
The REST Countries API returns data for countries, their capital, and their flag.
Bonus-round questions | Fish API
| PBKDF2 | Password hashing |
Local Storage | User and score storage |
IntelliJ IDEA is a development environment.
| Git | Version control |
Version control systems |

The project is based on a custom JSON parser that is lightweight and does not require to include an external JSON library to deal with API responses.

---

 🏗️ System Architecture

World Explorer is designed in a modular fashion with its different parts being separated from each other: authentication, game logic, API communication, user interface.

```text
                  WORLD EXPLORER
                         |
                         v
                  Java Swing GUI
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
     Authentication   Game Engine    Leaderboard
          |              |              |
          v              v              v
     AuthService     Question Logic   Score Storage
          |              |
          v              v
     SessionManager   API Services
                         |
                 +-------+-------+
                 |               |
                 v               v
            REST Countries     Fish API
                 |
                 v
             JSON Parser
```

 Main Packages

`com.worldexplorer.auth`

Manages authentication, password hashing, user storage, and session management.

`com.worldexplorer.engine`

Includes game logic, question generation, scoring, level progression, bonus question, and leader board.

`com.worldexplorer.net`

Handles HTTP communication with external APIs.

`com.worldexplorer.peripherals`

Includes the primary application screen and separate screens.

`com.worldexplorer.peripherals.ui`

Provides re-usable java swing components and application styling.

📁 Project Structure

WorldExplorer/
│
├── src/
│   └── com/
│       └── worldexplorer/
│           │
│           ├── auth/
│           │   ├── AuthResult.java
│           │   ├── AuthService.java
│           │   ├── UserRepository.java
│           │   ├── FileUserRepository.java
│           │   ├── PasswordHasher.java
│           │   └── SessionManager.java
│           │
│           ├── engine/
│           │   ├── Game.java
│           │   ├── GameEngine.java
│           │   ├── GameServer.java
│           │   ├── FishServer.java
│           │   ├── QuestionProvider.java
│           │   ├── BonusProvider.java
│           │   ├── JsonParser.java
│           │   ├── Config.java
│           │   ├── GameSummary.java
│           │   ├── ScoreEntry.java
│           │   ├── ScoreRepository.java
│           │   ├── Leaderboard.java
│           │   └── ScoreExporter.java
│           │
│           ├── net/
│           │   └── HttpSupport.java
│           │
│           └── peripherals/
│               ├── LoginGUI.java
│               ├── GameGUI.java
│               ├── LoginScreen.java
│               ├── ModeSelectScreen.java
│               ├── QuizScreen.java
│               ├── BonusScreen.java
│               ├── ResultsScreen.java
│               │
│               └── ui/
│                   ├── Theme.java
│                   ├── RoundedButton.java
│                   ├── RoundedPanel.java
│                   ├── InputField.java
│                   ├── AnswerButton.java
│                   ├── ProgressBar.java
│                   └── ...
│
├── config.example.properties
├── config.properties
├── .gitignore
└── README.md

⚙️ Installation and Setup

Prerequisites

Prior to running World Explorer, make sure you've:

Java Development Kit (JDK 11 or above).

The IntelliJ IDEA or other Java editor.

Git (optional).

Internet connection to access API-based questions.

If necessary, provide valid REST Countries API credentials.

The first step is to clone the Repository.The first step is to clone the Repository.

Log in to the terminal and type:

git clone <YOUR_REPOSITORY_URL>

Change to project directory:

cd WorldExplorer

Click Step 2 – Open the Project.Click Step 2 – Open the Project.

Open IntelliJ IDEA.

Select File → Open.

Click on the WorldExplorer project folder.

Set up the project's SDK.

If needed, mark src folder as Sources Root.

Step 3 – Set up the APIs

Make a config.properties file, based on config.example.properties.

Example:

# REST Countries API
api.base.url=https://api.restcountries.com/countries/v5
api.key=YOUR_API_KEY_HERE

# Fish API
fish.api.url=https://marcconrad.com/uob/fish/api.php?out=csv&base64=yes

# Bonus Configuration
bonus.enabled=true
bonus.every=5
bonus.points=20

If necessary, substitute the API key with the appropriate credentials.

Note: Be sure not to include actual API keys, passwords, or other sensitive information in GitHub.

Step 5 – Set the Application's Startup Mode

Open:

src/com/worldexplorer/peripherals/LoginGUI.java

Right click on the file and click on:

Run 'LoginGUI.main()'

The World Explorer login screen should display.

🎮 How to Play

Step 1 – Login

Start the application and provide login information.

Choose a Game Mode.Step 2 – Choose a Game Mode.

Select one of the choices below:

Guess the Country.

Guess the Capital.

Identify the Flag.

Step 3 – Answer Questions

Read the question and guess what the right answer is before the time runs out.

Step 4 – Earn Points

Collect points as correct answers, and advance to levels.

Step 5 – Bonus round (bonus round)

The game can include a Fish Bonus Round after each 5 regular questions.

Look at the picture of fish and choose the appropriate number.

Step 6 – View Results

Once you have finished the Quiz, check out your score, accuracy and level.

To check the Leaderboard, refer to Step 7

Let's see how you're doing compared to other players.

🔌 External API Integration

REST Countries API

Geographical information is obtained from the REST Countries API and used to create quiz questions.

The Applications runs the information of country names, capital cities, country codes, flag images etc.

Fish API

Fish API includes images of fish and their respective quantities.

The application fetches the image, decodes the Base64 encoded response and creates a bonus question.

Error Handling

There is error-handling for failed API calls, invalid API responses, unavailable API services and failed image loading in the application.

The bonus round is optional and may not be played if the Fish API is not available, but not a game terminator.

🧪 Testing

World Explorer has individual test classes for testing various modules.

Test Class

Testing Purpose

ConfigTest.java

Configuration loading

HttpSupportTest.java

HTTP requests

JsonParserTest.java

JSON parsing

GameServerTest.java

Country question generation

FishServerTest.java

Fish API integration

GameEngineTest.java

Game logic and scoring

UITest.java

Reusable UI components

LoginScreenTest.java

Login screen

ModeSelectScreenTest.java

Game mode selection

QuizScreenTest.java

Quiz interface

BonusScreenTest.java

Bonus interface

ResultsScreenTest.java

Results and leaderboard

Each test can be run separately using IntelliJ IDEA.

🧩 Object-Oriented Programming Concepts

The work of this project illustrates a number of key software engineering concepts.

Abstraction: Methods represent a way to access classes' data.

Abstractions: Interfaces specify duties (responsibilities) without revealing implementation details.

Polymorphism: Multiple alternative implementations can be used via a common interface.

Low Coupling: All authentication, API communication, game logic and UI components exist as separate modules.

High Cohesion: Each class has a particular responsibility.

Event-Driven Programming: Java Swing listeners respond to button events, answer selections and navigation events.

Interoperability: Application is able to communicate with external REST services through HTTP and JSON.

🚀 Future Improvements

Potential improvements include:

More categories on Geography Quiz.

Difficulty selection.

More advanced player statistics.

Achievements and badges.

Audio effects and graphics.

Online multiplayer support.

Cloud-based leaderboards.

Improved offline support.

A more detailed automated testing.

Creating an executable JAR of the application.

🎓 Academic Purpose

World Explorer was a project created to showcase real-world experience in software engineering, graphical user interface, object oriented design, REST API integration and Java programming at the university level.

The project integrates several external services all in one desktop application and shows an example of how the components communicate through well-defined interfaces.

📄 License

The improvements in this project are made for educational use.

The use of any third party API and/or resources is subject to their own terms and conditions.

World Explorer – Interactive Learning and Quizzing of Geography!
