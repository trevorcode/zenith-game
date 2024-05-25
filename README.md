Made for the Spring Lisp Game Jam 2024

Zenith is a small physics puzzle game. Similar to fruit ninja combined with a matching game.

## How to play
Runes/stones will come flying into your screen, click and drag them towards the top of the screen to attach it to the ceiling.  Letting a rune fall will cost one life. 

Clicking on a rune will activate it, by activating a combo of runes, you will gain points.

The game will progressively get faster.

Try to get the highest score you can!

### Point Calculations
3x of the same rune type: 3 points

Selecting all 4 of the rune types: 5 points and 1 life

## About
This was made using Clojurescript and squint

All images made by me with the exception of the main background. This site was used to help create the pixel look: https://www.pixelicious.xyz/

All piano sound effects were played on a piano by my father, thank you very much!

Rock collision sound effects were sourced here: https://opengameart.org/content/54-casino-sound-effects-cards-dice-chips


## Github URL:

trevorcode/zenith-game: 2024 Lisp Game Jam Clojurescript Squint game (github.com)

## How to build

Since zenith uses clojurescript squint, we're basically on the javascript ecosystem.

Requirements: babashka, node, and npm.

Install
`npm install`

To run
`bb dev`