<a name="readme-top"></a>

# MazeRunner
By '**Control Alt Elite**' - Andrew Denegar, Molly O'Connor, Nick Clouse

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#playing-the-game">Playing The Game</a>
      <ul>
        <li><a href="#controls">Controls</a></li>
        <li><a href="#game-explanation">Game Explanation</a></li>
      </ul>
    </li>
    <li><a href="#future-opportunities">Future Opportunities</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

MazeRunner is a gripping puzzle/action game for teens or young adults that blends simplicity and depth resulting in a suspenseful and strategic gaming experience. 
The aim of the game is to escape all three levels of the maze as quickly as you can, while killing as many enemies as possible!

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

[![Java][java-img]][java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

* **IDE**: Any IDE of your choosing, although we recommend Eclipse for optimal compatibility.
* **Java**: Ensure Java is installed on your device. The project is compatible with Java version 17 or higher (Not tested with older versions).

### Installation
The following folders are needed to run the game:
  * [**data**](data/) - Stores each level.
  * [**images**](images/) - Player sprite textures and screen backgrounds.
  * [**leaderboards**](leaderboards/) - Leaderboard data.
  * [**src**](src/) - 5 packages, which contain the java files.
  * [**documents**](documents/) - Documents for our game, mostly JavaDoc.
      * To read our JavaDoc, download this folder and open [**index.html**](documents/doc/index.html) in a web browser.
1. Clone the repo
   ```sh
   git clone https://github.com/CanFam23/MazeRunner.git
   ```
3. In your IDE, all folders should be in a project called 'MazeRunner,' and the 'src' folder shold be the source folder.
4. To play MazeRunner, run the [**Main.java**](src/main/Main.java) file in the package [**main**](src/main) in the [**src**](src/) folder.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Playing the Game -->
## Playing the Game

### Controls
* **Up Arrow key**: Move up
* **Down Arrow key**: Move down
* **Right Arrow key**: Move right
* **Left Arrow key**: Move left
* **Spacebar**: Attack

### Game Explanation
<p> The objective is to navigate through three challenging levels of mazes, find the exit, and escape before the time runs out.  </p>
<p></p>Once a player starts the game, they are directed to level 1. Each category of level (1,2,3) has 5 versions. Each time a level is started the player will be loaded on the “start block” and have to navigate the maze until they find the “end block”. There are a total of 3 levels, with each level increasing in difficulty.  Level 1 has the square dimension of 20x20 blocks. Level 2 is 30x30, and the final level is 40x40 blocks big. As the player progresses through the game, the maze increases in size, and each level has more enemies than the last. </p>

<p>For each level the player has 2 minutes to complete the maze. The time will not start until the player presses an arrow key and a player movement is detected. After the time starts the player can see there remaining time in the top left corner of the screen, as well as the number of enemies the user has killed. When the user kills an enemy, they get a extra 15 seconds to try and find the end of the maze.</p>

<p>If the player makes it through three mazes without ever running out of time, they have won! After they reach the end of the final level, their time and the amount of enemies they kill will be calculated and turned into a final score. </p>

<p>The users score is based off of how much time they have left when they find the end of each maze. Killing enemies adds time to the users time left to complete the maze, so killing more enemies will result in a higher score.</p> 
<p> <b>Example: If the player completes level 1 in 60 seconds and kills two enemies along the way, their total time left would be 60 + 15 + 15, or 90 seconds. So, their score for level 1 would be 90.</b></p> 
<p>The users scores from each level are added up if the user beats the game, and becomes their final score. If their final score is good enough, it will be added to the leaderboard! There is also has a leaderboard for level 1 and level 2 times!</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- Future Opportunities -->
## Future Opportunities

- [ ] Our collision methods could be more efficient, by using Java’s built in methods.
- [ ] Find a efficient way to incorparate the effect of decreasing invisibility into the game. Right now it happens when the player dies, but we originally wanted the players visibility to decrease as time went on.
- [ ] Implement more enemy sprites.
- [ ] Improve enemy movement, they can get stuck on walls pretty often.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing
If you have a suggestion that would make our game better, please fork the repo and create a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

* **Andrew Denegar** - adenegar@carroll.edu
* **Molly O'Connor** - moconnor@carroll.edu
* **Nick Clouse** - nclouse@carroll.edu

Project Link: [https://github.com/CanFam23/MazeRunner](https://github.com/CanFam23/MazeRunner)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
[java-url]:https://www.java.com/en/
[java-img]:https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
