Library added:
- com.android.Volley for handling get request to backend server


Contribution
Gao Keqian:
- Select 6 images (onClick, select image, if image already selected, click to deselect)
- Show 20 images in gridView layout

Kevin Aditya:
- store the 20 images from the url given
- progress bar running while the images downloaded (via broadcast and service)
- setup intent to submit the selected images to the game activity via button
- show the whole 20 images downloaded through grid view
- reset the whole pictures when the images from a new url are redownloaded
- countdown function before the game starts
- score board implementation integrated with SpringBoot Backend Server

Wang Dongyang:
- URL input to fetch images
- URL validation

Low Huai Zhong:
- retrieve intent extra of selected images array
- prepare an answer array and shuffle it
- prepare respective drawables for answer array
- show respective drawables when onItemClick
- set up game logic (e.g. match/mismatch)
- implement feature to close first 2 chosen card immediately when 3rd card is chosen
- fix bug when 3rd card chosen is one of the first 2
- implement feature to auto close first 2 chosen card after 3seconds delay using postdelay on main handler
- fixed bug where auto close feature closes wrong cards using removeCallbacksAndMessages
- implemented number of matches textView

Huan Xinyue:
- timer function (start, pause, resume, reset)
- reset the game function
- show the selected 12 images through grid view
- modify all layouts

Sharon Chien:

Li Rang:
- flipping animation, matching and mismatching sound effects
- set cards unclickable after pausing the game and clickable after restarting
- set Start button invisible during countdown period and visible when game starts
- set reset button unclickable after winning the game
- pause timer automatically after pressing main button and continue after returning 








