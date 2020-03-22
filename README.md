# jobotwar
A RobotWar - inspired programming game

jobotwar builds upon the concept of the classic Robot War programming game. See https://en.wikipedia.org/wiki/RobotWar for information
on the classic game.

The game takes place on a plane of 640x480 pixels, where up to twelve robots are fighting each other. Test your programming skills using either jobotwar's new state-based programming language, or a language that is very close to the one used by the classic Robot War game.

This screenshot shows jobotwar's V2 language:
![screenshot1](https://raw.githubusercontent.com/smackem/jobotwar/master/src/site/screenshot1.png "Screenshot1")

And here is some game play footage:
<p style="text-align: center">
<img src="https://raw.githubusercontent.com/smackem/jobotwar/master/src/site/gameplay1.gif" width="800px" height="600px" />
</p>

The game includes implementations of classic Robot Wars and crobots programs like Mover, Shooter and Corner, plus it adds many fresh designs as samples.

The game is written in Java 13, using ANTLR and JavaFX.

Here are some code samples to get started with programming robots:

```
// robot 'Bumblebee'

// main is the default state. programs start in this state
state main() {

    // randomly choose a new destination
    def destX = @random(40, 600)
    def destY = @random(40, 440)

    // print the new destination
    @log(destX)
    @log(destY)

    // set the speed
    @speed(destX - @x(), destY - @y())

    // and change state to 'move'
    yield move(destX, destY)
}

// the moving state. yields as soon as the destination has been reached.
state move(destX, destY) {

    // calculate distance from destination
    def dx = destX - @x()
    def dy = destY - @y()

    if sqrt(dx * dx + dy * dy) < 40 {
        // if distance if less than 40 pixels, change state to 'main'
        yield main()
    }
}
```

As you can see, the jobotwar language is state based. You define states by declaring code blocks, which are then executed repeatedly by the jobotwar runtime. You change to another state by using the keyword `yield`.
States can receive arguments, which cannot be changed while the program remains in this state (see `destX` and `destY` in the preceding sample).
Functions prefixed with `@` are robot methods, which manipulate or monitor the controlled robot.
